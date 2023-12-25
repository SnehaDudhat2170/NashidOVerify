package com.kyc.nashid.login.pojoclass;

import androidx.annotation.Keep;

@Keep
public class ChildItem {
    private String name;

    public ChildItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
// Getters and setters
}