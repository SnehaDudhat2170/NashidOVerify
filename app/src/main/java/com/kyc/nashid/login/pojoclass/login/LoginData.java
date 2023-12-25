package com.kyc.nashid.login.pojoclass.login;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kyc.nashid.login.pojoclass.featurecheck.FeatureCheckData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Keep
public class LoginData {
    @SerializedName("company_uuid")
    @Expose
    private String companyUuid;
//    @SerializedName("company_name")
//    @Expose
//    private String company_name;
    @SerializedName("endpoints")
    @Expose
    private JSONObject endpoints;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("nashid_token")
    @Expose
    private String nashid_token;

    @SerializedName("company_feature_list")
    @Expose
    private ArrayList<String> companyFeatureList=new ArrayList<>();

//    public String getCompany_name() {
//        return company_name;
//    }

    public JSONObject getEndpoints() {
        return endpoints;
    }

    public String getNashid_token() {
        return nashid_token;
    }

    public void setNashid_token(String nashid_token) {
        this.nashid_token = nashid_token;
    }


    @Override
    public String toString() {
        return "LoginData{" +
                "companyUuid='" + companyUuid + '\'' +
                ", endpoints=" + endpoints +
                ", token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", nashid_token='" + nashid_token + '\'' +
                ", companyFeatureList=" + companyFeatureList +
                '}';
    }


    public ArrayList<String> getCompanyFeatureList() {
        return companyFeatureList;
    }

    public void setCompanyFeatureList(ArrayList<String> companyFeatureList) {
        this.companyFeatureList = companyFeatureList;
    }

    public String getCompanyUuid() {
        return companyUuid;
    }

    public void setCompanyUuid(String companyUuid) {
        this.companyUuid = companyUuid;
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


}
