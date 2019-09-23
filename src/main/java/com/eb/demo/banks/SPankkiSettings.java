package com.eb.demo.banks;

import com.eb.demo.BankSettings;

import java.util.Arrays;
import java.util.List;

public class SPankkiSettings implements BankSettings {
    @Override
    public List<Object> clientSettings() {
        return Arrays.asList(
                true, //sandbox
                "!!! CLIENT ID TO BE INSERTED HERE !!!", //clientId
                "!!! X API KEY TO BE INSERTED HERE !!!", //xApiKey
                "SPankki/req_cert.crt", //certPath
                "SPankki/req_key.pem", //keyPath
                "SPankki/sign_private.pem", //signPrivateKey
                "!!! QSEAL CERTIFICATE SERIAL HERE !!!", //signPubKeySerial
                "https://your.domain/path/to/handler", //paymentAuthRedirectUri
                "some-state-string", //paymentAuthState
                null, //accessToken
                null, //refreshToken
                null //consentId
        );
    }
}
