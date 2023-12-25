package com.kyc.nashid.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.kyc.nashid.BuildConfig;
import com.kyc.nashid.MyAppApplication;
import com.kyc.nashid.databinding.ActivityEnterCompanyCodeBinding;
import com.kyc.nashid.login.networking.APIClient;
import com.kyc.nashid.login.networking.APIInterface;
import com.kyc.nashid.login.networking.AllUrls;
import com.kyc.nashid.login.pojoclass.login.Endpoints;
import com.kyc.nashid.login.pojoclass.login.Login;
import com.kyc.nashid.login.pojoclass.login.LoginData;
import com.kyc.nashid.login.utils.LoaderUtil;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashidmrz.Logger;
import com.kyc.nashid.R;
import com.kyc.nashid.login.utils.SuccessFailureDialog;
import com.kyc.nashidmrz.NashidSDK;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;
import com.kyc.nashidmrz.mrtd2.rooted.RootedCheck;
import com.kyc.nashidmrz.utility.SharePreferenceUtility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterLoginPinCodeActivity extends AppCompatActivity {

    private ActivityEnterCompanyCodeBinding binding;
    private TextSizeConverter textSizeConverter;
    private final Logger logger = Logger.withTag(this.getClass().getSimpleName());
    private String username = "";
    private String loginType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEnterCompanyCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RootedCheck.getInstance().setFlag(EnterLoginPinCodeActivity.this);
        if (RootedCheck.getInstance().isRootedDevice(getApplicationContext())) {
            RootedCheck.getInstance().showRootedDeviceDialog(this, getString(R.string.root_dialog), getString(R.string.root_desc), getString(R.string.root_btn));
        } else {
            initView();
        }
    }

    private void initView() {
        loginType = getIntent().getStringExtra("loginType");
        username = getIntent().getStringExtra("name");
        SVG svg = null;
        try {
            svg = SVG.getFromResource(getResources(), R.raw.back);
            binding.layoutHeader.imgBack.setSVG(svg);
        } catch (SVGParseException e) {

        }
        setLayoutAndTextSize();
        changeOTPviewSize();
    }

    private void changeOTPviewSize() {
        binding.layoutHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.imgScanComplete.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                openSelectDocumentScreen();
            }
        });

        int size = textSizeConverter.getWidth(8);
//        binding.otpView.setDistance(size, textSizeConverter.calculateRadius(8));
        /*binding.otpView.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText) {

                return true; // Return false to keep the keyboard open else return true to close the keyboard
            }
        });*/
        binding.txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnterLoginPinCodeActivity.this, ResetAccountActivity.class);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
            }
        });
        binding.cardBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLoginApi();
               /* if (!isAlreadyEnteredPincode) {
                    isAlreadyEnteredPincode = true;
                    binding.layoutHeader.lytHeaderMain.setVisibility(View.INVISIBLE);
                    binding.txtHeader.setText(getString(R.string.confirm_pincode_header));
                    binding.txtDesc.setText(getString(R.string.confirm_pincode_subtitle));
                    binding.txtBtnNext.setText(getString(R.string.pincode_validate));
                    binding.otpView.setText("");
                } else {
                    if (pincode.equalsIgnoreCase(binding.otpView.getText().toString())) {
                        callRegisterApi();
//                        Toast.makeText(getApplicationContext(), "password matched", Toast.LENGTH_SHORT).show();
                    } else {
                        showFailureDialog(getString(R.string.pin_match_fail));
//                        Toast.makeText(getApplicationContext(), "password incorrect", Toast.LENGTH_SHORT).show();
                    }
                }*/
            }
        });


    }

