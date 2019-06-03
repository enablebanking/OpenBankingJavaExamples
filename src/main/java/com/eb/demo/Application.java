package com.eb.demo;

import com.eb.demo.banks.SPankkiSettings;
import com.enablebanking.ApiClient;
import com.enablebanking.api.AispApi;
import com.enablebanking.api.AuthApi;
import com.enablebanking.model.AccountResource;
import com.enablebanking.model.Auth;
import com.enablebanking.model.Consent;
import com.enablebanking.model.HalAccounts;
import com.enablebanking.model.HalBalances;
import com.enablebanking.model.HalTransactions;
import com.enablebanking.model.Token;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.util.Arrays.asList;

public class Application {

    public static void main(String[] args) {
        BankSettings settings = new SPankkiSettings();
        ApiClient apiClient = new ApiClient(settings.bankName(), settings.clientSettings());
        AuthApi authApi = new AuthApi(apiClient);

        Auth auth = authApi.getAuth(
                "code", //responseType
                settings.redirectUri(),
                asList("aisp"), //scopes
                null, //clientId
                "test" //state
        );
        println(auth);

        String redirectedUrl = blockReadRedirectedUrl(auth.getUrl(), settings.redirectUri());
        Map<String, String> parsedQueryParams = parseQueryParams(redirectedUrl, settings.redirectUri());

        Token token = authApi.makeToken(
                "authorization_code", //grantType
                parsedQueryParams.get("code"), //code
                parsedQueryParams.get("id_token"), //idToken
                settings.redirectUri());
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

            print("Account " + account.getResourceId() + " has " + transactions.getTransactions().size() + " transactions");
            if (transactions.getTransactions().size() > 0) {
                println("First: " + transactions.getTransactions().get(0));
            }
        }
    }

    private static String blockReadRedirectedUrl(String authUrl, String redirectUri) {
        print("Please, open this page in browser: " + authUrl);
        print("Login, authenticate and copy paste back the URL where you got redirected.");
        print("URL: (starts with " + redirectUri + "): ");


        Scanner scan = new Scanner(System.in);
        String redirectedUrl = scan.next();
        scan.close();

        println("");

        return redirectedUrl;
    }

    private static void print(Object obj) {
        System.out.println(obj);
    }

    private static void println(Object obj) {
        print(obj);
        System.out.println();
    }


    private static Map<String, String> parseQueryParams(String url, String redirectUri) {
        Map<String, String> result = new HashMap<String, String>();

        url = url.replace(redirectUri, "");
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        if (url.startsWith("#")) {
            url = url.substring(1);
        }

        String[] pairs = url.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                result.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                        URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
