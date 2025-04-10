package com.example.biofitbe.service;

import com.example.biofitbe.config.MoMoConfig;
import com.example.biofitbe.dto.PaymentRequest;
import com.example.biofitbe.dto.PaymentResponse;
import com.example.biofitbe.model.Payment;
import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MoMoService {
    @Autowired
    private MoMoConfig moMoConfig;

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentResponse createPayment(PaymentRequest request) throws Exception {
        String orderId = moMoConfig.getPartnerCode() + System.currentTimeMillis();
        String requestId = orderId;
        long amount = "YEARLY".equals(request.getPlanType()) ? 300000L : 100000L;
//        String orderInfo = "Thanh toán gói " + request.getPlanType();
//        String extraData = "";

        // Tạo dữ liệu yêu cầu MoMo
        Map<String, Object> rawData = new HashMap<>();
        rawData.put("partnerCode", moMoConfig.getPartnerCode());
        rawData.put("accessKey", moMoConfig.getAccessKey());
        rawData.put("requestId", requestId);
        rawData.put("amount", String.valueOf(amount));
        rawData.put("orderId", orderId);
        rawData.put("orderInfo", "Thanh toán gói " + request.getPlanType());
        rawData.put("redirectUrl", "biofit://payment/callback");
        rawData.put("ipnUrl", moMoConfig.getIpnUrl());
        rawData.put("extraData", "");
        rawData.put("requestType", "captureWallet"); // Sử dụng captureWallet
        rawData.put("lang", "vi");

        // Tạo chữ ký (signature)
        String rawSignature = "accessKey=" + moMoConfig.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + "" +
                "&ipnUrl=" + moMoConfig.getIpnUrl() +
                "&orderId=" + orderId +
                "&orderInfo=" + "Thanh toán gói " + request.getPlanType() +
                "&partnerCode=" + moMoConfig.getPartnerCode() +
                "&redirectUrl=" + "biofit://payment/callback" +
                "&requestId=" + requestId +
                "&requestType=captureWallet";
        String signature = hmacSHA256(moMoConfig.getSecretKey(), rawSignature);
        rawData.put("signature", signature);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(rawData);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(moMoConfig.getApiEndpoint()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);

        // Lưu thông tin thanh toán
        Payment payment = Payment.builder()
                .user(Payment.builder().user(User.builder().userId(request.getUserId()).build()).build().getUser())
                .orderId(orderId)
                .amount(new BigDecimal(amount))
                .planType(request.getPlanType())
                .paymentMethod("MOMO")
                .paymentStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        // Trả về payUrl
        // Ưu tiên trả về deeplink, nếu không có thì dùng payUrl
        String deeplink = (String) responseBody.get("deeplink");
        if (deeplink == null || deeplink.isEmpty()) {
            deeplink = (String) responseBody.get("payUrl"); // Fallback
            if (deeplink == null || deeplink.isEmpty()) {
                throw new Exception("Không nhận được deeplink hoặc payUrl từ MoMo");
            }
        }
        return new PaymentResponse(true, "Payment created successfully", deeplink, orderId);
    }

    public boolean validatePaymentCallback(Map<String, String> params) throws Exception {
        System.out.println("MoMo callback params: " + params);
        String signature = params.get("signature");
        if (signature == null) {
            System.out.println("Signature is missing in callback params");
            return false;
        }

        String data = "accessKey=" + moMoConfig.getAccessKey() +
                "&amount=" + params.get("amount") +
                "&extraData=" + params.get("extraData") +
                "&message=" + params.get("message") +
                "&orderId=" + params.get("orderId") +
                "&orderInfo=" + params.get("orderInfo") +
                "&orderType=" + params.get("orderType") +
                "&partnerCode=" + params.get("partnerCode") +
                "&payType=" + params.get("payType") +
                "&requestId=" + params.get("requestId") +
                "&responseTime=" + params.get("responseTime") +
                "&resultCode=" + params.get("resultCode") +
                "&transId=" + params.get("transId");

        String computedSignature = hmacSHA256(moMoConfig.getSecretKey(), data);
        System.out.println("Computed signature: " + computedSignature);
        System.out.println("Received signature: " + signature);
        return signature.equals(computedSignature);
    }

    public void updatePaymentStatus(String orderId, String status) {
        paymentRepository.findByOrderId(orderId).ifPresent(payment -> {
            payment.setPaymentStatus(status);
            if ("COMPLETED".equals(status)) {
                payment.setPaidAt(LocalDateTime.now());
            }
            paymentRepository.save(payment);
            // Thêm log để kiểm tra
            System.out.println("Payment status updated: " + payment.getPaymentStatus());
        });
    }

    private String hmacSHA256(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(rawHmac);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private String sendHttpRequest(String url, String jsonRequest) throws Exception {
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(jsonRequest, headers);
        return restTemplate.postForObject(url, entity, String.class);
    }
}