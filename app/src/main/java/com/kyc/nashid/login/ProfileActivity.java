package com.kyc.nashid.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.kyc.nashid.BuildConfig;
import com.kyc.nashid.databinding.ActivityProfileBinding;
import com.kyc.nashid.login.networking.APIClient;
import com.kyc.nashid.login.networking.APIInterface;
import com.kyc.nashid.login.networking.AllUrls;
import com.kyc.nashid.login.utils.DialogSuccessListener;
import com.kyc.nashid.login.utils.LoaderUtil;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashid.login.utils.SuccessFailureDialog;
import com.kyc.nashidmrz.Logger;
import com.kyc.nashid.R;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private TextSizeConverter textSizeConverter;
    private boolean updateClicked = false;
    private Logger logger = Logger.withTag(this.getClass().getSimpleName());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initClick();
    }

    private void initClick() {
        binding.layoutHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.cardBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateClicked) {

                    callUpdateProfile();
                } else {
                    updateClicked = true;
                    setFocusable(true);
                    updateTextColor(Color.BLACK);
                    binding.txtBtnNext.setText(getString(R.string.btn_save));
                }
            }
        });
    }

    private void initView() {
        SVG svg = null;
        try {
            svg = SVG.getFromResource(getResources(), R.raw.back);
            binding.layoutHeader.imgBack.setSVG(svg);
        } catch (SVGParseException e) {

        }
        setLayoutAndTextSize();
        callGetProfileDetail();
    }

    private void setLayoutAndTextSize() {
        binding.lytProfile.setVisibility(View.GONE);
        textSizeConverter = new TextSizeConverter(getApplicationContext());
        textSizeConverter.changeStatusBarColor(ProfileActivity.this);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        int padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.lytMainProfile.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.txtHelp.setText(getString(R.string.header_profile));
        binding.layoutHeader.txtHelp.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(17));
        binding.countryCode.setTextVisible(10,20);

        padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.txtBtnNext.setPadding(0, padding, 0, padding);
        binding.txtBtnNext.setText(getString(R.string.profile_update));
        binding.txtBtnNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.cardBtnNext.setRadius(textSizeConverter.calculateRadius(8));

        LinearLayout.LayoutParams marginLyoutParam = (LinearLayout.LayoutParams) binding.txtHeader.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(20), 0, textSizeConverter.getPaddingORMarginValue(30));
        binding.txtHeader.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.layoutHeader.txtHelp.getLayoutParams();
        marginLyoutParam.setMargins(textSizeConverter.getPaddingORMarginValue(20), 0, 0, 0);
        binding.layoutHeader.txtHelp.setLayoutParams(marginLyoutParam);
        binding.txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(28));

        binding.txtFirstname.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(12));
        binding.txtLastname.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(12));
        binding.txtPhoneno.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(12));
        binding.txtEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(12));

        float textsize = textSizeConverter.getTextSize(16);
        binding.txtFirstnameValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        binding.txtLastnameValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        binding.edtPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        binding.countryCode.setTextSize((int) textsize);
        binding.edtEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);

        int margin = textSizeConverter.getPaddingORMarginValue(10);
        marginLyoutParam = (LinearLayout.LayoutParams) binding.divider1.getLayoutParams();
        marginLyoutParam.setMargins(0, margin, 0, margin);
        binding.divider1.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.divider2.getLayoutParams();
        marginLyoutParam.setMargins(0, margin, 0, margin);
        binding.divider2.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.divider3.getLayoutParams();
        marginLyoutParam.setMargins(0, margin, 0, margin);
        binding.divider3.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.divider4.getLayoutParams();
        marginLyoutParam.setMargins(0, margin, 0, margin);
        binding.divider4.setLayoutParams(marginLyoutParam);

        setFocusable(false);
    }

    private void callGetProfileDetail() {
        LoaderUtil.showLoader(ProfileActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        SharedPreferences securePreferences = new SecurePreferences(this);
        String url="";
        if (BuildConfig.FLAVOR.equals("dev")){
            url= new AllUrls().getDevMainURL();
        }else{
            url= new AllUrls().getProdMainURL();
        }
        APIClient.getClient(url, securePreferences.getString(getString(R.string.nashid_token), "")).create(APIInterface.class).callGetProfile().
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(ProfileActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        if (response.isSuccessful()) {
                            try {
                                String jsonResponse = response.body().string();
                                logger.log("response: " + jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.getBoolean("success")) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    String firstName = jsonObject1.getString("first_name");
                                    String lastName = jsonObject1.getString("last_name");
                                    String email = jsonObject1.getString("email");
                                    String phone = jsonObject1.getString("phone");
                                    String profilePhoto = jsonObject1.getString("profile_photo_url");
                                    binding.txtFirstnameValue.setText(firstName);
                                    binding.txtLastnameValue.setText(lastName);

                                    if (email.equalsIgnoreCase("null")) {
                                        binding.lytEmail.setVisibility(View.GONE);
                                    } else {
                                        binding.edtEmail.setText(email);
                                    }
                                    if (phone.equalsIgnoreCase("null")) {
                                        binding.lytPhone.setVisibility(View.GONE);
                                    } else {
                                        binding.edtPhone.setText(phone);
                                    }
                                    binding.lytProfile.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                logger.log("response:jsonexception " + e);
                            }
                        } else {
                            logger.log("response:fail ");
                            // Handle the case where the HTTP response is not successful (e.g., 404, 500)
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        LoaderUtil.hideLoader(ProfileActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        // Handle the case where the API call failed (e.g., network issues)
                    }

                });
    }

    private void callUpdateProfile() {
        LoaderUtil.showLoader(ProfileActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        SharedPreferences securePreferences = new SecurePreferences(this);
        String companyUUID=securePreferences.getString(getString(R.string.company_uuid),"");
        logger.log("update otpverification: " + securePreferences.getString(companyUUID+getString(R.string.backend_base_url), "") + "    ");
        String phoneEmail = "";
        if (binding.lytPhone.getVisibility() == View.VISIBLE) {
            phoneEmail = this.binding.edtPhone.getText().toString();
        }
        String url="";
        if (BuildConfig.FLAVOR.equals("dev")){
            url= new AllUrls().getDevMainURL();
        }else{
            url= new AllUrls().getProdMainURL();
        }
        APIClient.getClient(url, securePreferences.getString(getString(R.string.nashid_token), "")).create(APIInterface.class)
                .callUpdateProfile(binding.txtFirstnameValue.getText().toString() , binding.txtLastnameValue.getText().toString(), binding.edtEmail.getText().toString(), phoneEmail)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(ProfileActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        logger.log("update " + response);
                        if (response.isSuccessful()) {
                            try {
                                String jsonResponse = response.body().string();
                                logger.log("update response: " + jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.getBoolean("success")) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    String firstName = jsonObject1.getString("first_name");
                                    String lastName = jsonObject1.getString("last_name");
                                    String email = jsonObject1.getString("email");
                                    String phone = jsonObject1.getString("phone");
                                    String profilePhoto = jsonObject1.getString("profile_photo_url");
//                                    binding.txtFirstnameValue.setText(name);
//                                    binding.txtLastnameValue.setText(name);
                                    binding.txtFirstnameValue.setText(firstName);
                                    binding.txtLastnameValue.setText(lastName);
                                    if (email.equalsIgnoreCase("null")) {
                                        binding.lytEmail.setVisibility(View.GONE);
                                    } else {
                                        binding.edtEmail.setText(email);
                                    }
                                    if (phone.equalsIgnoreCase("null")) {
                                        binding.lytPhone.setVisibility(View.GONE);
                                    } else {
                                        binding.edtPhone.setText(phone);
                                    }
                                    binding.lytProfile.setVisibility(View.VISIBLE);
                                    SuccessFailureDialog.showFailureDialog(ProfileActivity.this, textSizeConverter, getString(R.string.profile_updated_success), "", true, new DialogSuccessListener() {
                                        @Override
                                        public void OnSuccessClick() {
                                            updateClicked = false;
                                            setFocusable(false);
                                            updateTextColor(ContextCompat.getColor(getApplicationContext(), R.color.profile_text_color));
                                            ProfileActivity.this.binding.txtBtnNext.setText(getString(R.string.profile_update));
                                        }

                                        @Override
                                        public void OnFailClick() {

                                        }
                                    });
                                }
                            } catch (Exception e) {
                                logger.log("response:jsonexception " + e);
                            }
                        } else {
                            logger.log("response:fail ");
                            // Handle the case where the HTTP response is not successful (e.g., 404, 500)
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        LoaderUtil.hideLoader(ProfileActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        // Handle the case where the API call failed (e.g., network issues)
                    }

                });
    }

    private void setFocusable(boolean isFocusable) {
        binding.txtFirstnameValue.setFocusableInTouchMode(isFocusable);
        binding.txtFirstnameValue.setFocusable(isFocusable);
        binding.txtLastnameValue.setFocusableInTouchMode(isFocusable);
        binding.txtLastnameValue.setFocusable(isFocusable);
        if (binding.lytPhone.getVisibility() == View.VISIBLE) {
            binding.edtPhone.setFocusable(false);
        }
        if (binding.lytEmail.getVisibility() == View.VISIBLE) {
            binding.edtEmail.setFocusable(false);
        }
    }

    private void updateTextColor(int color) {
        binding.txtFirstnameValue.setTextColor(color);
        binding.txtLastnameValue.setTextColor(color);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("name",binding.txtFirstnameValue.getText().toString()+" "+binding.txtLastnameValue.getText().toString() );
        setResult(RESULT_OK, intent);
        finish();
    }
}
