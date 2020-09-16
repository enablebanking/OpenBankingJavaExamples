package com.eb.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class DemoUtils {
    private static final Logger log = LoggerFactory.getLogger(DemoUtils.class);

    static void println(Object obj) {
        System.out.println(obj);
    }

    static String blockReadRedirectedUrl(String authUrl, String redirectUri) {
        println("Please, open this page in browser: " + authUrl);
        println("Login, authenticate and copy paste back the URL where you got redirected.");
        println("URL: (starts with " + redirectUri + "): ");


        Scanner scan = new Scanner(System.in);
        String redirectedUrl = scan.next();
        scan.close();

        println("");

        return redirectedUrl;
    }
}
