package com.eb.demo.banks;

import com.eb.demo.BankSettings;

import java.util.Arrays;
import java.util.List;

public class LHVSettings implements BankSettings {

    @Override
    public List<Object> clientSettings() {
        return Arrays.asList(
                true, //sandbox
                "LHV/client.crt", //certPath
                "LHV/client.key", //keyPath
                "LHV/server.crt", //caCertPath
                "PSDEE-LHVTEST-01AAA", //clientId
                "127.0.0.1", //psuIpAddress
                "liismarimannik", //accessToken
                "520189d2-e8bb-48ae-8221-45bed93b7316", //consentId
                "https://enablebanking.com" //tppRedirectUri
        );
    }

    @Override
    public String bankName() {
        return "LHV";
    }

    @Override
    public String redirectUri() {
        return "https://enablebanking.com";
    }

    @Override
    public boolean makeToken() {
        return false;
    }

}
