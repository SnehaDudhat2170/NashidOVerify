package com.kyc.nashid.login;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.kyc.nashid.BuildConfig;
import com.kyc.nashid.databinding.ActivityRegisterAccountBinding;
import com.kyc.nashid.databinding.DialogEmailPhoneConfirmationBinding;
import com.kyc.nashid.login.networking.APIClient;
import com.kyc.nashid.login.networking.APIInterface;
import com.kyc.nashid.login.networking.AllUrls;
import com.kyc.nashid.login.utils.LoaderUtil;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashid.login.utils.SuccessFailureDialog;
import com.kyc.nashidmrz.Logger;
import com.kyc.nashid.R;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetAccountActivity extends AppCompatActivity {
    private ActivityRegisterAccountBinding binding;
    private TextSizeConverter textSizeConverter;
    private String registerType = "phone";
    private final Logger logger = Logger.withTag(this.getClass().getSimpleName());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterAccountBinding.inflate(getLayoutInflater());
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
        binding.txtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerType = "phone";
                updateUI();
            }
        });
        binding.txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerType = "email";
                updateUI();
            }
        });
        binding.cardSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                logger.log("otpverification-0--: " + "    " + binding.edtEmailPhone.getText().toString());
//                openConfirmationDialog();
                if (binding.edtEmailPhone.getText().toString().isEmpty()){
                    if (registerType.equalsIgnoreCase("phone")) {
                        SuccessFailureDialog.showFailureDialog(ResetAccountActivity.this, textSizeConverter, getString(R.string.phone_failure), registerType, false, null);
                    }else{
                        SuccessFailureDialog.showFailureDialog(ResetAccountActivity.this, textSizeConverter, getString(R.string.email_failure), registerType, false, null);
                    }
                }else {
                    String phoneEmail = "";
                    if (registerType.equalsIgnoreCase("phone")) {
                        phoneEmail = binding.countryCode.getSelectedCountryCodeWithPlus() + binding.edtEmailPhone.getText().toString();
                    } else {
                        phoneEmail = binding.edtEmailPhone.getText().toString();
                    }
                    logger.log("otpverification-0--: " + "    " + binding.edtEmailPhone.getText().toString());
//                callLoginAPI();
                    if (registerType.equalsIgnoreCase("phone")) {
                        if (isValidMobile(phoneEmail)) {
                            logger.log("otpverification-0--: " + "    " + binding.edtEmailPhone.getText().toString());
//                callCompanyDomainAPI();
                            openConfirmationDialog();
                        } else {
                            SuccessFailureDialog.showFailureDialog(ResetAccountActivity.this, textSizeConverter, "phoneemailfail", registerType, false, null);
//                        Toast.makeText(getApplicationContext(), "invalide", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (isValidMail(phoneEmail)) {
                            logger.log("otpverification-0--: " + "    " + binding.edtEmailPhone.getText().toString());
//                callCompanyDomainAPI();
                            openConfirmationDialog();
                        } else {
                            SuccessFailureDialog.showFailureDialog(ResetAccountActivity.this, textSizeConverter, "phoneemailfail", registerType, false, null);
//                        Toast.makeText(getApplicationContext(), "invalide", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void initView() {
        registerType = getIntent().getStringExtra("loginType");
        SVG svg = null;
        try {
            svg = SVG.getFromResource(getResources(), R.raw.back);
            binding.layoutHeader.imgBack.setSVG(svg);
        } catch (SVGParseException e) {

        }
        setLayoutAndTextSize();
        updateUI();
    }

    private void setLayoutAndTextSize() {
        textSizeConverter = new TextSizeConverter(getApplicationContext());
        textSizeConverter.changeStatusBarColor(ResetAccountActivity.this);

        binding.phoneCardview.setVisibility(View.GONE);
        binding.emailCardview.setVisibility(View.GONE);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.imgLogo.setVisibility(View.GONE);
        binding.layoutHeader.txtHelp.setText(getString(R.string.reset_btn_reset));
        binding.layoutHeader.txtHelp.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(17));

        int padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.lytMainRegister.setPadding(padding, padding, padding, padding);
        binding.countryCode.setTextVisible(10,20);
        padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.btnSendOtp.setPadding(0, padding, 0, padding);
        binding.btnSendOtp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.cardSendOtp.setRadius(textSizeConverter.calculateRadius(8));
        LinearLayout.LayoutParams marginLyoutParam = (LinearLayout.LayoutParams) binding.txtHeader.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(20), 0, textSizeConverter.getPaddingORMarginValue(14));
        binding.txtHeader.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.layoutHeader.txtHelp.getLayoutParams();
        marginLyoutParam.setMargins(textSizeConverter.getPaddingORMarginValue(20), 0, 0, 0);
        binding.layoutHeader.txtHelp.setLayoutParams(marginLyoutParam);
        binding.txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(28));

        binding.txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.txtPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.txtEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        binding.edtEmailPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.countryCode.setTextSize((int) textSizeConverter.getTextSize(16));
        int textPadding = textSizeConverter.getPaddingORMarginValue(20);
        binding.txtPhone.setPadding(0, textPadding, 0, textPadding);
        binding.txtEmail.setPadding(0, textPadding, 0, textPadding);

        binding.emailCardview.setRadius(textSizeConverter.calculateRadius(8));
        binding.phoneCardview.setRadius(textSizeConverter.calculateRadius(8));
        binding.cardEnterPhoneEmail.setRadius(textSizeConverter.calculateRadius(8));

        textPadding = textSizeConverter.getPaddingORMarginValue(12);
        binding.lytEnterPhoneEmail.setPadding(0, textPadding, 0, textPadding);
        binding.countryCode.setPadding(textSizeConverter.getPaddingORMarginValue(24), 0, 0, 0);

        layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(10), 0, 0);
        binding.cardEnterPhoneEmail.setLayoutParams(layoutParams);
        binding.txtHeader.setText(getString(R.string.reset_pin_title));
        binding.txtDesc.setText(getString(R.string.reset_pin_subtitle));
        binding.btnSendOtp.setText(getString(R.string.reset_btn_reset));
    }

    private void updateUI() {
        binding.edtEmailPhone.setText("");
        if (registerType.equalsIgnoreCase("phone")) {

            binding.txtPhone.setTextColor(Color.WHITE);
            binding.txtEmail.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.register_tab_text));
            binding.phoneCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected));
            binding.emailCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.unselected));
            binding.edtEmailPhone.setHint(getString(R.string.phone_hint));
            binding.countryCode.setVisibility(View.VISIBLE);
            binding.edtEmailPhone.setPadding(0, 0, 0, 0);
            binding.edtEmailPhone.setInputType(InputType.TYPE_CLASS_PHONE);
