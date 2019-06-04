package com.eb.demo;

import com.eb.demo.banks.LHVSettings;
import com.eb.demo.banks.SPankkiSettings;
import com.enablebanking.ApiClient;
import com.enablebanking.ApiException;
import com.enablebanking.api.AispApi;
import com.enablebanking.api.AuthApi;
import com.enablebanking.model.AccountResource;
import com.enablebanking.model.Auth;
import com.enablebanking.model.Consent;
import com.enablebanking.model.HalAccounts;
import com.enablebanking.model.HalBalances;
import com.enablebanking.model.HalTransactions;
import com.enablebanking.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.eb.demo.DemoUtils.blockReadRedirectedUrl;
import static com.eb.demo.DemoUtils.parseQueryParams;
import static java.util.Arrays.asList;

public class EnableBankingDemo {
    private static final Logger log = LoggerFactory.getLogger(EnableBankingDemo.class);

    private static final Map<String, BankSettings> bankSettings = new HashMap<>();

    static {
        bankSettings.put("LHV", new LHVSettings());
        bankSettings.put("SPankki", new SPankkiSettings());
    }

    public static void main(String[] args) {
        BankSettings settings = bankSettings.get("LHV"); //one might choose another bank here

        ApiClient apiClient = new ApiClient(settings.bankName(), settings.clientSettings());
        AuthApi authApi = new AuthApi(apiClient);

        Auth auth = authApi.getAuth(
                "code", //responseType
                settings.redirectUri(),
                asList("aisp"), //scopes
                null, //clientId
                "test" //state
        );
        log.info("{}", auth);

        //for some banks there is already static token for sandbox
        if (settings.makeToken()) {
            String redirectedUrl = blockReadRedirectedUrl(auth.getUrl(), settings.redirectUri());
            Map<String, String> parsedQueryParams = parseQueryParams(redirectedUrl, settings.redirectUri());

            Token token = authApi.makeToken(
                    "authorization_code", //grantType
                    parsedQueryParams.get("code"), //code
                    parsedQueryParams.get("id_token"), //idToken
                    settings.redirectUri());
            log.info("{}", token);
        }


        AispApi aispApi = new AispApi(apiClient);
        //apiClient has already accessToken and refreshToken applied after call to makeToken()
        HalAccounts accounts = aispApi.getAccounts();
        log.info("{}", accounts);

        for (AccountResource account : accounts.getAccounts()) {
            try {
                HalBalances accountBalances = aispApi.getAccountBalances(account.getResourceId());
                log.info("{}", accountBalances);
            } catch (ApiException e) {
                log.error("{}: {}", account.getResourceId(), e.toString());
            }
        }

        Consent currentConsent = aispApi.getCurrentConsent();
        log.info("{}", currentConsent);

        for (AccountResource account : accounts.getAccounts()) {
            try {
                HalTransactions transactions = aispApi.getAccountTransactions(account.getResourceId(),
                        null, //dateFrom
                        null, //dateTo
                        null //afterEntryReference
                );

                log.info("Account '{}' has {} transactions", account.getResourceId(), transactions.getTransactions().size());
                if (transactions.getTransactions().size() > 0) {
                    log.info("First: {}", transactions.getTransactions().get(0));
                }
            } catch (ApiException e) {
                log.error("{}: {}", account.getResourceId(), e.toString());
            }
        }
    }

}