//    private void showFailureDialog(String desc) {
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        DialogRegisterFailureBinding binding = DialogRegisterFailureBinding.inflate(dialog.getLayoutInflater());
//        dialog.setContentView(binding.getRoot());
//
//        int padding = textSizeConverter.getPaddingORMarginValue(16);
//        binding.mainDialogLyt.setPadding(padding, padding, padding, padding);
//
//        ViewGroup.LayoutParams layoutParams2 = binding.imgScanFailure.getLayoutParams();
//        layoutParams2.width = textSizeConverter.getWidth(56);
//        layoutParams2.height = textSizeConverter.getHeight(56);
//        binding.imgScanFailure.setLayoutParams(layoutParams2);
//
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.imgScanFailure.getLayoutParams();
//        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(8), 0, 0);
//        binding.imgScanFailure.setLayoutParams(layoutParams);
////            binding.progressBar.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),R.color.orange_back)));
////        binding.layoutHeader.txtHelp.setText(getString(R.string.nfc_chip_rading));
//
//        binding.txtScanCompleted.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(20));
//        binding.txtScanCompleted.setText(desc);
//        layoutParams = (LinearLayout.LayoutParams) binding.txtScanCompleted.getLayoutParams();
//        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(24), 0, textSizeConverter.getPaddingORMarginValue(24));
//        binding.txtScanCompleted.setLayoutParams(layoutParams);
//
//
//        padding = textSizeConverter.getPaddingORMarginValue(12);
//        binding.txtBtnRetry.setPadding(0, padding, 0, padding);
//        binding.txtBtnRetry.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
//        binding.btnRetry.setRadius(textSizeConverter.calculateRadius(8));
//
//        binding.btnRetry.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//        dialog.setCancelable(false);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//    }

    private void setLayoutAndTextSize() {
        textSizeConverter = new TextSizeConverter(getApplicationContext());
        textSizeConverter.changeStatusBarColor(EnterLoginPinCodeActivity.this);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.txtHelp.setText(getString(R.string.company_header_title));
        binding.layoutHeader.txtHelp.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(17));

        layoutParams2 = binding.imgLogo.getLayoutParams();
        layoutParams2.width = textSizeConverter.getWidth(50);
        layoutParams2.height = textSizeConverter.getHeight(44);
        binding.imgLogo.setLayoutParams(layoutParams2);
        binding.imgLogo.setVisibility(View.VISIBLE);

        int padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.lytMainEnterCompanyCode.setPadding(padding, padding, padding, padding);

        padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.txtBtnNext.setPadding(0, padding, 0, padding);
        binding.txtBtnNext.setText(getString(R.string.login_header));
        binding.txtBtnNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.cardBtnNext.setRadius(textSizeConverter.calculateRadius(8));
        binding.cardBtnNext.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams marginLyoutParam = (LinearLayout.LayoutParams) binding.txtHeader.getLayoutParams();
        marginLyoutParam.setMargins(0, 0, 0, textSizeConverter.getPaddingORMarginValue(14));
        binding.txtHeader.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.layoutHeader.txtHelp.getLayoutParams();
        marginLyoutParam.setMargins(textSizeConverter.getPaddingORMarginValue(20), 0, 0, 0);
        binding.layoutHeader.txtHelp.setLayoutParams(marginLyoutParam);
        binding.txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(28));

        binding.txtHeader.setText(getString(R.string.pin_title));
        binding.txtDesc.setText(getString(R.string.pin_subtitle));

        binding.txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        binding.imgScanComplete.getLayoutParams().width = textSizeConverter.getWidth(56);
        binding.imgScanComplete.getLayoutParams().height = textSizeConverter.getHeight(56);
        binding.imgScanComplete.invalidate();
        marginLyoutParam = (LinearLayout.LayoutParams) binding.txtScanCompleted.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(24), 0, 0);
        binding.txtScanCompleted.setLayoutParams(marginLyoutParam);
        binding.txtScanCompleted.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(20));

        marginLyoutParam = (LinearLayout.LayoutParams) binding.imgLogo.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(30), 0, textSizeConverter.getPaddingORMarginValue(30));
        binding.imgLogo.setLayoutParams(marginLyoutParam);
        binding.txtResendDesc.setPadding(0, textSizeConverter.getPaddingORMarginValue(20), 0, 0);
