package com.example.biofitbe.controller;

import com.example.biofitbe.dto.PaymentRequest;
import com.example.biofitbe.dto.PaymentResponse;
import com.example.biofitbe.repository.PaymentRepository;
import com.example.biofitbe.repository.SubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentRepository paymentRepository;

    @MockitoBean
    private SubscriptionRepository subscriptionRepository;

    @MockitoBean
    private com.example.biofitbe.service.VnPayService vnPayService;

    @MockitoBean
    private com.example.biofitbe.service.MoMoService moMoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreatePayment_VnPay() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPlanType("MONTHLY");
        paymentRequest.setPaymentMethod("VNPAY");
        paymentRequest.setReturnUrl("biofit://payment/callback");
        paymentRequest.setUserId(1L);

        // Giả lập service trả về response
        Mockito.when(vnPayService.createPayment(Mockito.any()))
                .thenReturn(new PaymentResponse(true, "Payment created", "http://pay.vnpay.vn", "txn123"));

        mockMvc.perform(post("/api/payment/create-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Payment created"))
                .andExpect(jsonPath("$.paymentUrl").value("http://pay.vnpay.vn"))
                .andExpect(jsonPath("$.orderId").value("txn123"));
    }

    @Test
    void testVnPayReturn_Success() throws Exception {
        Mockito.when(vnPayService.validatePaymentCallback(Mockito.anyMap())).thenReturn(true);

        mockMvc.perform(get("/api/payment/vnPay-return")
                        .param("vnp_TxnRef", "txn123")
                        .param("vnp_ResponseCode", "00"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", containsString("biofit://payment/callback?vnp_ResponseCode=00")));
    }

    @Test
    void testCreatePayment_MoMo() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setPlanType("YEARLY");
        request.setPaymentMethod("MOMO");
        request.setReturnUrl("biofit://payment/callback");
        request.setUserId(2L);

        Mockito.when(moMoService.createPayment(Mockito.any()))
                .thenReturn(new PaymentResponse(true, "MoMo created", "http://momo.vn/pay", "momo456"));

        mockMvc.perform(post("/api/payment/create-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.paymentUrl").value("http://momo.vn/pay"))
                .andExpect(jsonPath("$.orderId").value("momo456"));
    }

    @Test
    void testMomoReturn_Success() throws Exception {
        Mockito.when(moMoService.validatePaymentCallback(Mockito.anyMap())).thenReturn(true);

        mockMvc.perform(get("/api/payment/momo-return")
                        .param("orderId", "momo123")
                        .param("resultCode", "0"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", containsString("biofit://payment/callback?resultCode=0")));
    }

    @Test
    void testVnPayIpn_Success() throws Exception {
        Mockito.when(vnPayService.validatePaymentCallback(Mockito.anyMap())).thenReturn(true);

        mockMvc.perform(post("/api/payment/vnPay-ipn")
                        .param("vnp_TxnRef", "txn123")
                        .param("vnp_ResponseCode", "00")
                        .param("vnp_TransactionStatus", "00"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Confirm Success")));
    }

    @Test
    void testMomoNotify_Success() throws Exception {
        Mockito.when(moMoService.validatePaymentCallback(Mockito.anyMap())).thenReturn(true);

        mockMvc.perform(post("/api/payment/momo-notify")
                        .param("orderId", "momo123")
                        .param("resultCode", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Success")));
    }
}
