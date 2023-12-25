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
import com.kyc.nashid.Logger;
import com.kyc.nashid.R;
import com.kyc.nashid.login.pojoclass.resetpasscod.ResetPassword;
import com.kyc.nashid.login.utils.DialogSuccessListener;
import com.kyc.nashid.login.utils.LoaderUtil;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashid.login.utils.SuccessFailureDialog;
import com.kyc.nashid.rooted.RootedCheck;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPinCodeActivity extends AppCompatActivity {

    private ActivityEnterCompanyCodeBinding binding;
    private TextSizeConverter textSizeConverter;
    private final Logger logger = Logger.withTag(this.getClass().getSimpleName());
    private String pincode = "";
    private boolean isAlreadyEnteredPincode = false;
    private String loginType = "";
    private String otp = "";
    private String username = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEnterCompanyCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RootedCheck.getInstance().setFlag(ResetPinCodeActivity.this);
        if (RootedCheck.getInstance().isRootedDevice(getApplicationContext())) {
            RootedCheck.getInstance().showRootedDeviceDialog(this, getString(R.string.root_dialog), getString(R.string.root_desc), getString(R.string.root_btn));
        } else {
            initView();
        }

    }

    private void initView() {
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
                        SuccessFailureDialog.showFailureDialog(ResetPinCodeActivity.this, textSizeConverter, getString(R.string.pin_match_fail), "", false, new DialogSuccessListener() {
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
                        SuccessFailureDialog.showFailureDialog(ResetPinCodeActivity.this, textSizeConverter, getString(R.string.pin_match_fail), "", false, null);
//                        Toast.makeText(getApplicationContext(), "password incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void retryResetPin() {
        binding.layoutHeader.lytHeaderMain.setVisibility(View.VISIBLE);
        binding.otpView.setText("");
        isAlreadyEnteredPincode = false;
        pincode = "";
    }
    /*private void showFailureDialog(String desc) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogRegisterFailureBinding binding = DialogRegisterFailureBinding.inflate(dialog.getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        int padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.mainDialogLyt.setPadding(padding, padding, padding, padding);

        ViewGroup.LayoutParams layoutParams2 = binding.imgScanFailure.getLayoutParams();
        layoutParams2.width = textSizeConverter.getWidth(56);
        layoutParams2.height = textSizeConverter.getHeight(56);
        binding.imgScanFailure.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.imgScanFailure.getLayoutParams();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(8), 0, 0);
        binding.imgScanFailure.setLayoutParams(layoutParams);
//            binding.progressBar.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),R.color.orange_back)));
//        binding.layoutHeader.txtHelp.setText(getString(R.string.nfc_chip_rading));

        binding.txtScanCompleted.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(20));
        binding.txtScanCompleted.setText(desc);
        layoutParams = (LinearLayout.LayoutParams) binding.txtScanCompleted.getLayoutParams();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(24), 0, textSizeConverter.getPaddingORMarginValue(24));
        binding.txtScanCompleted.setLayoutParams(layoutParams);


        padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.txtBtnRetry.setPadding(0, padding, 0, padding);
        binding.txtBtnRetry.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.btnRetry.setRadius(textSizeConverter.calculateRadius(8));

        binding.btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }*/

    private void setLayoutAndTextSize() {
        textSizeConverter = new TextSizeConverter(getApplicationContext());
        textSizeConverter.changeStatusBarColor(ResetPinCodeActivity.this);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.txtHelp.setText(getString(R.string.company_header_title));
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
        binding.txtScanCompleted.setText(getString(R.string.reset_sucess));

    }

    private void callRegisterApi() {
        LoaderUtil.showLoader(ResetPinCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        SharedPreferences securePreferences = new SecurePreferences(this);
        logger.log("Otpview text: " + otp + "\n" + securePreferences.getString(getString(R.string.reset_otp_token), "") + "\n" + binding.otpView.getText().toString() + "   " + pincode);
        String url = securePreferences.getString(getString(R.string.reset_base_url), "");
        /*if (BuildConfig.FLAVOR.equals("dev")){
            url= new AllUrls().getDevMainURL();
        }else{
            url= new AllUrls().getProdMainURL();
        }*/
        APIClient.getClient(url, securePreferences.getString(getString(R.string.register_token), "")).create(APIInterface.class)
                .callResetPassword(username, pincode, binding.otpView.getText().toString(),
                        otp,
                        securePreferences.getString(getString(R.string.reset_otp_token), ""))
                .enqueue(new Callback<ResetPassword>() {
                    @Override
                    public void onResponse(Call<ResetPassword> call, Response<ResetPassword> response) {
                        LoaderUtil.hideLoader(ResetPinCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        try {
//                            String jsonResponse = response.body().string();
//                            logger.log("Otpview text: " + response.toString() + " --  " + response.errorBody().string());
                            if (response.isSuccessful()) {
                                binding.lytRegisterSuccessfully.setVisibility(View.VISIBLE);
                            } else {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                                SuccessFailureDialog.showFailureDialog(ResetPinCodeActivity.this, textSizeConverter, jsonObject1.getString("data"), "", false, new DialogSuccessListener() {
                                    @Override
                                    public void OnSuccessClick() {
                                        retryResetPin();
                                    }

                                    @Override
                                    public void OnFailClick() {
                                        retryResetPin();
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.log("Otpview exce: " + e);
                        }

                    }

                    @Override
                    public void onFailure(Call<ResetPassword> call, Throwable t) {
                        LoaderUtil.hideLoader(ResetPinCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
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
        Intent intent = new Intent(ResetPinCodeActivity.this, LoginAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
