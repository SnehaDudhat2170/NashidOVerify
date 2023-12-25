package com.kyc.nashid.login.pojoclass.companydomain;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Data {

    @SerializedName("company_uuid")
    @Expose
    private String companyUuid;
    @SerializedName("registration_type")
    @Expose
    private String registratiorType;

    public String getRegistratiorType() {
        return registratiorType;
    }

    @SerializedName("backend_base_url")
    @Expose
    private String backendBaseUrl;
    @SerializedName("face_match_base_url")
    @Expose
    private String faceMatchBaseUrl;
    @SerializedName("aml_base_url")
    @Expose
    private String amlBaseUrl;
    @SerializedName("image_crop_base_url")
    @Expose
    private String imageCropBaseUrl;
    @SerializedName("liveness_base_url")
    @Expose
    private String livenessBaseUrl;

    public String getCompanyUuid() {
        return companyUuid;
    }

    public void setCompanyUuid(String companyUuid) {
        this.companyUuid = companyUuid;
    }

    public String getBackendBaseUrl() {
        return backendBaseUrl;
    }

    public void setBackendBaseUrl(String backendBaseUrl) {
        this.backendBaseUrl = backendBaseUrl;
    }

    public String getFaceMatchBaseUrl() {
        return faceMatchBaseUrl;
    }

    public void setFaceMatchBaseUrl(String faceMatchBaseUrl) {
        this.faceMatchBaseUrl = faceMatchBaseUrl;
    }

    public String getAmlBaseUrl() {
        return amlBaseUrl;
    }

    public void setAmlBaseUrl(String amlBaseUrl) {
        this.amlBaseUrl = amlBaseUrl;
    }

    public String getImageCropBaseUrl() {
        return imageCropBaseUrl;
    }

    public void setImageCropBaseUrl(String imageCropBaseUrl) {
        this.imageCropBaseUrl = imageCropBaseUrl;
    }

    public String getLivenessBaseUrl() {
        return livenessBaseUrl;
    }

    public void setLivenessBaseUrl(String livenessBaseUrl) {
        this.livenessBaseUrl = livenessBaseUrl;
    }

}