package com.kyc.nashid.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.kyc.nashid.Logger;
import com.kyc.nashid.databinding.ActivityEnterCompanyCodeBinding;
import com.kyc.nashid.login.customview.PinField;
import com.kyc.nashid.login.networking.APIClient;
import com.kyc.nashid.login.networking.APIInterface;
import com.kyc.nashid.login.networking.AllUrls;
import com.kyc.nashid.login.pojoclass.companydomain.CompanyDomain;
import com.kyc.nashid.login.utils.LoaderUtil;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashid.R;
import com.kyc.nashid.login.utils.SuccessFailureDialog;
import com.kyc.nashid.rooted.RootedCheck;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterCompanyCodeActivity extends AppCompatActivity {

    private ActivityEnterCompanyCodeBinding binding;
    private TextSizeConverter textSizeConverter;
    private String registerType = "";
    private final Logger logger = Logger.withTag(this.getClass().getSimpleName());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEnterCompanyCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RootedCheck.getInstance().setFlag(EnterCompanyCodeActivity.this);
        if (RootedCheck.getInstance().isRootedDevice(getApplicationContext())) {
            RootedCheck.getInstance().showRootedDeviceDialog(this, getString(R.string.root_dialog), getString(R.string.root_desc), getString(R.string.root_btn));
        } else {
            initView();
            initClick();

        }
    }

    private void initClick() {
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
        setLayoutAndTextSize();
        changeOTPviewSize();
    }

    private void changeOTPviewSize() {
        int size = textSizeConverter.getWidth(8);
//        binding.otpView.setDistance(size, textSizeConverter.calculateRadius(8));
        binding.otpView.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String enteredText) {
                Log.d("TAG", "onTextComplete: "+enteredText);
                callCompanyDomainAPI();
                return true; // Return false to keep the keyboard open else return true to close the keyboard
            }
        });
        binding.cardBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void setLayoutAndTextSize() {
        textSizeConverter = new TextSizeConverter(getApplicationContext());
        textSizeConverter.changeStatusBarColor(EnterCompanyCodeActivity.this);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
//        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
//        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.txtHelp.setText(getString(R.string.register_header_title));
        binding.layoutHeader.txtHelp.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        int padding = textSizeConverter.getPaddingORMarginValue(2);
        binding.layoutHeader.txtHelp.setPadding(0, padding, 0, 0);
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

         padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.lytMainEnterCompanyCode.setPadding(padding, padding, padding, padding);

        padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.txtBtnNext.setPadding(0, padding, 0, padding);
        binding.txtBtnNext.setText(getString(R.string.btn_next));
        binding.txtBtnNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.cardBtnNext.setRadius(textSizeConverter.calculateRadius(8));

        LinearLayout.LayoutParams marginLyoutParam = (LinearLayout.LayoutParams) binding.txtHeader.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(20), 0, textSizeConverter.getPaddingORMarginValue(30));
        binding.txtHeader.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.layoutHeader.txtHelp.getLayoutParams();
        marginLyoutParam.setMargins(textSizeConverter.getPaddingORMarginValue(20), 0, 0, 0);
        binding.layoutHeader.txtHelp.setLayoutParams(marginLyoutParam);
        binding.txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(28));

        binding.txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
    }

    private void callCompanyDomainAPI() {
        LoaderUtil.showLoader(EnterCompanyCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        logger.log("Otpview text: " + binding.otpView.getText().toString());
        SharedPreferences securePreferences = new SecurePreferences(this);
        String url = "";
        if (BuildConfig.FLAVOR.equals("dev")) {
            url = new AllUrls().getDevMainURL();
        } else {
            url = new AllUrls().getProdMainURL();
        }
        APIClient.getClient(url, "").create(APIInterface.class).callCompanyDomain(binding.otpView.getText().toString()).enqueue(new Callback<CompanyDomain>() {
            @Override
            public void onResponse(Call<CompanyDomain> call, Response<CompanyDomain> response) {
                logger.log("company domain:" + response.body());
                CompanyDomain companyDomain = response.body();
                LoaderUtil.hideLoader(EnterCompanyCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                if (companyDomain.getSuccess()) {
//                    try {

//                        JSONObject jsonObject = new JSONObject(MyAppApplication.loadJSONFromAsset(getApplicationContext()));
                    logger.log("Registartion:Company code: "+companyDomain.getData().getCompanyUuid());
                    securePreferences.edit().putString(getString(R.string.company_uuid), companyDomain.getData().getCompanyUuid()).apply();
                    registerType = companyDomain.getData().getRegistratiorType();
                    //                        JSONObject company = jsonObject.getJSONObject(companyDomain.getData().getCompanyUuid());
//                        securePreferences.edit().putString(getString(R.string.backend_base_url), company.getString("backend")).apply();
//                        securePreferences.edit().putString(getString(R.string.face_match_base_url), company.getString("facematch")).apply();
//                        securePreferences.edit().putString(getString(R.string.aml_base_url), company.getString("aml_search")).apply();
//                        securePreferences.edit().putString(getString(R.string.aml_detail), company.getString("aml_detail")).apply();
//
//                        jsonObject = new JSONObject(MyAppApplication.loadJSONFromAsset(getApplicationContext()));
//                        company = jsonObject.getJSONObject(companyDomain.getData().getCompanyUuid());
//                        securePreferences.edit().putString(getString(R.string.backend_base_url), company.getString("backend")).apply();
//                        securePreferences.edit().putString(getString(R.string.face_match_base_url), company.getString("facematch")).apply();
//                        securePreferences.edit().putString(getString(R.string.aml_base_url), company.getString("aml_search")).apply();
//                        securePreferences.edit().putString(getString(R.string.aml_detail), company.getString("aml_detail")).apply();

//                    } catch (JSONException e) {
//                    }

//                    Toast.makeText(getApplicationContext(), "Invalid Company codecshnkgfkkd", Toast.LENGTH_SHORT).show();
                    openRegistrationScreen();
                } else {
                    SuccessFailureDialog.showFailureDialog(EnterCompanyCodeActivity.this, textSizeConverter, getString(R.string.company_code_fail), "", false, null);
//                    Toast.makeText(getApplicationContext(), "Invalid Company code", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CompanyDomain> call, Throwable t) {
                LoaderUtil.hideLoader(EnterCompanyCodeActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                SuccessFailureDialog.showFailureDialog(EnterCompanyCodeActivity.this, textSizeConverter, getString(R.string.company_code_fail), "", false, null);
            }

        });
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
//        binding.txtScanCompleted.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(20));
//        binding.txtScanCompleted.setText();
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

    private void openRegistrationScreen() {
        Intent intent = new Intent(EnterCompanyCodeActivity.this, RegisterAccountActivity.class);
        intent.putExtra("registertype",registerType);
        startActivity(intent);
    }
}
