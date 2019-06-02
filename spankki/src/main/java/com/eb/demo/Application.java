package com.eb.demo;

import com.enablebanking.ApiClient;
import com.enablebanking.api.AispApi;
import com.enablebanking.api.AuthApi;
import com.enablebanking.api.MetaApi;
import com.enablebanking.model.AccountResource;
import com.enablebanking.model.Auth;
import com.enablebanking.model.Connector;
import com.enablebanking.model.Consent;
import com.enablebanking.model.HalAccounts;
import com.enablebanking.model.HalBalances;
import com.enablebanking.model.HalConnectors;
import com.enablebanking.model.HalTransactions;
import com.enablebanking.model.Token;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {

    public static void main(String[] args) {
        String bankName = "SPankki";
        String country = "FI";

        MetaApi metaApi = new MetaApi(new ApiClient());
        HalConnectors connectors = metaApi.getConnectors(country);

        Connector connector = null;
        for (Connector conn : connectors.getConnectors()) {
            if (conn.getName().equals(bankName)) {
                connector = conn;
                break;
            }
        }
        println(connector);

        String clientId = "QuaLy4hsXsoBvYCPOsapwv5dV9dkfw_qeWImvqZYh68";

        List<Object> settings = Arrays.asList(
                true, //sandbox
                clientId,
                "cf620ad4-1a5b-4d3c-9e38-dd111ba0a3ad", //xApiKey
                "req_cert.crt", //certPath
                "req_key.pem", //keyPath
                "sign_private.pem", //signPrivateKey
                null, //accessToken
                null, //refreshToken
                null //consentId
        );

        ApiClient apiClient = new ApiClient(bankName, settings);
        AuthApi authApi = new AuthApi(apiClient);

        String redirectUri = "https://enablebanking.com";

        Auth auth = authApi.getAuth(
                "code", //responseType
                redirectUri,
                connector.getScopes(), //scopes
                clientId,
                "test" //state
        );
        println(auth);

        //a human should go to auth.getUrl() page, input credentials and then got redirected
        //tokens should be extracted from redirected URI query parameters

        Map<String, String> parsedQueryParams = new HashMap<>();

        Token token = authApi.makeToken(
                "authorization_code", //grantType
                parsedQueryParams.get("code"), //code
                parsedQueryParams.get("id_token"), //idToken
                redirectUri);
        println(token);

        AispApi aispApi = new AispApi(apiClient);
        //apiClient has already accessToken and refreshToken applied after call to makeToken()
        HalAccounts accounts = aispApi.getAccounts();
        println(accounts);

        for (AccountResource account : accounts.getAccounts()) {
            HalBalances accountBalances = aispApi.getAccountBalances(account.getResourceId());
            println(accountBalances);
        }

        Consent currentConsent = aispApi.getCurrentConsent();
        println(currentConsent);

        for (AccountResource account : accounts.getAccounts()) {
            HalTransactions transactions = aispApi.getAccountTransactions(account.getResourceId(),
                    null, //dateFrom
                    null, //dateTo
                    null //afterEntryReference
            );
            println(transactions);
        }

    }

    private static void println(Object obj) {
        System.out.println(obj);
        System.out.println();
    }

}
