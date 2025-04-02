package com.example.biofitbe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MoMoConfig {
    @Value("${momo.api-endpoint}")
    private String apiEndpoint;

    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.secret-key}")
    private String secretKey;

    @Value("${momo.redirect-url}")
    private String redirectUrl;

    @Value("${momo.ipn-url}")
    private String ipnUrl;

    @Value("${momo.partner-name}")
    private String partnerName;

    @Value("${momo.store-id}")
    private String storeId;

    @Value("${momo.lang}")
    private String lang;

    @Value("${momo.request-type}")
    private String requestType;

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getIpnUrl() {
        return ipnUrl;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getLang() {
        return lang;
    }

    public String getRequestType() {
        return requestType;
    }
}
