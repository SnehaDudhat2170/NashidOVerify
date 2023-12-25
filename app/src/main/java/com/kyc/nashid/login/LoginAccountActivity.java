package com.kyc.nashid.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
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
import com.kyc.nashid.databinding.ActivityRegisterAccountBinding;
import com.kyc.nashid.login.networking.APIClient;
import com.kyc.nashid.login.networking.APIInterface;
import com.kyc.nashid.login.networking.AllUrls;
import com.kyc.nashid.login.utils.LoaderUtil;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashidmrz.Logger;
import com.kyc.nashid.R;
import com.kyc.nashid.login.utils.SuccessFailureDialog;
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

public class LoginAccountActivity extends AppCompatActivity {
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
        binding.txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginAccountActivity.this, EnterCompanyCodeActivity.class);
                startActivity(intent);
            }
        });
        binding.phoneCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerType = "phone";
                updateUI();
            }
        });
        binding.emailCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerType = "email";
                updateUI();
            }
        });
        binding.cardSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneEmail = "";
                if (binding.edtEmailPhone.getText().toString().isEmpty()){
                    if (registerType.equalsIgnoreCase("phone")) {
                        SuccessFailureDialog.showFailureDialog(LoginAccountActivity.this, textSizeConverter, getString(R.string.phone_failure), registerType, false, null);
                    }else{
                        SuccessFailureDialog.showFailureDialog(LoginAccountActivity.this, textSizeConverter, getString(R.string.email_failure), registerType, false, null);
                    }
                }else {
                    if (registerType.equalsIgnoreCase("phone")) {
                        phoneEmail = binding.countryCode.getSelectedCountryCodeWithPlus() + binding.edtEmailPhone.getText().toString();
                    } else {
                        phoneEmail = binding.edtEmailPhone.getText().toString();
                    }
                    logger.log("otpverification-0--: " + "    " + binding.edtEmailPhone.getText().toString());
//                callLoginAPI();
                    if (registerType.equalsIgnoreCase("phone")) {
                        if (isValidMobile(phoneEmail)) {
                            callCheckUserAPI();
                        } else {
                            SuccessFailureDialog.showFailureDialog(LoginAccountActivity.this, textSizeConverter, "phoneemailfail", registerType, false, null);
//                        Toast.makeText(getApplicationContext(), "invalide", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (isValidMail(phoneEmail)) {
                            callCheckUserAPI();
                        } else {
                            SuccessFailureDialog.showFailureDialog(LoginAccountActivity.this, textSizeConverter, "phoneemailfail", registerType, false, null);
//                        Toast.makeText(getApplicationContext(), "invalide", Toast.LENGTH_SHORT).show();
                        }
                    }
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
        updateUI();
    }

    private void setLayoutAndTextSize() {
        textSizeConverter = new TextSizeConverter(getApplicationContext());
        textSizeConverter.changeStatusBarColor(LoginAccountActivity.this);

        binding.lytPhoneEmail.setVisibility(View.VISIBLE);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.lytHeaderMain.setVisibility(View.INVISIBLE);
        layoutParams2 = binding.imgLogo.getLayoutParams();
        layoutParams2.width = textSizeConverter.getWidth(50);
        layoutParams2.height = textSizeConverter.getHeight(44);
        binding.imgLogo.setLayoutParams(layoutParams2);
        binding.imgLogo.setVisibility(View.VISIBLE);
        binding.layoutHeader.txtHelp.setText(getString(R.string.login_header));
        binding.layoutHeader.txtHelp.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.lyutEmailPhone.setGravity(Gravity.START );
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(17));

        int padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.lytMainRegister.setPadding(padding, padding, padding, padding);

        padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.btnSendOtp.setPadding(0, padding, 0, padding);
        binding.btnSendOtp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.cardSendOtp.setRadius(textSizeConverter.calculateRadius(8));
        LinearLayout.LayoutParams marginLyoutParam;
        marginLyoutParam = (LinearLayout.LayoutParams) binding.imgLogo.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(30), 0, textSizeConverter.getPaddingORMarginValue(30));
        binding.imgLogo.setLayoutParams(marginLyoutParam);
        marginLyoutParam = (LinearLayout.LayoutParams) binding.txtHeader.getLayoutParams();
        marginLyoutParam.setMargins(0, 0, 0, textSizeConverter.getPaddingORMarginValue(14));
        binding.txtHeader.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.layoutHeader.txtHelp.getLayoutParams();
        marginLyoutParam.setMargins(textSizeConverter.getPaddingORMarginValue(20), 0, 0, 0);
        binding.layoutHeader.txtHelp.setLayoutParams(marginLyoutParam);
        binding.txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(28));

        binding.txtDesc.setPadding(0,0,0,textSizeConverter.getPaddingORMarginValue(38));
        binding.txtHeader.setText(getString(R.string.login_title));
        binding.txtDesc.setText(getString(R.string.login_subtitle));

        binding.txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.txtPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.txtEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        binding.edtEmailPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.countryCode.setTextSize((int) textSizeConverter.getTextSize(16));
        binding.countryCode.setTextVisible(10,20);
        int textPadding = textSizeConverter.getPaddingORMarginValue(16);
        binding.txtPhone.setPadding(0, textPadding, 0, textPadding);
        binding.txtEmail.setPadding(0, textPadding, 0, textPadding);

        binding.emailCardview.setRadius(textSizeConverter.calculateRadius(8));
        binding.phoneCardview.setRadius(textSizeConverter.calculateRadius(8));
        binding.cardEnterPhoneEmail.setRadius(textSizeConverter.calculateRadius(8));

        textPadding = textSizeConverter.getPaddingORMarginValue(8);
        binding.lytEnterPhoneEmail.setPadding(0, textPadding, 0, textPadding);
        binding.countryCode.setPadding(textSizeConverter.getPaddingORMarginValue(24), 0, 0, 0);

        layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(10), 0, 0);
        binding.cardEnterPhoneEmail.setLayoutParams(layoutParams);
        binding.txtResendDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.txtResend.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        binding.txtResendDesc.setPadding(0, 0, 0, textSizeConverter.getPaddingORMarginValue(24));
        binding.txtResend.setPadding(0, 0, 0, textSizeConverter.getPaddingORMarginValue(24));
        binding.lytResend.setVisibility(View.VISIBLE);

        binding.btnSendOtp.setText(getString(R.string.login_next));
    }

    private void updateUI() {
        binding.edtEmailPhone.setText("");
        if (registerType.equalsIgnoreCase("phone")) {

            binding.txtPhone.setTextColor(Color.WHITE);
            binding.txtEmail.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_color));
            binding.phoneCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.button_color));
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
            binding.txtPhone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_color));
            binding.emailCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.button_color));
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

    private void callCheckUserAPI() {
        LoaderUtil.showLoader(LoginAccountActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        SharedPreferences securePreferences = new SecurePreferences(this);
        String url="";
        if (BuildConfig.FLAVOR.equals("dev")){
            url= new AllUrls().getDevMainURL();
        }else{
            url= new AllUrls().getProdMainURL();
        }
        logger.log("otpverification: " + url + "    " + binding.edtEmailPhone.getText().toString());
        APIClient.getClient(url, "").create(APIInterface.class).callCheckUser(binding.edtEmailPhone.getText().toString()).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(LoginAccountActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        logger.log("otpverification: "+response.isSuccessful());
                        if (response.isSuccessful()) {
                            try {
                                String jsonResponse = response.body().string();
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                boolean success = jsonObject.getBoolean("success");
                                logger.log("response---: " + jsonResponse+"\n"+success);
                                if (success) {
                                    SuccessFailureDialog.showFailureDialog(LoginAccountActivity.this,textSizeConverter,"apifail",registerType,false,null);
//                                    showFailureDialog("fail");
                                } else {

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

                            Intent intent = new Intent(LoginAccountActivity.this, EnterLoginPinCodeActivity.class);
                            intent.putExtra("name", binding.edtEmailPhone.getText().toString());
                            intent.putExtra("loginType", registerType);
                            startActivity(intent);
                            logger.log("response:fail ");
                            // Handle the case where the HTTP response is not successful (e.g., 404, 500)
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        logger.log("response:jsonexception " + t);
                        LoaderUtil.hideLoader(LoginAccountActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
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

//    private void showFailureDialog(String failureMessage) {
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
//        if (failureMessage.equalsIgnoreCase("")) {
//            if (registerType.equalsIgnoreCase("phone")) {
//
//                binding.txtScanCompleted.setText(getString(R.string.phone_error));
//            } else {
//                binding.txtScanCompleted.setText(getString(R.string.email_error));
//            }
//        } else {
//            if (registerType.equalsIgnoreCase("phone")) {
//
//                binding.txtScanCompleted.setText(getString(R.string.login_phone_error));
//            } else {
//                binding.txtScanCompleted.setText(getString(R.string.login_email_error));
//            }
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
        Intent intent = new Intent(LoginAccountActivity.this, OTPConfirmActivity.class);
        intent.putExtra("loginType", registerType);
        intent.putExtra("username", binding.edtEmailPhone.getText().toString());
        startActivity(intent);
    }
}
