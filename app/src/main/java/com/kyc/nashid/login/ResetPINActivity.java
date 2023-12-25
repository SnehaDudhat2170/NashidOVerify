package com.kyc.nashid.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.kyc.nashid.BuildConfig;
import com.kyc.nashid.R;
import com.kyc.nashid.databinding.ActivityOtpConfirmAccountBinding;
import com.kyc.nashid.login.customview.PinField;
import com.kyc.nashid.login.networking.APIClient;
import com.kyc.nashid.login.networking.APIInterface;
import com.kyc.nashid.login.networking.AllUrls;
import com.kyc.nashid.login.utils.DialogSuccessListener;
import com.kyc.nashid.login.utils.LoaderUtil;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashidmrz.Logger;
import com.kyc.nashid.login.utils.SuccessFailureDialog;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPINActivity extends AppCompatActivity {

    private ActivityOtpConfirmAccountBinding binding;
    private TextSizeConverter textSizeConverter;
    private CountDownTimer cTimer = null;
    private String registerType = "phone";
    private String userName = "";
    private final Logger logger = Logger.withTag(this.getClass().getSimpleName());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpConfirmAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initClick();

    }

    private void initClick() {
        binding.txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callCompanyDomainAPI();
            }
        });
        binding.cardBtnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callVerifyOTPAPI();
            }
        });
        binding.otpView.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NonNull String enteredText) {
                callVerifyOTPAPI();
                return true;
            }
        });
        binding.layoutHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

        registerType = getIntent().getStringExtra("loginType");
        userName = getIntent().getStringExtra("username");
        if (registerType.equalsIgnoreCase("phone")) {
            binding.lytPhoneOtp.setVisibility(View.VISIBLE);
            binding.lytEmailOtp.setVisibility(View.GONE);
            binding.cardBtnValidate.setVisibility(View.GONE);
        } else {
            binding.lytPhoneOtp.setVisibility(View.GONE);
            binding.lytEmailOtp.setVisibility(View.VISIBLE);
            binding.cardBtnValidate.setVisibility(View.VISIBLE);
        }
        setLayoutAndTextSize();
        startCountDownTimer();

    }

    private void startCountDownTimer() {
        cTimer = new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.txtTimer.setText("0:" + millisUntilFinished / 1000);
                // logic to set the EditText could go here
            }

            public void onFinish() {
                binding.txtTimer.setVisibility(View.GONE);
                binding.lytResend.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    private void setLayoutAndTextSize() {

        textSizeConverter = new TextSizeConverter(getApplicationContext());
        textSizeConverter.changeStatusBarColor(ResetPINActivity.this);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.txtHelp.setText(getString(R.string.reset_btn_reset));
        binding.layoutHeader.txtHelp.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(17));

        int padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.lytMainOtpConfirm.setPadding(padding, padding, padding, padding);

        padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.btnOtpValidate.setPadding(0, padding, 0, padding);
        binding.btnOtpValidate.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.cardBtnValidate.setRadius(textSizeConverter.calculateRadius(8));
        binding.cardEmailCode.setRadius(textSizeConverter.calculateRadius(8));

        LinearLayout.LayoutParams marginLyoutParam = (LinearLayout.LayoutParams) binding.txtHeader.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(20), 0, textSizeConverter.getPaddingORMarginValue(14));
        binding.txtHeader.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.layoutHeader.txtHelp.getLayoutParams();
        marginLyoutParam.setMargins(textSizeConverter.getPaddingORMarginValue(20), 0, 0, 0);
        binding.layoutHeader.txtHelp.setLayoutParams(marginLyoutParam);
        binding.txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(28));

        binding.txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.txtTimer.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.edtEmailCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        int textPadding = textSizeConverter.getPaddingORMarginValue(10);
        binding.txtTimer.setPadding(0, textPadding, 0, textPadding);

        layoutParams2 = binding.imgLogo.getLayoutParams();
        layoutParams2.width = textSizeConverter.getWidth(50);
        layoutParams2.height = textSizeConverter.getHeight(44);
        binding.imgLogo.setLayoutParams(layoutParams2);
        binding.imgLogo.setVisibility(View.GONE);
        marginLyoutParam = (LinearLayout.LayoutParams) binding.imgLogo.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(50), 0, textSizeConverter.getPaddingORMarginValue(10));
        binding.imgLogo.setLayoutParams(marginLyoutParam);

        binding.txtResendDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.txtResend.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        textPadding = textSizeConverter.getPaddingORMarginValue(28);
        binding.txtResendDesc.setPadding(0, textSizeConverter.getPaddingORMarginValue(10), 0, 0);
