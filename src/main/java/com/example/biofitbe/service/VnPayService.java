package com.example.biofitbe.service;

import com.example.biofitbe.config.VnPayConfig;
import com.example.biofitbe.dto.PaymentRequest;
import com.example.biofitbe.dto.PaymentResponse;
import com.example.biofitbe.model.Payment;
import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.PaymentRepository;
import com.example.biofitbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VnPayService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VnPayConfig vnPayConfig;

    @Autowired
    private PaymentRepository paymentRepository;

    // tạo URL thanh toán
    public PaymentResponse createPayment(PaymentRequest request) {
        try {
            String vnpVersion = "2.1.0";
            String vnpCommand = "pay";
            String vnpTmnCode = vnPayConfig.getVnpTerminalId();

            // Nhận số tiền dựa trên subscription
            BigDecimal amount;
            if ("YEARLY".equals(request.getPlanType())) {
                amount = new BigDecimal("300000"); // 300,000 VND
            } else {
                amount = new BigDecimal("100000"); // 100,000 VND
            }

            // Tạo orderId với tiền tố để theo dõi tốt hơn
            String orderId = "BF" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            // Tạo định dạng ngày giao dịch: yyyyMMddHHmmss
            String vnpCreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            // Lấy địa chỉ IP một cách chính xác hơn
            String ipAddr = request.getIpAddress();
            if (ipAddr == null || ipAddr.isEmpty()) {
                ipAddr = "localhost"; // IP mặc định nếu không có
            }

            // Đảm bảo returnUrl hợp lệ
            String returnUrl;
            if (request.getReturnUrl() != null && !request.getReturnUrl().isEmpty()) {
                returnUrl = request.getReturnUrl();
            } else {
                returnUrl = vnPayConfig.getVnpReturnUrl();
            }

            // Xây dựng tham số VNPay
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", vnpVersion);
            vnpParams.put("vnp_Command", vnpCommand);
            vnpParams.put("vnp_TmnCode", vnpTmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(amount.multiply(new BigDecimal("100")).intValue())); // Chuyển đổi sang VND * 100
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_BankCode", ""); // Cho phép người dùng chọn ngân hàng
            vnpParams.put("vnp_TxnRef", orderId);
            vnpParams.put("vnp_OrderInfo", "BioFit " + request.getPlanType() + " Subscription");
            vnpParams.put("vnp_OrderType", "250000"); // Mã loại hàng hóa: dịch vụ
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_IpAddr", ipAddr);
            vnpParams.put("vnp_CreateDate", vnpCreateDate);

            // Sắp xếp các tham số theo khóa
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);

            // Xây dựng dữ liệu băm và truy vấn
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnpParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Xây dựng dữ liệu băm
                    hashData.append(fieldName);
                    hashData.append('=');
                    try {
                        hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    } catch (UnsupportedEncodingException e) {
                        hashData.append(fieldValue);
                    }

                    // Xây dựng dữ liệu tham số
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            // Tạo hàm băm an toàn
            String vnpSecureHash = hmacSHA512(vnPayConfig.getVnpSecretKey(), hashData.toString());
            query.append("&vnp_SecureHash=").append(vnpSecureHash);

            // Xây dựng URL thanh toán
            String paymentUrl = vnPayConfig.getVnpPayUrl() + "?" + query;

            User user = userRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Lưu thông tin thanh toán vào db
            Payment payment = Payment.builder()
                    .user(user)
                    .orderId(orderId)
                    .amount(amount)
                    .planType(request.getPlanType())
                    .paymentMethod("VNPAY")
                    .paymentStatus("PENDING")
                    .build();

            paymentRepository.save(payment);

            return new PaymentResponse(true, "Payment URL created successfully", paymentUrl, orderId);

        } catch (Exception e) {
            e.printStackTrace();
            return new PaymentResponse(false, "Error creating payment URL: " + e.getMessage(), null, null);
        }
    }

    // Xử lý lệnh gọi lại thanh toán từ VNPay
    public boolean validatePaymentCallback(Map<String, String> params) {
        // Xóa vnp_SecureHashType và vnp_SecureHash khỏi params
        String vnpSecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHashType");
        params.remove("vnp_SecureHash");

        // Sắp xếp các tham số
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        // Xây dựng dữ liệu băm
        StringBuilder hashData = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                } catch (Exception e) {
                    hashData.append(fieldValue);
                }

                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

        String calculatedHash = hmacSHA512(vnPayConfig.getVnpSecretKey(), hashData.toString());

        // So sánh giá trị băm được tính toán với giá trị băm nhận được
        return calculatedHash.equals(vnpSecureHash);
    }

    public void updatePaymentStatus(String orderId, String status) {
        Optional<Payment> optPayment = paymentRepository.findByOrderId(orderId);
        if (optPayment.isPresent()) {
            Payment payment = optPayment.get();
            payment.setPaymentStatus(status);
            if ("COMPLETED".equals(status)) {
                payment.setPaidAt(LocalDateTime.now());
            }
            paymentRepository.save(payment);

            // Thêm log để kiểm tra
            System.out.println("Payment status updated: " + payment.getPaymentStatus());
        }
    }

    // Thuật toán HMAC_SHA512 để tạo băm an toàn
    private String hmacSHA512(String key, String data) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            sha512_HMAC.init(secretKeySpec);
            byte[] hash = sha512_HMAC.doFinal(data.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}