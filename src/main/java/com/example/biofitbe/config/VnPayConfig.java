package com.example.biofitbe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VnPayConfig {
    @Value("${vnPay.tmnCode}")
    private String vnpTerminalId;

    @Value("${vnPay.hashSecret}")
    private String vnpSecretKey;

    @Value("${vnPay.payUrl}")
    private String vnpPayUrl;

    @Value("${vnPay.returnUrl}")
    private String vnpReturnUrl;

    @Value("${vnPay.apiUrl}")
    private String vnpApiUrl;

    // Thêm biến cho ipnUrl
    @Value("${vnPay.ipnUrl}")
    private String vnpIpnUrl;

    // Getters (giữ nguyên các getters hiện có)
    public String getVnpTerminalId() {
        return vnpTerminalId;
    }

    public String getVnpSecretKey() {
        return vnpSecretKey;
    }

    public String getVnpPayUrl() {
        return vnpPayUrl;
    }

    public String getVnpReturnUrl() {
        return vnpReturnUrl;
    }

    public String getVnpApiUrl() {
        return vnpApiUrl;
    }

    public String getVnpIpnUrl() {
        return vnpIpnUrl;
    }
}