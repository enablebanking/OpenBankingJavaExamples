package com.eb.demo;

import com.enablebanking.ApiClient;
import com.enablebanking.api.PispApi;
import com.enablebanking.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import static com.eb.demo.DemoUtils.blockReadRedirectedUrl;
import static com.eb.demo.DemoUtils.parseQueryParams;

public class PispExample {
    private static final Logger log = LoggerFactory.getLogger(PispExample.class);

    public static void main(String[] args) {
        String paymentAuthRedirectUri = "https://enablebanking.com"; // !!! PUT YOUR REDIRECT URI HERE

        ConnectorSettings settings = new AliorConnectorSettings() // one might choose another bank here
                .clientId("client-id")  // API client ID
                .clientSecret("client-secret")
                .certPath("cert-path")  // Path or URI QWAC certificate in PEM format
                .keyPath("key-path")  // Path or URI to QWAC certificate private key in PEM format
                .signKeyPath("sign-key-path")  // Path or URI to QSeal certificate in PEM format
                .signPubKeySerial("sign-pub-key-serial")  // Public serial key of the QSeal certificate located in signKeyPath
                .signFingerprint("sign-fingerprint")
                .signCertUrl("sign-cert-url")
                .redirectUri(paymentAuthRedirectUri)
                .paymentAuthRedirectUri(paymentAuthRedirectUri)  // URI where clients are redirected to after payment authorization.
                .paymentAuthState("test")  // This value returned to paymentAuthRedirectUri after payment authorization.
                .sandbox(true);

        ApiClient apiClient = new ApiClient(settings);

        PispApi pispApi = new PispApi(apiClient);
        UnstructuredRemittanceInformation remittance = new UnstructuredRemittanceInformation();
        remittance.add("Some remittance info");

        PaymentRequestResource prr = new PaymentRequestResource()
                .creditTransferTransaction(Arrays.asList(
                        new CreditTransferTransaction()
                                .instructedAmount(new AmountType()
                                        .currency("EUR")
                                        .amount(new BigDecimal("12.25")))
                                .frequency(FrequencyCode.DAIL)
                                .beneficiary(
                                        new Beneficiary()
                                                .creditor(
                                                        new PartyIdentification()
                                                                .name("Creditor name")
                                                                .postalAddress(
                                                                        new PostalAddress()
                                                                                .addressLine(Arrays.asList(
                                                                                        "Creditor Name ",
                                                                                        "Creditor Address 1",
                                                                                        "Creditor Address 2"))
                                                                                .country("RO")))
                                                .creditorAccount(new AccountIdentification()
                                                        .iban("RO56ALBP0RON421000045875")))
                                .remittanceInformation(remittance)))
                .paymentTypeInformation(new PaymentTypeInformation()
                        .serviceLevel(ServiceLevelCode.SEPA) // will be resolved to "pis:EEA"
                        .localInstrument("SEPA")) // set explicitly, can also be for example SWIFT or ELIXIR
                .debtor(new PartyIdentification()
                        .name("Debtor name")
                        .postalAddress(new PostalAddress()
                                .addressLine(Arrays.asList(
                                        "Debtor Name",
                                        "Debtor Address 1",
                                        "Debtor Address 2"))
                                .country("PL")))
                .debtorAccount(new AccountIdentification().iban("PL63249000050000400030900682"));
        HalPaymentRequestCreation c = pispApi.makePaymentRequest(prr);

        // calling helper functions for CLI interaction
        String redirectedUrl = blockReadRedirectedUrl(c.getLinks().getConsentApproval().getHref(), authRedirectUri);

        Map<String, String> parsedQueryParams = parseQueryParams(redirectedUrl);
        HalPaymentRequest pr = pispApi.makePaymentRequestConfirmation(
                c.getPaymentRequestResourceId(),
                new PaymentRequestConfirmation().psuAuthenticationFactor(parsedQueryParams.get("code")).paymentRequest(prr));

        log.info("Payment request: {}", pr);
    }
}
