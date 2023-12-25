package com.kyc.nashid.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.kyc.nashid.databinding.ActivityEnterCompanyCodeBinding;
import com.kyc.nashid.login.customview.PinField;
import com.kyc.nashid.login.networking.APIClient;
import com.kyc.nashid.login.networking.APIInterface;
import com.kyc.nashid.login.networking.AllUrls;
import com.kyc.nashid.login.pojoclass.registration.RegisterData;
import com.kyc.nashid.login.pojoclass.registration.Registration;
import com.kyc.nashid.login.utils.DialogSuccessListener;
import com.kyc.nashid.login.utils.LoaderUtil;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashidmrz.Logger;
import com.kyc.nashid.R;
import com.kyc.nashid.login.utils.SuccessFailureDialog;
import com.kyc.nashidmrz.NashidSDK;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;
import com.kyc.nashidmrz.mrtd2.rooted.RootedCheck;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePinCodeActivity extends AppCompatActivity {

    private ActivityEnterCompanyCodeBinding binding;
    private TextSizeConverter textSizeConverter;
    private final Logger logger = Logger.withTag(this.getClass().getSimpleName());
    private String pincode = "";
    private boolean isAlreadyEnteredPincode = false;
    private String firstName = "";
    private String lastName = "";
    private String loginType = "";
    private String otp = "";
    private String username = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEnterCompanyCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RootedCheck.getInstance().setFlag(CreatePinCodeActivity.this);
        if (RootedCheck.getInstance().isRootedDevice(getApplicationContext())) {
            RootedCheck.getInstance().showRootedDeviceDialog(this, getString(R.string.root_dialog), getString(R.string.root_desc), getString(R.string.root_btn));
        } else {
            initView();
        }
    }

    private void initView() {
        firstName = getIntent().getStringExtra("firstname");
        lastName = getIntent().getStringExtra("lastname");
        loginType = getIntent().getStringExtra("loginType");
        otp = getIntent().getStringExtra("otp");
        username = getIntent().getStringExtra("username");
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
        binding.cardBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSelectDocumentScreen();
            }
        });

        int size = textSizeConverter.getWidth(8);