//        binding.txtResend.setPadding(0, 0, 0, textPadding);

        binding.edtEmailCode.setPadding(textSizeConverter.getPaddingORMarginValue(24), textSizeConverter.getPaddingORMarginValue(12), textSizeConverter.getPaddingORMarginValue(24), textSizeConverter.getPaddingORMarginValue(12));
        binding.txtHeader.setText(getString(R.string.reset_enter_pin_title));
        if (registerType.equalsIgnoreCase("phone")) {
            binding.txtDesc.setText(getString(R.string.reset_enter_pin_subtitle_phone));
        } else {
            binding.txtDesc.setText(getString(R.string.reset_enter_pin_subtitle_email));
        }
        binding.btnOtpValidate.setText(getString(R.string.otp_validate));
    }

    private void callCompanyDomainAPI() {
        LoaderUtil.showLoader(ResetPINActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        SharedPreferences securePreferences = new SecurePreferences(this);
        String url="";
        if (BuildConfig.FLAVOR.equals("dev")){
            url= new AllUrls().getDevMainURL();
        }else{
            url= new AllUrls().getProdMainURL();
        }
        logger.log("otpverification:ortp " + url+ "    " + userName);
        APIClient.getClient(url, "").create(APIInterface.class).callOTP(userName).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(ResetPINActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        String jsonResponse = null;
                        try {
                            jsonResponse = response.body().string();
                        } catch (IOException e) {
                            logger.log("response:++ ");
                        }
                        logger.log("response: " + jsonResponse);
                        if (response.isSuccessful()) {
                            try {

                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {
                                    startCountDownTimer();
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    String token = data.getString("token");
                                    String uuid = data.getString("uuid");
                                    logger.log("response: token " + token);
                                    securePreferences.edit().putString(getString(R.string.reset_otp_token), token).apply();
//                                        openOTPConfirmActivity();
                                } else {
                                }
                                // Now you have the raw JSON response as a string (jsonResponse)
                                // You can parse it or work with it as needed.
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
                        // Handle the case where the API call failed (e.g., network issues)
                        LoaderUtil.hideLoader(ResetPINActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                    }

                });
    }

    private void callVerifyOTPAPI() {
        LoaderUtil.showLoader(ResetPINActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        SharedPreferences securePreferences = new SecurePreferences(this);
        String otp = "";
        if (registerType.equalsIgnoreCase("phone")) {
            otp = binding.otpView.getText().toString();
        } else {
            otp = binding.edtEmailCode.getText().toString();
        }
        String url=securePreferences.getString(getString(R.string.reset_base_url),"");
//        if (BuildConfig.FLAVOR.equals("dev")){
//            url= new AllUrls().getDevMainURL();
//        }else{
//            url= new AllUrls().getProdMainURL();
//        }
        logger.log("otpverification:verify " + url+"   "+securePreferences.getString(getString(R.string.reset_otp_token), "") + "   " + otp + "    " + userName);
        APIClient.getClient(url, "").create(APIInterface.class)
                .callVerifyOTP(userName, securePreferences.getString(getString(R.string.reset_otp_token), ""), otp).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(ResetPINActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        logger.log("response: " + response.isSuccessful());
                        if (response.isSuccessful()) {
                            try {
                                String jsonResponse = response.body().string();
                                logger.log("response: " + jsonResponse);
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {
                                    SuccessFailureDialog.showFailureDialog(ResetPINActivity.this, textSizeConverter, getString(R.string.otp_success), "", true, new DialogSuccessListener() {
                                        @Override
                                        public void OnSuccessClick() {
                                            openAccountDetailActivity();
                                        }

                                        @Override
                                        public void OnFailClick() {

                                        }
                                    });
                                } else {
                                    SuccessFailureDialog.showFailureDialog(ResetPINActivity.this, textSizeConverter, getString(R.string.otp_incorrect), "", false, null);
                                }
                                // Now you have the raw JSON response as a string (jsonResponse)
                                // You can parse it or work with it as needed.
                            } catch (IOException e) {
                                logger.log("response:exception " + e);
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                logger.log("response:jsonexception " + e);
                            }
                        } else {
                            SuccessFailureDialog.showFailureDialog(ResetPINActivity.this, textSizeConverter, getString(R.string.otp_incorrect), "", false, null);
                            logger.log("response:fail ");
                            // Handle the case where the HTTP response is not successful (e.g., 404, 500)
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        LoaderUtil.hideLoader(ResetPINActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        // Handle the case where the API call failed (e.g., network issues)
                    }

                });
    }

    private void openAccountDetailActivity() {
        Intent intent = new Intent(ResetPINActivity.this, ResetPinCodeActivity.class);
        intent.putExtra("loginType", registerType);
        if (registerType.equalsIgnoreCase("phone")) {
            intent.putExtra("otp", binding.otpView.getText().toString());
        } else {
            intent.putExtra("otp", binding.edtEmailCode.getText().toString());
        }
        intent.putExtra("username", userName);
        startActivity(intent);
        /*Intent intent = new Intent(ResetPINActivity.this, RegisterAccountDetailActivity.class);
        intent.putExtra("loginType", registerType);
        intent.putExtra("username", userName);
        if (registerType.equalsIgnoreCase("phone")) {
            intent.putExtra("otp", binding.otpView.getText().toString());
        } else {
            intent.putExtra("otp", binding.edtEmailCode.getText().toString());
        }
        startActivity(intent);*/

    }

//    private void showFailureDialog(boolean isSucess,String message) {
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
//
//        if (isSucess) {
//            binding.imgScanComplete.setVisibility(View.VISIBLE);
//            binding.imgScanFailure.setVisibility(View.GONE);
//            binding.btnRetry.setVisibility(View.GONE);
//        } else {
//            binding.imgScanComplete.setVisibility(View.GONE);
//            binding.imgScanFailure.setVisibility(View.VISIBLE);
//            binding.btnRetry.setVisibility(View.VISIBLE);
//        }
//        layoutParams2 = binding.imgScanComplete.getLayoutParams();
//        layoutParams2.width = textSizeConverter.getWidth(56);
//        layoutParams2.height = textSizeConverter.getHeight(56);
//        binding.imgScanComplete.setLayoutParams(layoutParams2);
//
//        layoutParams = (LinearLayout.LayoutParams) binding.imgScanComplete.getLayoutParams();
//        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(8), 0, 0);
//        binding.imgScanComplete.setLayoutParams(layoutParams);
////            binding.progressBar.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),R.color.orange_back)));
////        binding.layoutHeader.txtHelp.setText(getString(R.string.nfc_chip_rading));
//
//        binding.txtScanCompleted.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(18));
//
//        binding.txtScanCompleted.setText(message);
//
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
//        binding.imgScanComplete.addAnimatorListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                dialog.dismiss();
//                openAccountDetailActivity();
//            }
//        });
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
}
