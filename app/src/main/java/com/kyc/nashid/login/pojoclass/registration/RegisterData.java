package com.kyc.nashid.login.pojoclass.registration;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Keep
public class RegisterData {

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("company_feature_list")
    @Expose
    private ArrayList<String> companyFeatureList = new ArrayList<>();

    public ArrayList<String> getCompanyFeatureList() {
        return companyFeatureList;
    }

    public void setCompanyFeatureList(ArrayList<String> companyFeatureList) {
        this.companyFeatureList = companyFeatureList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RegisterData{" +
                "token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", companyFeatureList=" + companyFeatureList +
                ", username=" + username +
                '}';
    }

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