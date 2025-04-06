package com.example.biofitbe.service;

import com.example.biofitbe.config.MoMoConfig;
import com.example.biofitbe.dto.PaymentRequest;
import com.example.biofitbe.dto.PaymentResponse;
import com.example.biofitbe.model.Payment;
import com.example.biofitbe.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MoMoServiceUnitTest {
    @InjectMocks
    private MoMoService moMoService;

    @Mock
    private MoMoConfig moMoConfig;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePayment_Success() throws Exception {

        // Thiết lập cấu hình
        when(moMoConfig.getPartnerCode()).thenReturn("MOMO");
        when(moMoConfig.getAccessKey()).thenReturn("accessKey");
        when(moMoConfig.getSecretKey()).thenReturn("secretKey");
        when(moMoConfig.getApiEndpoint()).thenReturn("https://test-payment.momo.vn");
        when(moMoConfig.getIpnUrl()).thenReturn("https://callback.url");

        PaymentRequest request = new PaymentRequest();
        request.setPlanType("MONTHLY");
        request.setUserId(1L);

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("deeplink", "momo://payment");
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);

        // Sử dụng spy để mô phỏng phương thức sendHttpRequest
        MoMoService moMoServiceSpy = spy(moMoService);
        doReturn(jsonResponse).when(moMoServiceSpy).sendHttpRequest(anyString());

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = moMoServiceSpy.createPayment(request);

        assertTrue(response.isSuccess());
        assertEquals("Payment created successfully", response.getMessage());
        assertEquals("momo://payment", response.getPaymentUrl());
        assertNotNull(response.getOrderId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testCreatePayment_FallbackToPayUrl() throws Exception {

        // Thiết lập cấu hình
        when(moMoConfig.getPartnerCode()).thenReturn("MOMO");
        when(moMoConfig.getAccessKey()).thenReturn("accessKey");
        when(moMoConfig.getSecretKey()).thenReturn("secretKey");
        when(moMoConfig.getApiEndpoint()).thenReturn("https://test-payment.momo.vn");
        when(moMoConfig.getIpnUrl()).thenReturn("https://callback.url");

        PaymentRequest request = new PaymentRequest();
        request.setPlanType("YEARLY");
        request.setUserId(1L);

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("deeplink", "");
        mockResponse.put("payUrl", "https://pay.momo.vn");
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);

        MoMoService moMoServiceSpy = spy(moMoService);
        doReturn(jsonResponse).when(moMoServiceSpy).sendHttpRequest(anyString());

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = moMoServiceSpy.createPayment(request);

        assertTrue(response.isSuccess());
        assertEquals("https://pay.momo.vn", response.getPaymentUrl());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testCreatePayment_NoDeeplinkOrPayUrl_ThrowsException() throws Exception {

        when(moMoConfig.getPartnerCode()).thenReturn("MOMO");
        when(moMoConfig.getAccessKey()).thenReturn("accessKey");
        when(moMoConfig.getSecretKey()).thenReturn("secretKey");
        when(moMoConfig.getApiEndpoint()).thenReturn("https://test-payment.momo.vn");
        when(moMoConfig.getIpnUrl()).thenReturn("https://callback.url");

        PaymentRequest request = new PaymentRequest();
        request.setPlanType("MONTHLY");
        request.setUserId(1L);

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("deeplink", "");
        mockResponse.put("payUrl", "");
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);

        MoMoService moMoServiceSpy = spy(moMoService);
        doReturn(jsonResponse).when(moMoServiceSpy).sendHttpRequest(anyString());

        Exception exception = assertThrows(Exception.class, () -> {
            moMoServiceSpy.createPayment(request);
        });
        assertEquals("Không nhận được deeplink hoặc payUrl từ MoMo", exception.getMessage());
    }

    @Test
    void testValidatePaymentCallback_Success() throws Exception {

        when(moMoConfig.getAccessKey()).thenReturn("accessKey");
        when(moMoConfig.getSecretKey()).thenReturn("secretKey");

        Map<String, String> params = new HashMap<>();
        params.put("signature", "validSignature");
        params.put("amount", "100000");
        params.put("extraData", "");
        params.put("message", "Success");
        params.put("orderId", "MOMO123");
        params.put("orderInfo", "Thanh toán gói MONTHLY");
        params.put("orderType", "momo_wallet");
        params.put("partnerCode", "MOMO");
        params.put("payType", "qr");
        params.put("requestId", "MOMO123");
        params.put("responseTime", "123456789");
        params.put("resultCode", "0");
        params.put("transId", "trans123");

        String data = "accessKey=accessKey&amount=100000&extraData=&message=Success&orderId=MOMO123" +
                "&orderInfo=Thanh toán gói MONTHLY&orderType=momo_wallet&partnerCode=MOMO&payType=qr" +
                "&requestId=MOMO123&responseTime=123456789&resultCode=0&transId=trans123";
        String computedSignature = moMoService.hmacSHA256("secretKey", data);
        params.put("signature", computedSignature);

        boolean result = moMoService.validatePaymentCallback(params);

        assertTrue(result);
    }

    @Test
    void testValidatePaymentCallback_InvalidSignature() throws Exception {

        when(moMoConfig.getAccessKey()).thenReturn("accessKey");
        when(moMoConfig.getSecretKey()).thenReturn("secretKey");

        Map<String, String> params = new HashMap<>();
        params.put("signature", "invalidSignature");
        params.put("amount", "100000");

        boolean result = moMoService.validatePaymentCallback(params);

        assertFalse(result);
    }

    @Test
    void testUpdatePaymentStatus_Success() {

        String orderId = "MOMO123";
        Payment payment = Payment.builder()
                .orderId(orderId)
                .paymentStatus("PENDING")
                .build();

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));


        moMoService.updatePaymentStatus(orderId, "COMPLETED");


        assertEquals("COMPLETED", payment.getPaymentStatus());
        assertNotNull(payment.getPaidAt());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testHmacSHA256() throws Exception {

        String key = "secretKey";
        String data = "testData";
        String result = moMoService.hmacSHA256(key, data);

        assertNotNull(result);
        assertEquals(64, result.length()); // SHA-256 produces 64 hex characters
    }
}