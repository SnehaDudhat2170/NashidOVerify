package com.kyc.nashid;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kyc.nashid.login.LoginAccountActivity;
import com.kyc.nashid.login.MainDrawerScreen;
import com.kyc.nashid.login.networking.APIClient;
import com.kyc.nashid.login.networking.APIInterface;
import com.kyc.nashid.login.networking.AllUrls;
import com.kyc.nashid.login.pojoclass.featurecheck.FeatureCheck;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashidmrz.mrtd2.rooted.RootedCheck;
import com.kyc.nashidmrz.utility.SharePreferenceUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAppApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private Logger logger = Logger.withTag("MyAppApplication");
    private boolean isApiCalling = false;

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);
        // Initialize Firebase
//        FirebaseApp.initializeApp(this);
//
//         Enable Crashlytics
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
openActivity();
    }

    private void openActivity() {
        SharedPreferences securePreferences = new SecurePreferences(this);
        Log.d("TAG", "onCreate: " + securePreferences.getString(getString(R.string.register_token), ""));
        if (!securePreferences.getString(getString(R.string.register_token), "").equalsIgnoreCase("")) {
            Intent intent = new Intent(getApplicationContext(), MainDrawerScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginAccountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
//        RootedCheck.getInstance().setFlag(activity);

        if (RootedCheck.getInstance().isRootedDevice(getApplicationContext())) {
            RootedCheck.getInstance().showRootedDeviceDialog(activity, getString(R.string.root_dialog), getString(com.kyc.nashidmrz.R.string.root_desc), getString(com.kyc.nashidmrz.R.string.root_btn));
        } else {

        }
        SharedPreferences securePreferences = new SecurePreferences(this);

      /*  try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(getApplicationContext()));
            logger.log("json read data: "+jsonObject.getString("login_url"));
            securePreferences.edit().putString(getString(R.string.login_url),jsonObject.getString("login_url")).apply();
            securePreferences.edit().putString(getString(R.string.user_companies),jsonObject.getString("user_companies")).apply();
            securePreferences.edit().putString(getString(R.string.switch_company),jsonObject.getString("switch_company")).apply();
            securePreferences.edit().putString(getString(R.string.app_version), jsonObject.getString("app_version")).apply();
            securePreferences.edit().putString(getString(R.string.company_domain), jsonObject.getString("company_domain")).apply();
            securePreferences.edit().putString(getString(R.string.main_server), jsonObject.getString("main_server")).apply();
            securePreferences.edit().putString(getString(R.string.check_user),jsonObject.getString("main_server")).apply();
        } catch (JSONException e) {

        }*/
//        securePreferences.edit().putString(getString(R.string.backend_base_url), "").apply();
//        securePreferences.edit().putString(getString(R.string.face_match_base_url), companyDomain.getData().getFaceMatchBaseUrl()).apply();
//        securePreferences.edit().putString(getString(R.string.aml_base_url), companyDomain.getData().getAmlBaseUrl()).apply();
//        securePreferences.edit().putString(getString(R.string.image_crop_base_url), companyDomain.getData().getImageCropBaseUrl()).apply();


//        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("endpoints.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        logger.log("myapplication:resume");
        if (!isApiCalling) {
            callFeatureCheck();
        }
//        openActivity();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        logger.log("myapplication:paused");
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    private void callFeatureCheck() {
        SharedPreferences securePreferences = new SecurePreferences(this);
//        if (!securePreferences.getString(getString(R.string.company_uuid), "").equalsIgnoreCase("")) {
//            isApiCalling = true;
//            logger.log("myapplication: " + securePreferences.getString(getString(R.string.backend_base_url), "") + "    " + securePreferences.getString(getString(R.string.company_uuid), ""));
//            String url;
//            if (BuildConfig.FLAVOR.equals("dev")) {
//                url = new AllUrls().getDevMainURL();
//            } else {
//                url = new AllUrls().getProdMainURL();
//            }
//            APIClient.getClient(url, securePreferences.getString(getString(R.string.register_token), "")).create(APIInterface.class)
//                    .callFeatureCheck(securePreferences.getString(getString(R.string.company_uuid), ""))
//                    .enqueue(new Callback<FeatureCheck>() {
//                        @Override
//                        public void onResponse(Call<FeatureCheck> call, Response<FeatureCheck> response) {
//                            logger.log("myapplication---: " + response);
//                            isApiCalling = false;
//                            FeatureCheck featureCheck = response.body();
//                            try {
//                                if (featureCheck.getData().getFeatures() != null) {
//                                    ArrayList<String> strings = featureCheck.getData().getFeatures();
//                                    if (strings.size() != 0) {
//                                        logger.log("myapplication---: " + strings.contains("NFC"));
//                                        if (strings.contains("NFC")) {
//                                            SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.nfc), true);
//                                        } else {
//                                            SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.nfc), false);
//                                        }
//                                        if (strings.contains("Face Match")) {
//                                            SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.facila_matching), true);
//                                        } else {
//                                            SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.facila_matching), false);
//                                        }
//                                        if (strings.contains("AML")) {
//                                            SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.aml_screening), true);
//                                        } else {
//                                            SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.aml_screening), false);
//                                        }
//                                        if (strings.contains("OCR")) {
//                                            SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.ocr), true);
//                                        } else {
//                                            SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.ocr), false);
//                                        }
//                                        if (strings.contains("Liveness")) {
//                                            SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.liveness), true);
//                                        } else {
//                                            SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.liveness), false);
//                                        }
//                                    }
//                                } else {
//                                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.nfc), false);
//                                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.ocr), false);
//                                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.liveness), false);
//                                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.aml_screening), false);
//                                }
//                            }catch (Exception e){
//                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.nfc), false);
//                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.ocr), false);
//                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.liveness), false);
//                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.aml_screening), false);
//                            }
//                            logger.log("myapplication: " + featureCheck.toString());
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<FeatureCheck> call, Throwable t) {
//                            isApiCalling = false;
//                        }
//
//                    });
//        }
    }

}
