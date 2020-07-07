package com.cryptite.lite;

public class Account extends AccountData {
    public Account(LokaLite plugin, AccountData data) {
        super(plugin.db, data);
    }
}
