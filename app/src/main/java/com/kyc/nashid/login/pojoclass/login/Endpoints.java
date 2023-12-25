package com.kyc.nashid.login.pojoclass.login;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Keep
public class Endpoints {

    @SerializedName("login_url")
    @Expose
    private String loginUrl;
    @SerializedName("user_companies")
    @Expose
    private String userCompanies;
    @SerializedName("switch_company")
    @Expose
    private String switchCompany;
    @SerializedName("app_version")
    @Expose
    private String appVersion;
    @SerializedName("company_domain")
    @Expose
    private String companyDomain;
    @SerializedName("main_server")
    @Expose
    private String mainServer;

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getUserCompanies() {
        return userCompanies;
    }

    public void setUserCompanies(String userCompanies) {
        this.userCompanies = userCompanies;
    }

    public String getSwitchCompany() {
        return switchCompany;
    }

    public void setSwitchCompany(String switchCompany) {
        this.switchCompany = switchCompany;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCompanyDomain() {
        return companyDomain;
    }

    public void setCompanyDomain(String companyDomain) {
        this.companyDomain = companyDomain;
    }

    public String getMainServer() {
        return mainServer;
    }

    public void setMainServer(String mainServer) {
        this.mainServer = mainServer;
    }


}