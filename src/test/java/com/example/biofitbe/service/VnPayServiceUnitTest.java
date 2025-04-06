package com.example.biofitbe.service;

import com.example.biofitbe.config.VnPayConfig;
import com.example.biofitbe.dto.PaymentRequest;
import com.example.biofitbe.dto.PaymentResponse;
import com.example.biofitbe.model.Payment;
import com.example.biofitbe.model.User;
import com.example.biofitbe.repository.PaymentRepository;
import com.example.biofitbe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VnPayServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VnPayConfig vnPayConfig;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private VnPayService vnPayService;

    private PaymentRequest paymentRequest;
    private User testUser;

    @BeforeEach
    public void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(1L);
        paymentRequest.setPlanType("MONTHLY");
        paymentRequest.setIpAddress("127.0.0.1");
        paymentRequest.setReturnUrl("https://example.com/return");

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    public void createPayment_WithMonthlyPlan_ShouldReturnCorrectAmount() throws Exception {
        // Arrange
        when(vnPayConfig.getVnpTerminalId()).thenReturn("TESTTERMINAL");
        when(vnPayConfig.getVnpPayUrl()).thenReturn("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        when(vnPayConfig.getVnpSecretKey()).thenReturn("TEST_SECRET_KEY");
        when(userRepository.findByUserId(1L)).thenReturn(Optional.of(testUser));

        // Use spy to mock the hmac generation
        VnPayService spyService = spy(vnPayService);
        doReturn("mocked_hash_value").when(spyService).hmacSHA512(anyString(), anyString());

        // Act
        PaymentResponse response = spyService.createPayment(paymentRequest);

        // Assert
        assertAll(
                () -> assertTrue(response.isSuccess(), "Response should be successful"),
                () -> assertNotNull(response.getPaymentUrl(), "Payment URL should not be null"),
                () -> assertTrue(response.getPaymentUrl().contains("vnp_Amount=10000000"),
                        "URL should contain correct amount (10000000)"),
                () -> assertTrue(response.getPaymentUrl().contains("BioFit+MONTHLY+Subscription"),
                        "URL should contain correct order info"),
                () -> assertTrue(response.getPaymentUrl().contains("vnp_SecureHash=mocked_hash_value"),
                        "URL should contain mocked hash value")
        );
    }

    @Test
    public void createPayment_WithYearlyPlan_ShouldReturnCorrectAmount() throws Exception {
        // Arrange
        paymentRequest.setPlanType("YEARLY");
        when(vnPayConfig.getVnpTerminalId()).thenReturn("TESTTERMINAL");
        when(vnPayConfig.getVnpPayUrl()).thenReturn("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        when(vnPayConfig.getVnpSecretKey()).thenReturn("TEST_SECRET_KEY");
        when(userRepository.findByUserId(1L)).thenReturn(Optional.of(testUser));

        // Use spy to mock the hmac generation
        VnPayService spyService = spy(vnPayService);
        doReturn("mocked_hash_value").when(spyService).hmacSHA512(anyString(), anyString());

        // Act
        PaymentResponse response = spyService.createPayment(paymentRequest);

        // Assert
        assertAll(
                () -> assertTrue(response.isSuccess(), "Response should be successful"),
                () -> assertNotNull(response.getPaymentUrl(), "Payment URL should not be null"),
                () -> assertTrue(response.getPaymentUrl().contains("vnp_Amount=30000000"),
                        "URL should contain correct amount (30000000)"),
                () -> assertTrue(response.getPaymentUrl().contains("BioFit+YEARLY+Subscription"),
                        "URL should contain correct order info"),
                () -> assertTrue(response.getPaymentUrl().contains("vnp_SecureHash=mocked_hash_value"),
                        "URL should contain mocked hash value")
        );
    }

    @Test
    public void createPayment_WithInvalidUser_ShouldReturnError() throws Exception {
        // Arrange
        when(userRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(vnPayConfig.getVnpSecretKey()).thenReturn("TEST_SECRET_KEY"); // Add this to prevent NPE

        // Act
        PaymentResponse response = vnPayService.createPayment(paymentRequest);

        // Assert
        assertAll(
                () -> assertFalse(response.isSuccess(), "Response should not be successful"),
                () -> assertTrue(response.getMessage().contains("User not found"),
                        "Error message should indicate user not found"),
                () -> assertNull(response.getPaymentUrl(), "Payment URL should be null")
        );
    }

    @Test
    public void createPayment_WithDefaultReturnUrl_ShouldUseConfigUrl() throws Exception {
        // Arrange
        paymentRequest.setReturnUrl(null);
        when(vnPayConfig.getVnpTerminalId()).thenReturn("TESTTERMINAL");
        when(vnPayConfig.getVnpPayUrl()).thenReturn("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        when(vnPayConfig.getVnpReturnUrl()).thenReturn("https://default-return-url.com");
        when(vnPayConfig.getVnpSecretKey()).thenReturn("TEST_SECRET_KEY");
        when(userRepository.findByUserId(1L)).thenReturn(Optional.of(testUser));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PaymentResponse response = vnPayService.createPayment(paymentRequest);

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getPaymentUrl().contains("vnp_ReturnUrl=https%3A%2F%2Fdefault-return-url.com"));
    }

    @Test
    public void validatePaymentCallback_WithValidHash_ShouldReturnTrue() throws Exception {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Amount", "10000000");
        params.put("vnp_BankCode", "NCB");
        params.put("vnp_OrderInfo", "BioFit MONTHLY Subscription");
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_SecureHash", "valid_hash");

        when(vnPayConfig.getVnpSecretKey()).thenReturn("TEST_SECRET_KEY");

        // Mock the hmacSHA512 method to return a known value
        VnPayService spyService = spy(vnPayService);
        doReturn("valid_hash").when(spyService).hmacSHA512(anyString(), anyString());

        // Act
        boolean isValid = spyService.validatePaymentCallback(params);

        // Assert
        assertTrue(isValid);
    }

    @Test
    public void validatePaymentCallback_WithInvalidHash_ShouldReturnFalse() throws Exception {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Amount", "10000000");
        params.put("vnp_BankCode", "NCB");
        params.put("vnp_OrderInfo", "BioFit MONTHLY Subscription");
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_SecureHash", "invalid_hash");

        when(vnPayConfig.getVnpSecretKey()).thenReturn("TEST_SECRET_KEY");

        // Mock the hmacSHA512 method to return a different value
        VnPayService spyService = spy(vnPayService);
        doReturn("valid_hash").when(spyService).hmacSHA512(anyString(), anyString());

        // Act
        boolean isValid = spyService.validatePaymentCallback(params);

        // Assert
        assertFalse(isValid);
    }

    @Test
    public void updatePaymentStatus_WithExistingOrder_ShouldUpdateStatus() throws Exception {
        // Arrange
        String orderId = "BF20230501120000";
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentStatus("PENDING");

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        vnPayService.updatePaymentStatus(orderId, "COMPLETED");

        // Assert
        assertEquals("COMPLETED", payment.getPaymentStatus());
        assertNotNull(payment.getPaidAt());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    public void updatePaymentStatus_WithNonExistingOrder_ShouldDoNothing() throws Exception {
        // Arrange
        String orderId = "NON_EXISTING_ORDER";
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        // Act
        vnPayService.updatePaymentStatus(orderId, "COMPLETED");

        // Assert
        verify(paymentRepository, never()).save(any());
    }

    @Test
    public void hmacSHA512_ShouldReturnCorrectHash() throws Exception {
        // Arrange
        String key = "TEST_KEY";
        String data = "TEST_DATA";

        // Act
        String hash = vnPayService.hmacSHA512(key, data);

        // Assert
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
        // Note: For a real test, you might want to compare with a known hash value
    }

    @Test
    public void bytesToHex_ShouldConvertCorrectly() throws Exception {
        // Arrange
        byte[] bytes = new byte[] { (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78 };

        // Act
        String hex = vnPayService.bytesToHex(bytes);

        // Assert
        assertEquals("12345678", hex);
    }
}