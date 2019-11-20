package com.eb.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DemoUtils {
    private static final Logger log = LoggerFactory.getLogger(DemoUtils.class);

    static void println(Object obj) {
        System.out.println(obj);
    }

    static Map<String, String> parseQueryParams(String url) {
        Map<String, String> result = new HashMap<String, String>();

        String queryParams = null;

        try {
            queryParams = new URL(url).getQuery();
        } catch (IOException e) {
            log.error("", e);
        }

        String[] pairs = queryParams.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                result.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                        URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
            }
        }
        return result;
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
