package com.kyc.nashid.login.pojoclass.recentscan;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class RecentScanData {

    @SerializedName("scans")
    @Expose
    private Scans scans;

    public Scans getScans() {
        return scans;
    }

    @Override
    public String toString() {
        return "RecentScanData{" +
                "scans=" + scans +
                '}';
    }

    public void setScans(Scans scans) {
        this.scans = scans;
    }

}