//            binding.edtEmailPhone.setText("89898998");
            int maxLength = 10; // Set the maximum length here
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(maxLength);
            binding.edtEmailPhone.setFilters(filters);
        } else {
            binding.txtEmail.setTextColor(Color.WHITE);
            binding.txtPhone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.register_tab_text));
            binding.emailCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected));
            binding.phoneCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.unselected));
            binding.edtEmailPhone.setHint(getString(R.string.email_hint));
            binding.countryCode.setVisibility(View.GONE);
            binding.edtEmailPhone.setPadding(textSizeConverter.getPaddingORMarginValue(24), 0, 0, 0);
            binding.edtEmailPhone.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//            binding.edtEmailPhone.setText("sneha@nashid.io");
            int maxLength = 100; // Set the maximum length here
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(maxLength);
            binding.edtEmailPhone.setFilters(filters);
        }
    }

    private void callGetOTPAPI() {
        LoaderUtil.showLoader(ResetAccountActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        SharedPreferences securePreferences = new SecurePreferences(this);
        String url="";
        logger.log("API:getotp");
        if (BuildConfig.FLAVOR.equals("dev")){
            url= new AllUrls().getDevMainURL();
        }else{
            url= new AllUrls().getProdMainURL();
        }
        APIClient.getClient(url, "").create(APIInterface.class).callOTP(binding.edtEmailPhone.getText().toString()).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(ResetAccountActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        logger.log("API:getotp "+response.isSuccessful());
                        if (response.isSuccessful()) {
                            try {
                                String jsonResponse = response.body().string();
                                logger.log("otp response reset: " + jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    String token = data.getString("token");
                                    String uuid = data.getString("uuid");
                                    String resetPasswordUrl = data.getString("reset_password_url");
                                    logger.log("response: token " + token);
                                    securePreferences.edit().putString(getString(R.string.reset_otp_token), token).apply();
                                    securePreferences.edit().putString(getString(R.string.reset_base_url), resetPasswordUrl.substring(0, resetPasswordUrl.lastIndexOf("/") + 1)).apply();
                                    openOTPConfirmActivity();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Invalide phone or email id", Toast.LENGTH_SHORT).show();
                                }
                                // Now you have the raw JSON response as a string (jsonResponse)
                                // You can parse it or work with it as needed.
                            } catch (IOException e) {
                                logger.log("response:exception " + e);
                                e.printStackTrace();
                            } catch (JSONException e) {
                                logger.log("response:jsonexception " + e);
                            }
                        } else {
                            logger.log("response:fail ");
                            // Handle the case where the HTTP response is not successful (e.g., 404, 500)
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        logger.log("API:getotp "+t);
                        LoaderUtil.hideLoader(ResetAccountActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        // Handle the case where the API call failed (e.g., network issues)
                    }

                });
    }

    private void openConfirmationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogEmailPhoneConfirmationBinding binding = DialogEmailPhoneConfirmationBinding.inflate(dialog.getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.dialogHeader.getLayoutParams();
        int padding = textSizeConverter.getPaddingORMarginValue(16);
        layoutParams.setMargins(padding, padding, padding, 0);
        binding.dialogHeader.setLayoutParams(layoutParams);

//        binding.mainLayouyt.setPadding(padding,padding,padding,padding);
        ViewGroup.LayoutParams layoutParams2 = binding.cardConfirmation.getLayoutParams();
        layoutParams2.width = textSizeConverter.getWidth(280);
//        layoutParams2.height = textSizeConverter.getHeight(44);
        binding.cardConfirmation.setLayoutParams(layoutParams2);

        binding.cardConfirmation.setRadius(textSizeConverter.calculateRadius(14));
        binding.dialogHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(18));
        binding.dialogSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(14));
        binding.btnNo.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.btnYes.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        layoutParams = (LinearLayout.LayoutParams) binding.dialogSubtitle.getLayoutParams();
        layoutParams.setMargins(padding, padding, padding, padding);
        binding.dialogSubtitle.setLayoutParams(layoutParams);

        padding = textSizeConverter.getPaddingORMarginValue(12);
        layoutParams = (LinearLayout.LayoutParams) binding.btnNo.getLayoutParams();
        layoutParams.setMargins(0, padding, 0, padding);
        binding.btnNo.setLayoutParams(layoutParams);

        layoutParams = (LinearLayout.LayoutParams) binding.btnYes.getLayoutParams();
        layoutParams.setMargins(textSizeConverter.getPaddingORMarginValue(24), padding,  textSizeConverter.getPaddingORMarginValue(16), padding);
        binding.btnYes.setLayoutParams(layoutParams);
        String phoneEmail = "";
        if (registerType.equalsIgnoreCase("phone")) {
            phoneEmail = this.binding.countryCode.getSelectedCountryCodeWithPlus() + this.binding.edtEmailPhone.getText().toString();
            binding.dialogHeader.setText(getString(R.string.phone_confirmation));
            binding.dialogSubtitle.setText(getString(R.string.phone_confirm_desc) + "\n" + phoneEmail);
        } else {
            binding.dialogHeader.setText(getString(R.string.email_confirmation));
            phoneEmail = this.binding.edtEmailPhone.getText().toString();
            binding.dialogSubtitle.setText(getString(R.string.email_confirm_desc) + "\n" + phoneEmail);
        }


        String finalPhoneEmail = phoneEmail;
        binding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                logger.log("emailphonestring : " + finalPhoneEmail);
                if (registerType.equalsIgnoreCase("phone")) {
                    if (isValidMobile(finalPhoneEmail)) {
                        callCheckUserAPI();
                    } else {
                        SuccessFailureDialog.showFailureDialog(ResetAccountActivity.this, textSizeConverter, "phoneemailfail", registerType, false, null);
//                        Toast.makeText(getApplicationContext(), "invalide", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isValidMail(finalPhoneEmail)) {
                        callCheckUserAPI();
                    } else {
                        SuccessFailureDialog.showFailureDialog(ResetAccountActivity.this, textSizeConverter, "phoneemailfail", registerType, false, null);
//                        Toast.makeText(getApplicationContext(), "invalide", Toast.LENGTH_SHORT).show();
                    }
                }
