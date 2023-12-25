package com.kyc.nashid.login.pojoclass.registration;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
@Keep
public class InvalideUser {
    @SerializedName("username")
    @Expose
    private List<String> username;

    public List<String> getUsername() {
        return username;
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }
}
