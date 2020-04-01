package com.eb.demo;

import com.enablebanking.ApiClient;
import com.enablebanking.exception.DataRetrievalException;
import com.enablebanking.exception.InsufficientScopeException;
import com.enablebanking.api.AispApi;
import com.enablebanking.api.AuthApi;
import com.enablebanking.model.AccountResource;
import com.enablebanking.model.ConnectorSettings;
import com.enablebanking.model.HalAccounts;
import com.enablebanking.model.HalBalances;
import com.enablebanking.model.HalTransactions;
import com.enablebanking.model.Token;
import com.enablebanking.model.NordeaConnectorSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

import static com.eb.demo.DemoUtils.blockReadRedirectedUrl;
import static com.eb.demo.DemoUtils.parseQueryParams;

public class AispExample {
    private static final Logger log = LoggerFactory.getLogger(AispExample.class);

    public static void main(String[] args) {
        String authRedirectUri = "https://enablebanking.com"; // !!! PUT YOUR REDIRECT URI HERE

        // Initialize settings.
        ConnectorSettings settings = new NordeaConnectorSettings() // one might choose another bank here
                .clientId("client-id")  // API client ID
                .clientSecret("client-secret")
                .redirectUri(authRedirectUri)
                .certPath("cert-path")  // Path or URI to QWAC certificate in PEM format
                .keyPath("key-path")  // Path or URI to QWAC certificate private key in PEM format
                .country("FI")
                .language("fi")
                .sandbox(true);

        // Create client instance.
        ApiClient apiClient = new ApiClient(settings);

        // Create authentication interface.
        AuthApi authApi = new AuthApi(apiClient);

        String authUrl = authApi.getAuth(
                "test", // state to pass to redirect URL
                null // no access parameter (requesting consent for default AISP scope)
        ).getUrl();

        // calling helper functions for CLI interaction
        String redirectedUrl = blockReadRedirectedUrl(authUrl, authRedirectUri);
        Map<String, String> parsedQueryParams = parseQueryParams(redirectedUrl);

        Token token = authApi.makeToken(
                "authorization_code", // grant type, MUST be set to "authorization_code"
                parsedQueryParams.get("code"), // The code received in the query string when redirected from authorization
                );
        log.info("Token: {}", token);

        AispApi aispApi = new AispApi(apiClient);

        // apiClient has already accessToken and refreshToken applied after call to makeToken()
        HalAccounts accounts = aispApi.getAccounts();
        log.info("Accounts: {}", accounts);

        for (AccountResource account : accounts.getAccounts()) {
            try {
                HalBalances accountBalances = aispApi.getAccountBalances(account.getResourceId());
                log.info("Account balances: {}", accountBalances);
            } catch (DataRetrievalException e) {
                log.error("{}: {}", account.getResourceId(), e.toString());
            }
        }

        for (AccountResource account : accounts.getAccounts()) {
            try {
                HalTransactions transactions = aispApi.getAccountTransactions(account.getResourceId(),
                        null,  // dateFrom
                        null,  // dateTo
                        null,  // afterEntryReference,
                        null  // transactionStatus
                );

                log.info("Account '{}' has {} transactions", account.getResourceId(), transactions.getTransactions().size());
                if (transactions.getTransactions().size() > 0) {
                    log.info("First: {}", transactions.getTransactions().get(0));
                }
            } catch (DataRetrievalException e) {
                log.error("{}: {}", account.getResourceId(), e.toString());
            }
        }
    }
}
