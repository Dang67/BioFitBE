package com.example.biofitbe.controller;

import com.example.biofitbe.dto.PaymentRequest;
import com.example.biofitbe.dto.PaymentResponse;
import com.example.biofitbe.model.Payment;
import com.example.biofitbe.model.Subscription;
import com.example.biofitbe.repository.PaymentRepository;
import com.example.biofitbe.repository.SubscriptionRepository;
import com.example.biofitbe.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // Trích xuất các tham số thành map, dùng để lấy dữ liệu từ URL callback của VNPAY
    private Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            params.put(paramName, request.getParameter(paramName));
        }
        return params;
    }

    // Tạo thanh toán
    @PostMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request, HttpServletRequest httpRequest) {
        // Lấy địa chỉ IP của khách hàng để truyền vào request
        String ipAddress = httpRequest.getRemoteAddr();
        request.setIpAddress(ipAddress);

        PaymentResponse response;
        if ("VNPAY".equals(request.getPaymentMethod())) {
            response = vnPayService.createPayment(request);
        } else {
            // Để triển khai MOMO trong tương lai.
            response = new PaymentResponse(false, "Payment method not supported yet", null, null);
        }

        return ResponseEntity.ok(response);
    }

    // Xử lý phản hồi từ VNPAY
    @GetMapping("/vnPay-return")
    public ResponseEntity<String> vnpayReturn(HttpServletRequest request) {
        Map<String, String> params = extractParams(request);

        String vnpTxnRef = params.get("vnp_TxnRef"); // mã giao dịch
        String vnpResponseCode = params.get("vnp_ResponseCode"); // mã phản hồi

        // Xác thực chữ ký giao dịch
        if (vnPayService.validatePaymentCallback(params)) {
            if ("00".equals(vnpResponseCode)) {
                // Thanh toán thành công, cập nhật trạng thái thanh toán và tạo đăng ký
                vnPayService.updatePaymentStatus(vnpTxnRef, "COMPLETED");
                createSubscription(vnpTxnRef);

                // Chuyển hướng về ứng dụng với các tham số đăng kí thành công
                String redirectUrl = "biofit://payment/callback?vnp_ResponseCode=" + vnpResponseCode +
                        "&vnp_TxnRef=" + vnpTxnRef;

                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", redirectUrl)
                        .build();
            } else {
                // Thanh toán lỗi
                vnPayService.updatePaymentStatus(vnpTxnRef, "FAILED");

                // Chuyển hướng về ứng dụng với thông báo lỗi
                String redirectUrl = "biofit://payment/callback?vnp_ResponseCode=" + vnpResponseCode +
                        "&vnp_TxnRef=" + vnpTxnRef;

                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", redirectUrl)
                        .build();
            }
        } else {
            // Chữ ký không hợp lệ
            String redirectUrl = "biofit://payment/callback?error=invalid_signature";

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }
    }

    // Thêm endpoint mới để xử lý IPN của VNPay
    @PostMapping("/vnPay-ipn")
    public ResponseEntity<String> vnpayIpn(HttpServletRequest request) {
        Map<String, String> params = extractParams(request);

        String vnpTxnRef = params.get("vnp_TxnRef");
        String vnpResponseCode = params.get("vnp_ResponseCode");
        String vnpTransactionStatus = params.get("vnp_TransactionStatus");

        if (vnPayService.validatePaymentCallback(params)) {
            if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
                vnPayService.updatePaymentStatus(vnpTxnRef, "COMPLETED");
                createSubscription(vnpTxnRef);
                return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
            } else {
                vnPayService.updatePaymentStatus(vnpTxnRef, "FAILED");
                return ResponseEntity.ok("{\"RspCode\":\"99\",\"Message\":\"Payment Failed\"}");
            }
        } else {
            return ResponseEntity.ok("{\"RspCode\":\"97\",\"Message\":\"Invalid Signature\"}");
        }
    }

    // Tạo Subscription sau khi thanh toán thành công
    private void createSubscription(String orderId) {
        Optional<Payment> optPayment = paymentRepository.findByOrderId(orderId);
        if (optPayment.isPresent()) {
            Payment payment = optPayment.get();

            // Đảm bảo cập nhật payment status
            payment.setPaymentStatus("COMPLETED");
            paymentRepository.save(payment);

            // Tính toán thời gian hết hạn (1 năm cho gói YEARLY, 1 tháng cho gói MONTHLY)
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate;

            if ("YEARLY".equals(payment.getPlanType())) {
                endDate = startDate.plusYears(1);
            } else {
                endDate = startDate.plusMonths(1);
            }

            // Tạo subscription
            Subscription subscription = Subscription.builder()
                    .userId(payment.getUser().getUserId())
                    .planType(payment.getPlanType())
                    .startDate(startDate)
                    .endDate(endDate)
                    .isActive(true)
                    .payment(payment)
                    .build();

            subscriptionRepository.save(subscription);
        }
    }
}	