//        binding.txtResend.setPadding(0,0, 0, textSizeConverter.getPaddingORMarginValue(24));
        binding.lytResend.setVisibility(View.VISIBLE);
        binding.txtResendDesc.setText(getString(R.string.pin_reset));
        binding.txtResend.setPaintFlags(binding.txtResend.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.txtResend.setText(getString(R.string.btn_pin_reset));
    }

    private void callLoginApi() {
        LoaderUtil.showLoader(EnterLoginPinCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        SharedPreferences securePreferences = new SecurePreferences(this);
        String url = "";
        if (BuildConfig.FLAVOR.equals("dev")) {
            url = new AllUrls().getDevLoginURL();
        } else {
            url = new AllUrls().getProdLoginURL();
        }
//         .callLogin(username, binding.otpView.getText().toString())
        logger.log("Otpview text: " + url);
        APIClient.getClient(url, "").create(APIInterface.class)
                .callLogin(username, binding.otpView.getText().toString())
//                .callLogin("vogij59951@newnime.com", "dev@2000")
//                .callLogin("xojeso3882@mkurg.com", "dev@2000")
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(EnterLoginPinCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        try {
                            String jsonResponse = response.body().string();
                            logger.log("Otpview text: " + jsonResponse + "  " + response.body());
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getBoolean("success")) {
//                            if (response.body().getSuccess()) {
                                JSONObject registerData = jsonObject.getJSONObject("data");
                                String companuUUID = registerData.getString("company_uuid");
                                boolean dealer = registerData.getBoolean("dealer");
                                securePreferences.edit().putBoolean(getString(R.string.dealer), dealer).apply();
                                securePreferences.edit().putString(getString(R.string.company_uuid), companuUUID).apply();
                                securePreferences.edit().putString(getString(R.string.register_token), registerData.getString("token")).apply();
                                securePreferences.edit().putString(getString(R.string.nashid_token), registerData.getString("nashid_token")).apply();
                                securePreferences.edit().putBoolean(getString(R.string.company_data_sharing), registerData.getBoolean("company_data_sharing")).apply();
                                securePreferences.edit().putString(getString(R.string.company_verification_documents), registerData.getString("company_verification_documents")).apply();
//                                binding.lytRegisterSuccessfully.setVisibility(View.VISIBLE);
                                securePreferences.edit().putString(getString(R.string.register_name), registerData.getString("name")).apply();
                                securePreferences.edit().putString(getString(R.string.company_name), registerData.getString("company_name")).apply();
                                logger.log("name: " + registerData.getString("name"));
                                JSONObject endpoints = registerData.getJSONObject("endpoints");
                                logger.log("endpoints: " + endpoints);
//                                securePreferences.edit().putString(getString(R.string.login_url),endpoints.getString("login_url")).apply();
                                int lastSlashIndex = endpoints.getString("user_companies").lastIndexOf("/");
                                String modifiedString = endpoints.getString("user_companies").substring(0, lastSlashIndex + 1);
                                securePreferences.edit().putString(getString(R.string.user_companies), modifiedString).apply();

                                lastSlashIndex = endpoints.getString("switch_company").lastIndexOf("/");
                                modifiedString = endpoints.getString("switch_company").substring(0, lastSlashIndex + 1);
                                securePreferences.edit().putString(getString(R.string.switch_company), modifiedString).apply();

                                lastSlashIndex = endpoints.getString("app_version").lastIndexOf("/");
                                modifiedString = endpoints.getString("app_version").substring(0, lastSlashIndex + 1);
                                securePreferences.edit().putString(getString(R.string.app_version), modifiedString).apply();

                                lastSlashIndex = endpoints.getString("company_domain").lastIndexOf("/");
                                modifiedString = endpoints.getString("company_domain").substring(0, lastSlashIndex + 1);
                                securePreferences.edit().putString(getString(R.string.company_domain), modifiedString).apply();
//                                securePreferences.edit().putString(getString(R.string.main_server),endpoints.getString("main_server")).apply();
                                Iterator<String> keys = endpoints.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    logger.log("login:" + key);
                                    try {
                                        JSONObject endpointObject = endpoints.getJSONObject(key);

                                        // Here 'key' will be the dynamic key in the JSON response
                                        // You can access the values within each 'endpointObject'
                                        String backendURL = endpointObject.optString("backend");
                                        String facematchURL = endpointObject.optString("facematch");
                                        String amlSearchURL = endpointObject.optString("aml_search");
                                        String amlDetailURL = endpointObject.optString("aml_detail");
                                        String imageCropURL = endpointObject.optString("image_crop");
                                        securePreferences.edit().putString(key + getString(R.string.backend_base_url), backendURL).apply();
                                        securePreferences.edit().putString(key + getString(R.string.face_match_base_url), facematchURL.substring(0, facematchURL.lastIndexOf("/") + 1)).apply();
                                        securePreferences.edit().putString(key + getString(R.string.aml_base_url), amlSearchURL.substring(0, amlSearchURL.lastIndexOf("/") + 1)).apply();
                                        securePreferences.edit().putString(key + getString(R.string.aml_detail), amlDetailURL.substring(0, amlDetailURL.lastIndexOf("/") + 1)).apply();
                                        securePreferences.edit().putString(key + getString(R.string.image_crop_base_url), imageCropURL.substring(0, imageCropURL.lastIndexOf("/") + 1)).apply();
                                    } catch (Exception e) {
                                    }
                                    // Use these values as needed
                                }
//                                JSONObject company = endpoints.getJSONObject(companuUUID);
//                                JSONObject jsonObject = new JSONObject(MyAppApplication.loadJSONFromAsset(getApplicationContext()));
//                                JSONObject company=companyJasonObject.getJSONObject(registerData.getCompanyUuid());
//                                String facematch= company.getString("facematch");
//                                String amlSearch=company.getString("aml_search");
//                                String amlDetail=company.getString("aml_detail");
//                                String imageCrop=company.getString("image_crop");

                                try {
                                    JSONArray jsonArray = registerData.getJSONArray("company_feature_list");
                                    String feature = null;
                                    if (jsonArray.length() != 0) {
                                        ArrayList<String> strings = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            String value = jsonArray.getString(i);
                                            strings.add(value);
                                            // Do something with each value in the array (e.g., print, store, etc.)
                                            System.out.println("Value at index " + i + ": " + value);
                                            if (feature == null) {
                                                feature = value;
                                            } else {
                                                feature = feature + "," + value;
                                            }
                                        }
                                        if (strings.size() != 0) {
                                            NashidSDK.setLoginFeatureList(strings);
                                            securePreferences.edit().putString(getString(R.string.login_feature), feature).apply();
                                            logger.log("myapplication---: " + strings.contains("NFC"));
                                            /*if (strings.contains("NFC")) {
                                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.nfc), true);
                                            } else {
                                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.nfc), false);
                                            }
                                            if (strings.contains("Face Match")) {
                                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.facila_matching), true);
                                            } else {
                                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.facila_matching), false);
                                            }
                                            if (strings.contains("AML")) {
                                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.aml_screening), true);
                                            } else {
                                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.aml_screening), false);
                                            }
                                            if (strings.contains("OCR")) {
                                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.ocr), true);
                                            } else {
                                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.ocr), false);
                                            }
                                            if (strings.contains("Liveness")) {
                                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.liveness), true);
                                            } else {
                                                SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.liveness), false);
                                            }*/
                                        }
                                    } else {
//                                        SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.nfc), false);
//                                        SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.ocr), false);
//                                        SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.liveness), false);
//                                        SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.aml_screening), false);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
//                                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.nfc), false);
//                                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.ocr), false);
//                                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.liveness), false);
//                                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.aml_screening), false);
                                }

                                openSelectDocumentScreen();
                            } else {
                                SuccessFailureDialog.showFailureDialog(EnterLoginPinCodeActivity.this, textSizeConverter, getString(R.string.login_pincode_error), "", false, null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            SuccessFailureDialog.showFailureDialog(EnterLoginPinCodeActivity.this, textSizeConverter, getString(R.string.login_pincode_error), "", false, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        logger.log("Otpview text:fai " + t);
                        LoaderUtil.hideLoader(EnterLoginPinCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                    }
                /*.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(CreatePinCodeActivity.this,binding.customLoader.loaderContainer,textSizeConverter);
                        try {
                            String jsonResponse = response.body().string();
                            logger.log("Otpview text: " + jsonResponse, textSizeConverter);
                        } catch (IOException e) {

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        LoaderUtil.hideLoader(CreatePinCodeActivity.this,binding.customLoader.loaderContainer,textSizeConverter);
                    }*/

                });
    }

    private void openSelectDocumentScreen() {
        Intent intent = new Intent(EnterLoginPinCodeActivity.this, MainDrawerScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
