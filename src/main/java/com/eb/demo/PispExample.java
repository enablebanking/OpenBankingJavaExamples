package com.eb.demo;

import com.enablebanking.ApiClient;
import com.enablebanking.api.AuthApi;
import com.enablebanking.api.PispApi;
import com.enablebanking.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.eb.demo.DemoUtils.blockReadRedirectedUrl;

public class PispExample {
    private static final Logger log = LoggerFactory.getLogger(PispExample.class);

    private static String getAuthRedirectUri() {
        return "https://enablebanking.com"; // !!! PUT YOUR REDIRECT URI HERE
    }

    private static ConnectorSettings getConnectorSettings() {
        // Initialize settings.
        ConnectorSettings settings = new SPankkiConnectorSettings() // one might choose another bank here
                .redirectUri(getAuthRedirectUri()) // URI where clients are redirected to after payment authorization.
                .clientId("clientId")  // API client ID
                .xApiKey("xApiKey") // API key
                .certPath("path/to/cert")  // Path or URI QWAC certificate in PEM format
                .keyPath("path/to/key")  // Path or URI to QWAC certificate private key in PEM format
                .signKeyPath("path/to/sign/cert/")  // Path or URI to QSeal certificate in PEM format
                .signPubKeySerial("sign-pub-key-serial")  // Public serial key of the QSeal certificate located in signKeyPath
                .sandbox(true);
        return settings;
    }

    public static void main(String[] args) {
        ConnectorSettings settings = getConnectorSettings();
        ApiClient apiClient = new ApiClient(settings);
        AuthApi authApi = new AuthApi(apiClient);

        PispApi pispApi = new PispApi(apiClient);
        UnstructuredRemittanceInformation remittance = new UnstructuredRemittanceInformation();
        remittance.add("Some remittance info");

        PaymentRequestResource prr = new PaymentRequestResource()
                .creditTransferTransaction(Arrays.asList(
                        new CreditTransferTransaction()
                                .instructedAmount(new AmountType()
                                        .currency("EUR")
                                        .amount(new BigDecimal("2.00")))
                                .beneficiary(
                                        new Beneficiary()
                                                .creditor(
                                                        new PartyIdentification()
                                                                .name("Creditor name"))
                                                .creditorAccount(new AccountIdentification()
                                                        .iban("FI4966010005485495")))
                                .remittanceInformation(remittance)))
                .paymentTypeInformation(new PaymentTypeInformation()
                        .instructionPriority(PriorityCode.NORM)
                        .categoryPurpose(CategoryPurposeCode.CASH)
                        .serviceLevel(ServiceLevelCode.SEPA)); // will be resolved to "pis:EEA"
        HalPaymentRequestCreation c = pispApi.makePaymentRequest(prr,
                                                                "test", null, null); // This value returned to redirectUri after payment authorization.

        // calling helper functions for CLI interaction
        String redirectedUrl = blockReadRedirectedUrl(c.getLinks().getConsentApproval().getHref(), getAuthRedirectUri());

        AuthRedirect parsedQueryParams = authApi.parseRedirectUrl(redirectedUrl);
        HalPaymentRequest pr = pispApi.makePaymentRequestConfirmation(
                c.getPaymentRequestResourceId(),
                new PaymentRequestConfirmation().psuAuthenticationFactor(parsedQueryParams.getCode()));

        log.info("Payment request: {}", pr);
    }
}
