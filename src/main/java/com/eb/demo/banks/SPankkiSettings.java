package com.eb.demo.banks;

import com.eb.demo.BankSettings;

import java.util.Arrays;
import java.util.List;

public class SPankkiSettings implements BankSettings {

    @Override
    public List<Object> clientSettings() {
        return Arrays.asList(
                true, //sandbox
                "QuaLy4hsXsoBvYCPOsapwv5dV9dkfw_qeWImvqZYh68",
                "cf620ad4-1a5b-4d3c-9e38-dd111ba0a3ad", //xApiKey
                "SPankki/req_cert.crt", //certPath
                "SPankki/req_key.pem", //keyPath
                "SPankki/sign_private.pem", //signPrivateKey
                null, //accessToken
                null, //refreshToken
                null //consentId
        );
    }

    @Override
    public String bankName() {
        return "SPankki";
    }

    @Override
    public String redirectUri() {
        return "https://enablebanking.com";
    }

    @Override
    public boolean makeToken() {
        return true;
    }

}
