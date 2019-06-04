package com.eb.demo;

import java.util.List;

public interface BankSettings {

    List<Object> clientSettings();

    String bankName();

    String redirectUri();

    boolean makeToken();

}
