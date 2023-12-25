package com.kyc.nashid.login.pojoclass.featurecheck;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class FeatureCheck {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private FeatureCheckData registerData;

    @Override
    public String toString() {
        return "FeatureCheck{" +
                "success=" + success +
                ", registerData=" + registerData +
                ", message='" + message + '\'' +
                '}';
    }

    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public FeatureCheckData getData() {
        return registerData;
    }

    public void setData(FeatureCheckData registerData) {
        this.registerData = registerData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
