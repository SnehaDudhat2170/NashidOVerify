package com.kyc.nashid.login.pojoclass.registration;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Keep
public class Registration {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private RegisterData registerData;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public RegisterData getData() {
        return registerData;
    }

    @Override
    public String toString() {
        return "Registration{" +
                "success=" + success +
                ", registerData=" + registerData +
                ", message='" + message + '\'' +
                '}';
    }

    public void setData(RegisterData registerData) {
        this.registerData = registerData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}