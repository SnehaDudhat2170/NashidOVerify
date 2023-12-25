package com.kyc.nashid.login.pojoclass.featurecheck;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@Keep
public class FeatureCheckData {

    @SerializedName("features")
    @Expose
    private ArrayList<String> features=new ArrayList<>();

    @Override
    public String toString() {
        return "FeatureCheckData{" +
                "features=" + features +
                '}';
    }

    public ArrayList<String> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<String> features) {
        this.features = features;
    }
}
