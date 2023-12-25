package com.kyc.nashid.login.pojoclass.recentscan;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("company_id")
    @Expose
    private Integer companyId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("scan_type")
    @Expose
    private String scanType;
    @SerializedName("scanned_by")
    @Expose
    private String scannedBy;

    @Override
    public String toString() {
        return "Datum{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", userId=" + userId +
                ", scanType='" + scanType + '\'' +
                ", scannedBy='" + scannedBy + '\'' +
                ", scanData='" + scanData + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", scanStatus=" + scanStatus +
                '}';
    }

    @SerializedName("scan_data")
    @Expose
    private String scanData;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("scan_status")
    @Expose
    private Boolean scanStatus;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @SerializedName("slug")
    @Expose
    private String slug;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getScannedBy() {
        return scannedBy;
    }

    public void setScannedBy(String scannedBy) {
        this.scannedBy = scannedBy;
    }

    public String getScanData() {
        return scanData;
    }

    public void setScanData(String scanData) {
        this.scanData = scanData;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(Boolean scanStatus) {
        this.scanStatus = scanStatus;
    }

}