//        binding.otpView.setDistance(size, textSizeConverter.calculateRadius(8));
        binding.otpView.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText) {
                if (!isAlreadyEnteredPincode) {
                    pincode = binding.otpView.getText().toString();
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
                        SuccessFailureDialog.showFailureDialog(CreatePinCodeActivity.this, textSizeConverter, getString(R.string.pin_match_fail), "", false, new DialogSuccessListener() {
                            @Override
                            public void OnSuccessClick() {
                                retryResetPin();
                            }

                            @Override
                            public void OnFailClick() {
                                retryResetPin();
                            }
                        });
//                        Toast.makeText(getApplicationContext(), "password incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
                return true; // Return false to keep the keyboard open else return true to close the keyboard
            }
        });
        binding.cardBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAlreadyEnteredPincode) {
                    pincode = binding.otpView.getText().toString();
                    isAlreadyEnteredPincode = true;
//                    binding.layoutHeader.lytHeaderMain.setVisibility(View.INVISIBLE);
                    binding.txtHeader.setText(getString(R.string.confirm_pincode_header));
                    binding.txtDesc.setText(getString(R.string.confirm_pincode_subtitle));
                    binding.txtBtnNext.setText(getString(R.string.pincode_validate));
                    binding.otpView.setText("");
                } else {
                    if (pincode.equalsIgnoreCase(binding.otpView.getText().toString())) {
                        callRegisterApi();
//                        Toast.makeText(getApplicationContext(), "password matched", Toast.LENGTH_SHORT).show();
                    } else {
                        SuccessFailureDialog.showFailureDialog(CreatePinCodeActivity.this, textSizeConverter, getString(R.string.pin_match_fail), "", false, null);
//                        Toast.makeText(getApplicationContext(), "password incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
private void retryResetPin(){
    binding.layoutHeader.lytHeaderMain.setVisibility(View.VISIBLE);
    binding.otpView.setText("");
    isAlreadyEnteredPincode=false;
    pincode="";
}
    private void setLayoutAndTextSize() {
        textSizeConverter = new TextSizeConverter(getApplicationContext());
        textSizeConverter.changeStatusBarColor(CreatePinCodeActivity.this);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.txtHelp.setText(getString(R.string.register_header_title));
        binding.layoutHeader.txtHelp.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(17));

        int padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.lytMainEnterCompanyCode.setPadding(padding, padding, padding, padding);

        padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.txtBtnNext.setPadding(0, padding, 0, padding);
        binding.txtBtnNext.setText(getString(R.string.pincode_confirm));
        binding.txtBtnNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.cardBtnNext.setRadius(textSizeConverter.calculateRadius(8));


        padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.lytRegisterSuccessfully.setPadding(padding, padding, padding, padding);

        padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.txtBtnLogin.setPadding(0, padding, 0, padding);
        binding.txtBtnLogin.setText(getString(R.string.login_header));
        binding.txtBtnLogin.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.cardBtnLogin.setRadius(textSizeConverter.calculateRadius(8));

        LinearLayout.LayoutParams marginLyoutParam = (LinearLayout.LayoutParams) binding.txtHeader.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(20), 0, textSizeConverter.getPaddingORMarginValue(30));
        binding.txtHeader.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.layoutHeader.txtHelp.getLayoutParams();
        marginLyoutParam.setMargins(textSizeConverter.getPaddingORMarginValue(20), 0, 0, 0);
        binding.layoutHeader.txtHelp.setLayoutParams(marginLyoutParam);
        binding.txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(28));

        binding.txtHeader.setText(getString(R.string.create_pincode_header));
        binding.txtDesc.setText(getString(R.string.create_pincode_subtitle));

        binding.txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        binding.imgScanComplete.getLayoutParams().width = textSizeConverter.getWidth(56);
        binding.imgScanComplete.getLayoutParams().height = textSizeConverter.getHeight(56);
        binding.imgScanComplete.invalidate();
        marginLyoutParam = (LinearLayout.LayoutParams) binding.txtScanCompleted.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(24), 0, 0);
        binding.txtScanCompleted.setLayoutParams(marginLyoutParam);
        binding.txtScanCompleted.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(20));

    }

    private void callRegisterApi() {
        LoaderUtil.showLoader(CreatePinCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        logger.log("Otpview text: " + binding.otpView.getText().toString());
        SharedPreferences securePreferences = new SecurePreferences(this);
        String url = "";
        if (BuildConfig.FLAVOR.equals("dev")) {
            url = new AllUrls().getDevMainURL();
        } else {
            url = new AllUrls().getProdMainURL();
        }
        logger.log("Registartion:register api detail: " + firstName + "-" + lastName + "-" + username + "-" + pincode + "-" + binding.otpView.getText().toString() + "-" +
                securePreferences.getString(getString(R.string.company_uuid), "") + "-" + otp + "-" +
                securePreferences.getString(getString(R.string.otp_token), ""));
        APIClient.getClient(url, securePreferences.getString(getString(R.string.register_token), "")).create(APIInterface.class)
                .callRegistration(firstName, lastName, username, pincode, binding.otpView.getText().toString(),
                        securePreferences.getString(getString(R.string.company_uuid), ""), otp,
                        securePreferences.getString(getString(R.string.otp_token), ""))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(CreatePinCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        try {
//                            String jsonResponse = response.body().string();
                            logger.log("Otpview text: " + response.body());
                            logger.log("Registartion:register api response:" + response.body());
                            String jsonResponse = response.body().string();
                            logger.log("Otpview text: " + jsonResponse + "  " + response.body());
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.getBoolean("success")) {
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
                                    logger.log("login:regi" + key);
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
                                    }catch (Exception e){

                                    }
                                    // Use these values as needed
                                }
//                                JSONObject company = endpoints.getJSONObject(companuUUID);
//                                JSONObject jsonObject = new JSONObject(MyAppApplication.loadJSONFromAsset(getApplicationContext()));
//                                JSONObject company=companyJasonObject.getJSONObject(registerData.getCompanyUuid());
//                                String facematch = company.getString("facematch");
//                                String amlSearch = company.getString("aml_search");
//                                String amlDetail = company.getString("aml_detail");
//                                String imageCrop = company.getString("image_crop");
//                                securePreferences.edit().putString(getString(R.string.backend_base_url), company.getString("backend")).apply();
//                                securePreferences.edit().putString(getString(R.string.face_match_base_url), facematch.substring(0, facematch.lastIndexOf("/") + 1)).apply();
//                                securePreferences.edit().putString(getString(R.string.aml_base_url), amlSearch.substring(0, amlSearch.lastIndexOf("/") + 1)).apply();
//                                securePreferences.edit().putString(getString(R.string.aml_detail), amlDetail.substring(0, amlDetail.lastIndexOf("/") + 1)).apply();
//                                securePreferences.edit().putString(getString(R.string.image_crop_base_url), imageCrop.substring(0, imageCrop.lastIndexOf("/") + 1)).apply();

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
                                    }
                                }
                                logger.log("Registartion:register api response body:" + registerData);
//                                securePreferences.edit().putString(getString(R.string.register_token), registerData.getToken()).apply();
//                                securePreferences.edit().putString(getString(R.string.register_name), registerData.getName()).apply();
                                binding.lytRegisterSuccessfully.setVisibility(View.VISIBLE);
                            } else {
                                try {
                                    SuccessFailureDialog.showFailureDialog(CreatePinCodeActivity.this, textSizeConverter, "Failed to register account", "", false, new DialogSuccessListener() {
                                        @Override
                                        public void OnSuccessClick() {
                                            retryResetPin();
                                        }

                                        @Override
                                        public void OnFailClick() {
                                            retryResetPin();
                                        }
                                    });
                                } catch (Exception e) {

                                }

                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        LoaderUtil.hideLoader(CreatePinCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
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
        Intent intent = new Intent(CreatePinCodeActivity.this, MainDrawerScreen.class);
//        Intent intent = new Intent(CreatePinCodeActivity.this, LoginAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