//                callCompanyDomainAPI();
            }
        });
        binding.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCancelable(false);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void callCheckUserAPI() {
        LoaderUtil.showLoader(ResetAccountActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        SharedPreferences securePreferences = new SecurePreferences(this);
        String url="";
        logger.log("API:checkuser");
        if (BuildConfig.FLAVOR.equals("dev")){
            url= new AllUrls().getDevMainURL();
        }else{
            url= new AllUrls().getProdMainURL();
        }
        APIClient.getClient(url, "").create(APIInterface.class).callCheckUser(binding.edtEmailPhone.getText().toString()).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(ResetAccountActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        logger.log("API:checkuser "+ response.isSuccessful());
                        if (response.isSuccessful()) {
                            try {
                                String jsonResponse = response.body().string();
                                logger.log("response: " + jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {
                                    SuccessFailureDialog.showFailureDialog(ResetAccountActivity.this, textSizeConverter, "apifail", registerType, false, null);
                                } else {
                                    callGetOTPAPI();
                                }
                                // Now you have the raw JSON response as a string (jsonResponse)
                                // You can parse it or work with it as needed.
                            } catch (IOException e) {
                                logger.log("response:exception " + e);
                                e.printStackTrace();
                            } catch (JSONException e) {
                                logger.log("response:jsonexception " + e);
                            }
                        } else {
                            logger.log("API:checkuser failed" );
                            callGetOTPAPI();
//                            SuccessFailureDialog.showFailureDialog(ResetAccountActivity.this, textSizeConverter, "apifail", registerType, false, null);
                            logger.log("response:fail ");
                            // Handle the case where the HTTP response is not successful (e.g., 404, 500)
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        logger.log("API:checkuser onfailure "+t);
                        LoaderUtil.hideLoader(ResetAccountActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        // Handle the case where the API call failed (e.g., network issues)
                    }

                });
    }

    private boolean isValidMail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";

        // Use regex to check if the email matches the pattern
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);

        // Check the length of the local part (before '@') and domain part (after '@')
        if (matcher.matches()) {
            String[] parts = email.split("@");
            String localPart = parts[0];
            String domainPart = parts[1];

            // Check if the local part length is less than or equal to 64 characters
            if (localPart.length() <= 64 && domainPart.length() <= 255) {
                return true; // Email is valid
            }
        }

        return false;
    }

    private boolean isValidMobile(String phone) {
        // Define a regex pattern for Omani phone numbers
        String omanPhoneNumberPattern = "^(\\+968|968|0)[2379]\\d{7}$";

        // Use regex to check if the phone number matches the pattern
        Pattern pattern = Pattern.compile(omanPhoneNumberPattern);
        Matcher matcher = pattern.matcher(phone);

        return matcher.matches();
    }

//    private void showFailureDialog() {
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
//        binding.txtScanCompleted.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(18));
//        if (registerType.equalsIgnoreCase("phone")) {
//
//            binding.txtScanCompleted.setText(getString(R.string.phone_error));
//        } else {
//            binding.txtScanCompleted.setText(getString(R.string.email_error));
//        }
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

    private void openOTPConfirmActivity() {
        Intent intent = new Intent(ResetAccountActivity.this, ResetPINActivity.class);
        intent.putExtra("loginType", registerType);
        intent.putExtra("username", binding.edtEmailPhone.getText().toString());
        startActivity(intent);
    }
}
