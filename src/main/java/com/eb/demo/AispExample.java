package com.eb.demo;

import com.enablebanking.ApiClient;
import com.enablebanking.exception.DataRetrievalException;
import com.enablebanking.api.AispApi;
import com.enablebanking.api.AuthApi;
import com.enablebanking.model.Access;
import com.enablebanking.model.AccountResource;
import com.enablebanking.model.ClientInfo;
import com.enablebanking.model.ConnectorSettings;
import com.enablebanking.model.HalAccounts;
import com.enablebanking.model.AuthRedirect;
import com.enablebanking.model.HalBalances;
import com.enablebanking.model.HalTransactions;
import com.enablebanking.model.Token;
import com.enablebanking.model.NordeaConnectorSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.OffsetDateTime;


import static com.eb.demo.DemoUtils.blockReadRedirectedUrl;

public class AispExample {
    private static final Logger log = LoggerFactory.getLogger(AispExample.class);
    private static String getAuthRedirectUri() {
        return "https://enablebanking.com"; // !!! PUT YOUR REDIRECT URI HERE
    }

    private static ConnectorSettings getConnectorSettings() {
        // Initialize settings.
        ConnectorSettings settings = new NordeaConnectorSettings() // one might choose another bank here
                .clientId("client-id")  // API client ID
                .clientSecret("client-secret") // API client secret
                .redirectUri(getAuthRedirectUri())
                .signKeyPath("sign/key/path")  // Path or URI to QSEAL certificate in PEM format
                .country("FI")
                .language("fi")
                .sandbox(true);
        return settings;
    }

    public static void main(String[] args) {
        ConnectorSettings settings = getConnectorSettings();
        // Create client instance.
        ApiClient apiClient = new ApiClient(settings);

        // Create authentication interface.
        AuthApi authApi = new AuthApi(apiClient);
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.psuIpAddress("10.10.10.10")
                .psuIpPort("80")
                .psuUserAgent("Mozilla")
                .psuHttpMethod("GET"); // more parameters might be needed for some banks, so it's recommended to fill in all.
        authApi.setClientInfo(clientInfo);

        OffsetDateTime validUntil = OffsetDateTime.now().plusDays(1);
        Access access = new Access()
                .frequencyPerDay(4)
                .recurringIndicator(false)
                .validUntil(validUntil);
        String authUrl = authApi.getAuth(
                "test", // state to pass to redirect URL
                null, // credentials (required for connectors in some countries e.g. Sweden)
                null, // authentication method (required for some connectors which have multiple authentication methods available)
                access //  access parameter (if access = null -> requesting consent for default AISP scope),
        ).getUrl();

        // calling helper function for CLI interaction
        String redirectedUrl = blockReadRedirectedUrl(authUrl, getAuthRedirectUri());
        //AuthRedirect parsedQueryParams = authApi.parseRedirectUrl(redirectedUrl);

        Token token = authApi.makeToken(
                "authorization_code", // grant type, MUST be set to "authorization_code"
                redirectedUrl, // The code received in the query string when redirected from authorization
                null
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
