package com.kyc.nashid.login.pojoclass.recentscan;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Link {

    @Override
    public String toString() {
        return "Link{" +
                "url=" + url +
                ", label='" + label + '\'' +
                ", active=" + active +
                '}';
    }

    @SerializedName("url")
    @Expose
    private Object url;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("active")
    @Expose
    private Boolean active;

    public Object getUrl() {
        return url;
    }

    public void setUrl(Object url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}