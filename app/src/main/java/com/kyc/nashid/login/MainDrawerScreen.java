package com.kyc.nashid.login;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.kyc.nashid.BuildConfig;
import com.kyc.nashid.databinding.ActivityMainDrawerScreenBinding;
import com.kyc.nashid.databinding.CustomPopupLayoutBinding;
import com.kyc.nashid.databinding.DialogEmailPhoneConfirmationBinding;
import com.kyc.nashid.login.adapter.CustomPagerAdapter;
import com.kyc.nashid.login.adapter.OnItemClickListener;
import com.kyc.nashid.login.adapter.SimpleAdapter;
import com.kyc.nashid.login.networking.APIClient;
import com.kyc.nashid.login.networking.APIInterface;
import com.kyc.nashid.login.networking.AllUrls;
import com.kyc.nashid.login.pojoclass.KeyValueItem;
import com.kyc.nashid.login.utils.LoaderUtil;
import com.kyc.nashid.login.utils.RandomStringGenerator;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashid.login.utils.SuccessFailureDialog;
import com.kyc.nashidmrz.Logger;
import com.kyc.nashid.R;
import com.kyc.nashidmrz.NashidSDK;
import com.kyc.nashidmrz.mrtd2.activity.InstructionScreenActivity;
import com.kyc.nashidmrz.mrtd2.activity.StartScanningActivity;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;
import com.kyc.nashidmrz.mrtd2.resultcallback.ResultListener;
import com.kyc.nashidmrz.utility.ErrorUtility;
import com.kyc.nashidmrz.utility.OnInternetAvailabilityListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainDrawerScreen extends AppCompatActivity implements OnInternetAvailabilityListener {
    private ActivityMainDrawerScreenBinding binding;
    private TextSizeConverter textSizeConverter;
    private SecurePreferences securePreferences;
    private String selectedDocType = "";
    private String[] arr = new String[]{Manifest.permission.CAMERA};
    private int REQUEST_CODE_ASK_PERMISSIONS = 100;
    private boolean isInternetAvailable;
    private final Logger logger = Logger.withTag(this.getClass().getSimpleName());
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        String name = intent.getStringExtra("name");
                        logger.log("name: " + name);
                        binding.txtNavSubtitle.setText(name);
                    }
                }
            });

    //second dealer
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainDrawerScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        logger.log("Dealer: " + securePreferences.getBoolean(getString(R.string.dealer), false));
        if (securePreferences.getBoolean(getString(R.string.dealer), false)) {
            getAllCompanylist();
            binding.layoutHome.lytArrow.setVisibility(View.VISIBLE);
            binding.layoutHome.txtCompanyname.setText("");
        } else {
            binding.layoutHome.lytArrow.setVisibility(View.GONE);
            binding.layoutHome.txtCompanyname.setText(securePreferences.getString(getString(R.string.company_name), ""));
            LoaderUtil.hideLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
        }
        initClickView();
//        callUserCompanies();

    }

    private void initView() {
        NashidSDK.setNeedToShowResultScreen(false);

        isInternetAvailable = ErrorUtility.getInstance().registerConnectivityanager(this, this);
        if (!isInternetAvailable) {
            ErrorUtility.getInstance().showNoInternetDialog(this, (!this.isFinishing() && !this.isDestroyed()), getString(R.string.no_internet_error));
        }
//        binding.drawerLayout.setScrimColor(Color.TRANSPARENT);
        setLayoutAndTextSize();
        ArrayList<String> strings1 = new ArrayList<>();
        strings1.add("Scan");
        Log.d("TAG", "initView:feature " + securePreferences.getString(getString(R.string.company_uuid), "").equalsIgnoreCase("9KMAQPJFK2MJSKR"));
        if (securePreferences.getString(getString(R.string.company_uuid), "").equalsIgnoreCase("9KMAQPJFK2MJSKR")) {
            strings1.add("NFC");
            strings1.add("Liveness");
        } else {
            strings1.add("NFC");
        }
        NashidSDK.setFeatureList(strings1);
        Log.d("TAG", "callFeatureCheck:feature " + securePreferences.getString(getString(R.string.company_uuid), "").equalsIgnoreCase("9KMAQPJFK2MJSKR") + "\n" + NashidSDK.getFeatureList());
    }

    private void setLayoutAndTextSize() {
        securePreferences = new SecurePreferences(this);

        textSizeConverter = new TextSizeConverter(getApplicationContext());
        textSizeConverter.changeStatusBarColor(MainDrawerScreen.this);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_menu_icon_size)));
        layoutParams2.width = textSizeConverter.getWidth(Integer.parseInt(getString(R.string.header_menu_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.txtHelp.setText(getString(R.string.main_header));
        binding.layoutHeader.txtHelp.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(17));

        int padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.content.setPadding(padding, padding, padding, padding);

        layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(textSizeConverter.getPaddingORMarginValue(16), 0, 0, 0);
        binding.layoutHeader.imgHeaderLogo.setLayoutParams(layoutParams);

        binding.layoutHeader.txtHelp.setPadding(textSizeConverter.getPaddingORMarginValue(4), 0, 0, 0);

        layoutParams2 = binding.layoutHeader.imgHeaderLogo.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_menu_icon_size)));
        layoutParams2.width = textSizeConverter.getWidth(Integer.parseInt(getString(R.string.header_menu_icon_size)));
        binding.layoutHeader.imgHeaderLogo.setLayoutParams(layoutParams2);

        layoutParams2 = binding.lytNavHeader.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(106);
        binding.lytNavHeader.setLayoutParams(layoutParams2);

        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width (MATCH_PARENT or a specific width in pixels or dp)
                LinearLayout.LayoutParams.WRAP_CONTENT  // Height (MATCH_PARENT or a specific height in pixels or dp)
        );
        layoutParams.setMargins(textSizeConverter.getPaddingORMarginValue(24), 0, 0, 0);
        binding.imgNavLogo.setLayoutParams(layoutParams);

        layoutParams2 = binding.imgNavLogo.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(40);
        layoutParams2.width = textSizeConverter.getWidth(36);
        binding.imgNavLogo.setLayoutParams(layoutParams2);

        binding.txtNavTitle.setPadding(textSizeConverter.getPaddingORMarginValue(14), 0, 0, 0);
        binding.txtNavSubtitle.setPadding(textSizeConverter.getPaddingORMarginValue(14), 0, 0, 0);
        binding.txtNavTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(14));
        binding.txtNavSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        binding.txtNavSubtitle.setText(securePreferences.getString(getString(R.string.register_name), ""));

        binding.navigationNested.setPadding(textSizeConverter.getPaddingORMarginValue(16), 0, textSizeConverter.getPaddingORMarginValue(16), textSizeConverter.getPaddingORMarginValue(16));
        binding.lytHome.setPadding(0, textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_option_margin))), 0, 0);
        binding.lytProfile.setPadding(0, textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_option_margin))), 0, 0);
        binding.lytHistory.setPadding(0, textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_option_margin))), 0, 0);
        binding.lytContatctus.setPadding(0, textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_option_margin))), 0, 0);
        binding.lytTurbomode.setPadding(0, textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_option_margin))), 0, 0);
        binding.lytInstruction.setPadding(0, textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_option_margin))), 0, 0);
        binding.lytQr.setPadding(0, textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_option_margin))), 0, 0);

        layoutParams2 = binding.imgHome.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.nav_icon_size)));
        layoutParams2.width = textSizeConverter.getWidth(Integer.parseInt(getString(R.string.nav_icon_size)));
        binding.imgHome.setLayoutParams(layoutParams2);
        layoutParams2 = binding.imgProfile.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.nav_icon_size)));
        layoutParams2.width = textSizeConverter.getWidth(Integer.parseInt(getString(R.string.nav_icon_size)));
        binding.imgProfile.setLayoutParams(layoutParams2);
        layoutParams2 = binding.imgHistory.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.nav_icon_size)));
        layoutParams2.width = textSizeConverter.getWidth(Integer.parseInt(getString(R.string.nav_icon_size)));
        binding.imgHistory.setLayoutParams(layoutParams2);
        layoutParams2 = binding.imgContactus.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.nav_icon_size)));
        layoutParams2.width = textSizeConverter.getWidth(Integer.parseInt(getString(R.string.nav_icon_size)));
        binding.imgContactus.setLayoutParams(layoutParams2);
        layoutParams2 = binding.imgTurbomode.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.nav_icon_size)));
        layoutParams2.width = textSizeConverter.getWidth(Integer.parseInt(getString(R.string.nav_icon_size)));
        binding.imgTurbomode.setLayoutParams(layoutParams2);
        layoutParams2 = binding.imgInstruction.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.nav_icon_size)));
        layoutParams2.width = textSizeConverter.getWidth(Integer.parseInt(getString(R.string.nav_icon_size)));
        binding.imgInstruction.setLayoutParams(layoutParams2);
        layoutParams2 = binding.imgInstruction.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.nav_icon_size)));
        layoutParams2.width = textSizeConverter.getWidth(Integer.parseInt(getString(R.string.nav_icon_size)));
        binding.imgQr.setLayoutParams(layoutParams2);

        layoutParams2 = binding.imgSwitchTurbomode.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(25);
        layoutParams2.width = textSizeConverter.getWidth(40);
        binding.imgSwitchTurbomode.setLayoutParams(layoutParams2);

        layoutParams2 = binding.imgSwitchInstruction.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(25);
        layoutParams2.width = textSizeConverter.getWidth(40);
        binding.imgSwitchInstruction.setLayoutParams(layoutParams2);

        layoutParams2 = binding.imgSwitchInstruction.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(25);
        layoutParams2.width = textSizeConverter.getWidth(40);
        binding.imgSwitchQr.setLayoutParams(layoutParams2);

        binding.txtHome.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(Integer.parseInt(getString(R.string.nav_text_size))));
        binding.txtProfile.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(Integer.parseInt(getString(R.string.nav_text_size))));
        binding.txtHistory.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(Integer.parseInt(getString(R.string.nav_text_size))));
        binding.txtContactus.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(Integer.parseInt(getString(R.string.nav_text_size))));
        binding.txtTurbomode.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(Integer.parseInt(getString(R.string.nav_text_size))));
        binding.txtInstruction.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(Integer.parseInt(getString(R.string.nav_text_size))));
        binding.txtQr.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(Integer.parseInt(getString(R.string.nav_text_size))));

        binding.txtHome.setPadding(textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_text_padding))), 0, 0, 0);
        binding.txtProfile.setPadding(textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_text_padding))), 0, 0, 0);
        binding.txtHistory.setPadding(textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_text_padding))), 0, 0, 0);
        binding.txtContactus.setPadding(textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_text_padding))), 0, 0, 0);
        binding.txtTurbomode.setPadding(textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_text_padding))), 0, 0, 0);
        binding.txtInstruction.setPadding(textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_text_padding))), 0, 0, 0);
        binding.txtQr.setPadding(textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_text_padding))), 0, 0, 0);

        binding.lytLogout.setPadding(0, 0, 0, textSizeConverter.getPaddingORMarginValue(30));
        layoutParams2 = binding.imgLogout.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(18);
        layoutParams2.width = textSizeConverter.getWidth(18);
        binding.imgLogout.setLayoutParams(layoutParams2);

        binding.txtLogout.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(Integer.parseInt(getString(R.string.nav_text_size))));

        binding.txtLogout.setPadding(textSizeConverter.getPaddingORMarginValue(Integer.parseInt(getString(R.string.nav_text_padding))), 0, 0, 0);

        initHomeLayout();

    }

    private void initHomeLayout() {
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Width (MATCH_PARENT or a specific width in pixels or dp)
                LinearLayout.LayoutParams.WRAP_CONTENT  // Height (MATCH_PARENT or a specific height in pixels or dp)
        );
        layoutParams1.setMargins(0, textSizeConverter.getPaddingORMarginValue(64), 0, 0);
        binding.layoutHome.mainLayout.setLayoutParams(layoutParams1);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHome.imgHomeCompany.getLayoutParams();
        layoutParams2.width = textSizeConverter.getWidth(64);
        layoutParams2.height = textSizeConverter.getHeight(50);
        binding.layoutHome.imgHomeCompany.setLayoutParams(layoutParams2);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width (MATCH_PARENT or a specific width in pixels or dp)
                LinearLayout.LayoutParams.WRAP_CONTENT  // Height (MATCH_PARENT or a specific height in pixels or dp)
        );
        layoutParams.setMargins(textSizeConverter.getPaddingORMarginValue(15), 0, 0, 0);
        binding.layoutHome.imgArrow.setLayoutParams(layoutParams);

        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width (MATCH_PARENT or a specific width in pixels or dp)
                LinearLayout.LayoutParams.WRAP_CONTENT  // Height (MATCH_PARENT or a specific height in pixels or dp)
        );
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(40), 0, 0);
        binding.layoutHome.viewPager.setLayoutParams(layoutParams);

        layoutParams2 = binding.layoutHome.imgArrow.getLayoutParams();
        layoutParams2.width = textSizeConverter.getWidth(12);
        layoutParams2.height = textSizeConverter.getHeight(12);
        binding.layoutHome.imgArrow.setLayoutParams(layoutParams2);

        binding.layoutHome.txtCompanyname.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(30));

        binding.layoutHome.txtCompanyname.setPadding(textSizeConverter.getPaddingORMarginValue(4), textSizeConverter.getPaddingORMarginValue(14), 0, 0);
        int padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.layoutHome.txtBtnStart.setPadding(0, padding, 0, padding);
        binding.layoutHome.txtBtnStart.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.layoutHome.cardBtnStart.setRadius(textSizeConverter.calculateRadius(8));

        binding.layoutHome.txtHomeDesc.setPadding(0, textSizeConverter.getPaddingORMarginValue(28), 0, textSizeConverter.getPaddingORMarginValue(40));
        binding.layoutHome.txtHomeDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        if (securePreferences.getString(getString(R.string.company_verification_documents), "").equalsIgnoreCase("id")) {
            strings.add("Omani ID Card");
        } else {
            strings.add("PassPort");
        }
        binding.layoutHome.viewPager.setAdapter(new CustomPagerAdapter(this, strings));
        binding.layoutHome.tabLayout.setupWithViewPager(binding.layoutHome.viewPager);
        selectedDocType = "ID Card";
        boolean instruction = NashidSDK.isShowInstruction(getApplicationContext());
        if (instruction) {
//        if (SharePreferenceUtility.getSavePreferenceData(getApplicationContext(), getString(R.string.verification_step))) {
            binding.imgSwitchInstruction.setImageResource(R.drawable.toggle_on);
        } else {
            binding.imgSwitchInstruction.setImageResource(R.drawable.toggle_off);
        }
        boolean qrCode = securePreferences.getBoolean(getString(R.string.qr_code), true);
        if (qrCode) {
            binding.imgSwitchQr.setImageResource(R.drawable.toggle_on);
        } else {
            binding.imgSwitchQr.setImageResource(R.drawable.toggle_off);
        }

        binding.txtNavSubtitle.setText(securePreferences.getString(getString(R.string.register_name), ""));
    }

    private ArrayList<String> strings = new ArrayList<>();

    private void initClickView() {
        NashidSDK.setResultListener(new ResultListener() {
            @Override
            public void onResultData(JSONObject jsonObject, String s) {
                logger.log("Result success: " + jsonObject);
                String scanType;
                if (s.equalsIgnoreCase("E-passport")) {
                    scanType = "Passport";
                } else {
                    scanType = "Card";
                }
                if (securePreferences.getBoolean(getString(R.string.company_data_sharing), false)) {
                    logger.log("Result success:allow " + jsonObject);
                    callCompanyScan(jsonObject, scanType, 1);
                }
            }

            @Override
            public void onFailure() {
                logger.log("Result failure: ");
            }
        });
       /* NashidSDK.setScanDocumentResultListener(new ScanResultListener() {
            @Override
            public void onCompleteSuccess(ScanDocument scanDocument, String documentType) {
                logger.log("completion scan detail: " + scanDocument);
                String scanType;
                JSONObject requestData = new JSONObject();
                if (documentType.equalsIgnoreCase("E-passport")) {
                    scanType = "Passport";
                } else {
                    scanType = "Card";
                }
                try {
                    requestData.put("Document Number", scanDocument.getDocuemntNo().replace("<", ""));
                    requestData.put("Date of Birth", formateDateFromstring("yyMMdd", Constant.dateFormate, scanDocument.getDateOfBirth()));
                    requestData.put("Expiry Date", formateDateFromstring("yyMMdd", Constant.dateFormate, scanDocument.getExpiryDate()));
                    // Add more key-value pairs as needed
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callCompanyScan(requestData, scanType, 1);

            }

            @Override
            public void onFailure() {

            }
        });
        NashidSDK.setNFCResultListener(new NFCResultListener() {
            JSONObject requestData = new JSONObject();

            @Override
            public void onIdCardCompleteSuccess(IDCardNFCData nfcData) {
                try {
                    requestData.put("Identity Number", nfcData.getIdentityNumber());
                    requestData.put("Issue Date", nfcData.getIssueDate());
//                requestData.put("Expiry Date", format6digitDate(dg1Parser.getExpiryDate(), 2000));
                    requestData.put("Expiry Date", nfcData.getExpiryDate());
                    requestData.put("Place Of Issue (Arabic)", nfcData.getPlaceOfIssueArabic());
                    requestData.put("Place Of Issue (English)", nfcData.getPlaceOfIssueEnglish());
                    requestData.put("Full Name (Arabic)", nfcData.getFullNameArabic());
                    requestData.put("Full Name (English)", nfcData.getFullNameEnglish());
                    requestData.put("Date Of Birth", nfcData.getDateOfBirth());
                    requestData.put("Place Of Birth (Arabic)", nfcData.getPlaceOfBirthArabic());
                    requestData.put("Place Of Birth (English)", nfcData.getPlaceOfBirthEnglish());
                    requestData.put("Nationality (Arabic)", nfcData.getNationalityArabic());
                    requestData.put("Gender (Arabic)", nfcData.getGenderArabic());
                    requestData.put("Nationality (English)", nfcData.getNationalityEnglish());
                    requestData.put("Gender (English)", nfcData.getGenderEnglish());
                    requestData.put("Visa Number", nfcData.getVisaNumber());
                    requestData.put("Permit type", nfcData.getPermitType());
                    requestData.put("Use By Date", nfcData.getUseByDate());
                    requestData.put("Place of Issue (Arabic)", nfcData.getVisaPlaceOfIssueArabic());

                    requestData.put("Permit Number", nfcData.getPermitNumber());

                    requestData.put("Issue Date", nfcData.getSponserIssueDate());
                    requestData.put("Expiry Date", nfcData.getSponserExpirtDate());
                    requestData.put("Company Name (Arabic)", nfcData.getCompanyNameArabic());
                    requestData.put("Company Name (English)", nfcData.getCompanyNameEnglish());
                    requestData.put("Company Address (Arabic)", nfcData.getCompanyAddressAraic());

                } catch (Exception e) {

                }
                callCompanyScan(requestData, "NFC", 1);
            }

            @Override
            public void onPassportCompleteSuccess(JSONObject jsonObject) {
                callCompanyScan(jsonObject, "NFC", 1);
            }

//            @Override
//            public void onPassportCompleteSuccess(PassportNFCData nfcData) {
//                try {
//                    requestData.put("name", nfcData.getGivenName());
//                    requestData.put("Document type", nfcData.getDocumentType());
//                    requestData.put("Issued By", nfcData.getIssueBy());
//                    requestData.put("Document Number", nfcData.getDocumentNumber());
//
//                    requestData.put("Expiry Date", nfcData.getExpirtDate());
//
//                    requestData.put("Gender", nfcData.getGender());
//                    requestData.put("Nationality", nfcData.getNationality());
//                    requestData.put("Surname", nfcData.getSurname());
//                    requestData.put("Given Name", nfcData.getGivenName());
//                    requestData.put("Date of Birth", nfcData.getDateOfBirth());
//
//                    requestData.put("Issuer Country", nfcData.getIssuerCountry());
//                    requestData.put("Cerification Authority", nfcData.getCertificationAutority());
//                    requestData.put("Issuer Organization", nfcData.getIssuerOrganization());
//                    requestData.put("Organizational Unit", nfcData.getOrganizationalUnit());
//                    requestData.put("Signature Algorithm", nfcData.getSignatureAlgorith());
//                    requestData.put("LED Hash Algorithm", nfcData.getLEDHashAlgorithm());
//                    requestData.put("Certificate Valid From", nfcData.getCertificateValideFrom());
//                    requestData.put("Certificate Valid Until", nfcData.getCertificateValideUntil());
//                } catch (JSONException e) {
//
//                }
//                callCompanyScan(requestData, "NFC", 1);
//            }


            @Override
            public void onFailure() {
                callCompanyScan(requestData, "NFC", 0);
            }
        });
        NashidSDK.setLivenessResultListener(new LivenessResultListener() {
            JSONObject requestData = new JSONObject();

            @Override
            public void onCompleteSuccess(byte[] bytes) {

                callCompanyScan(requestData, "Liveness", 1);
            }

            @Override
            public void onFailure() {
                callCompanyScan(requestData, "Liveness", 0);
            }
        });
        NashidSDK.setFaceMatchResultListener(new FaceMatchResultListener() {
            JSONObject requestData = new JSONObject();

            @Override
            public void onCompleteSuccess(String faceMatchedValue) {
                try {
                    requestData.put("Match value", faceMatchedValue);
                } catch (JSONException e) {

                }
                callCompanyScan(requestData, "FaceMatch", 1);
            }

            @Override
            public void onFailure() {
                callCompanyScan(requestData, "FaceMatch", 0);
            }
        });*/
        binding.layoutHome.lytArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                logger.log("Clickec" + stringStringHashMap.size());
                LayoutInflater inflater = (LayoutInflater)
                        getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                CustomPopupLayoutBinding customPopupLayoutBinding = CustomPopupLayoutBinding.inflate(inflater);

                View view = customPopupLayoutBinding.getRoot();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) customPopupLayoutBinding.cardPopup.getLayoutParams();
                int padding = textSizeConverter.getPaddingORMarginValue(20);
                layoutParams.setMargins(padding, padding, padding, padding);
                customPopupLayoutBinding.lytPopup.setLayoutParams(layoutParams);
                customPopupLayoutBinding.cardPopup.setRadius(textSizeConverter.calculateRadius(8));

                PopupWindow mypopupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
                SimpleAdapter simpleAdapter = new SimpleAdapter(MainDrawerScreen.this, stringStringHashMap);
                customPopupLayoutBinding.recyCompany.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                customPopupLayoutBinding.recyCompany.setAdapter(simpleAdapter);
                simpleAdapter.setClickListner(new OnItemClickListener() {
                    @Override
                    public void onChildItemClick(KeyValueItem childName) {
                        binding.layoutHome.txtCompanyname.setText(childName.getValue());
                        mypopupWindow.dismiss();
                        callSwitchCompany(childName.getKey());
                    }
                });
                mypopupWindow.showAsDropDown(view1);
            }
        });
        binding.lytContatctus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainDrawerScreen.this, ContactUsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                binding.getRoot().close();
            }
        });
        binding.lytProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainDrawerScreen.this, ProfileActivity.class);
//                startActivity(intent);
                someActivityResultLauncher.launch(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                binding.getRoot().close();
            }
        });
        binding.lytHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainDrawerScreen.this, RecentScanActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                binding.getRoot().close();
            }
        });
        binding.imgSwitchInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean instruction = NashidSDK.isShowInstruction(getApplicationContext());
                if (instruction) {
                    NashidSDK.setShowInstruction(getApplicationContext(), false);
                    binding.imgSwitchInstruction.setImageResource(R.drawable.toggle_off);
                } else {
                    NashidSDK.setShowInstruction(getApplicationContext(), true);
                    binding.imgSwitchInstruction.setImageResource(R.drawable.toggle_on);
                }
               /* if (SharePreferenceUtility.getSavePreferenceData(getApplicationContext(), getString(R.string.verification_step))) {
                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.verification_step), false);
                    binding.imgSwitchInstruction.setImageResource(R.drawable.toggle_off);
                } else {
                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.verification_step), true);
                    binding.imgSwitchInstruction.setImageResource(R.drawable.toggle_on);
                }*/
            }
        });
        binding.imgSwitchQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean qrCode = securePreferences.getBoolean(getString(R.string.qr_code), true);
                if (qrCode) {
                    securePreferences.edit().putBoolean(getString(R.string.qr_code), false).apply();
                    binding.imgSwitchQr.setImageResource(R.drawable.toggle_off);
                } else {
                    securePreferences.edit().putBoolean(getString(R.string.qr_code), true).apply();
                    binding.imgSwitchQr.setImageResource(R.drawable.toggle_on);
                }
               /* if (SharePreferenceUtility.getSavePreferenceData(getApplicationContext(), getString(R.string.verification_step))) {
                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.verification_step), false);
                    binding.imgSwitchInstruction.setImageResource(R.drawable.toggle_off);
                } else {
                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.verification_step), true);
                    binding.imgSwitchInstruction.setImageResource(R.drawable.toggle_on);
                }*/
            }
        });
        binding.imgSwitchTurbomode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (SharePreferenceUtility.getSavePreferenceData(getApplicationContext(), getString(R.string.turbo_mode))) {
//                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.turbo_mode), false);
//                    binding.imgSwitchTurbomode.setImageResource(R.drawable.toggle_off);
//                } else {
//                    SharePreferenceUtility.savePreferenceValue(getApplicationContext(), getString(R.string.turbo_mode), true);
//                    binding.imgSwitchTurbomode.setImageResource(R.drawable.toggle_on);
//                }
            }
        });
        binding.lytLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogoutDialog();
                binding.getRoot().close();
            }
        });
        binding.layoutHome.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                logger.log("Selected doc type of viewpager: " + strings.get(position));
                if (strings.get(position).equalsIgnoreCase("passport")) {
                    selectedDocType = "E-Passport";
                } else {
                    selectedDocType = "ID Card";
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        binding.layoutHome.cardBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logger.log("Selected doc type: " + selectedDocType);
                if (selectedDocType.equals("E-Passport") || selectedDocType.equals("ID Card")) {
                    if (ContextCompat.checkSelfPermission(MainDrawerScreen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MainDrawerScreen.this,
                                arr, REQUEST_CODE_ASK_PERMISSIONS);

                    } else {
//                    finish();
                        openScanDocumentScreen();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please select document either idcard or passport", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        binding.content.setX(binding.navView.getWidth() * slideOffset);
                        RelativeLayout.LayoutParams lp =
                                (RelativeLayout.LayoutParams) binding.content.getLayoutParams();
                        binding.content.setLayoutParams(lp);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                }
        );

        binding.layoutHeader.imgBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (binding.drawerLayout.isDrawerOpen(binding.navView)) {
                            binding.drawerLayout.closeDrawer(binding.navView);
                        } else {
                            binding.drawerLayout.openDrawer(binding.navView);
                        }
                    }
                }
        );
    }

    private void callSwitchCompany(String companyUUID) {
        LoaderUtil.showLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
        logger.log("switchcompany:" + securePreferences.getString(getString(R.string.nashid_token), "") + " " + companyUUID);
        APIClient.getClient(securePreferences.getString(getString(R.string.switch_company), ""), securePreferences.getString(getString(R.string.nashid_token), "")).create(APIInterface.class)
                .callSwitchCompany(companyUUID).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        logger.log("calling: switch api " + response.body());
                        if (response.isSuccessful()) {
                            try {
                                String jsonResponse = response.body().string();
                                logger.log("Otpview text: " + jsonResponse + "  " + response.body());

                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.getBoolean("success")) {
                                    JSONObject registerData = jsonObject.getJSONObject("data");
                                    String companuUUID = registerData.getString("company_uuid");
                                    ArrayList<String> strings1 = new ArrayList<>();
                                    strings1.add("Scan");
                                    if (companuUUID.equalsIgnoreCase("9KMAQPJFK2MJSKR")) {
                                        strings1.add("NFC");
                                        strings1.add("Liveness");
                                    } else {
                                        strings1.add("NFC");
                                    }
                                    NashidSDK.setFeatureList(strings1);
                                    securePreferences.edit().putString(getString(R.string.company_uuid), companuUUID).apply();
                                    securePreferences.edit().putString(getString(R.string.register_token), registerData.getString("token")).apply();
                                    securePreferences.edit().putString(getString(R.string.register_name), registerData.getString("name")).apply();
                                    securePreferences.edit().putString(getString(R.string.company_name), registerData.getString("company_name")).apply();
                                } else {
                                    SuccessFailureDialog.showFailureDialog(MainDrawerScreen.this, textSizeConverter, getString(R.string.switch_company_fail), "", false, null);
                                }
//                                securePreferences.edit().putString(getString(R.string.register_name), login.get).apply();
                            } catch (Exception e) {

                            }
                            LoaderUtil.hideLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        LoaderUtil.hideLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
                    }
                });

    }

    private void openLogoutDialog() {
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
        layoutParams.setMargins(textSizeConverter.getPaddingORMarginValue(24), padding, textSizeConverter.getPaddingORMarginValue(16), padding);
        binding.btnYes.setLayoutParams(layoutParams);
        String phoneEmail = "";

        binding.dialogHeader.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.pingmedium));
        binding.dialogSubtitle.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.regular));

        binding.dialogHeader.setText(getString(R.string.logout_title));
        binding.dialogSubtitle.setText(getString(R.string.logout_subtitle));


        String finalPhoneEmail = phoneEmail;
        binding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                logger.log("emailphonestring : " + finalPhoneEmail);
                SharedPreferences securePreferences = new SecurePreferences(MainDrawerScreen.this);
//                        securePreferences.edit().putString(getString(R.string.backend_base_url), "").apply();
//                        securePreferences.edit().putString(getString(R.string.face_match_base_url), "").apply();
//                        securePreferences.edit().putString(getString(R.string.aml_base_url), "").apply();
//                        securePreferences.edit().putString(getString(R.string.image_crop_base_url), "").apply();
//                        securePreferences.edit().putString(getString(R.string.company_uuid), "").apply();
                securePreferences.edit().putString(getString(R.string.otp_token), "").apply();
                securePreferences.edit().putString(getString(R.string.register_token), "").apply();
                securePreferences.edit().putString(getString(R.string.register_name), "").apply();
                Intent intent = new Intent(MainDrawerScreen.this, LoginAccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openScanDocumentScreen();
            } else {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainDrawerScreen.this,
                            arr, REQUEST_CODE_ASK_PERMISSIONS);
                }
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openScanDocumentScreen() {

        if (!isInternetAvailable) {
            ErrorUtility.getInstance().showNoInternetDialog(this, (!this.isFinishing() && !this.isDestroyed()), getString(R.string.no_internet_error));
        } else {
            String url;
            if (BuildConfig.FLAVOR.equals("dev")) {
                url = new AllUrls().getDevMainURL();
            } else {
                url = new AllUrls().getProdMainURL();
            }
            NashidSDK nashidSDK = new NashidSDK();
            nashidSDK.setFeatureCheckUrl(url);
            String loginFeature = securePreferences.getString(getString(R.string.login_feature), "");
            String[] splitFeature = loginFeature.split(",");
            ArrayList<String> stringArrayList = new ArrayList<>();
            for (int i = 0; i < splitFeature.length; i++) {
                stringArrayList.add(splitFeature[i]);
            }
//            strings1.add("Liveness");
            NashidSDK.setLoginFeatureList(stringArrayList);
            NashidSDK.setAppName("Overify");
            NashidSDK.init(securePreferences.getString(getString(R.string.nashid_token), ""), securePreferences.getString(getString(R.string.company_uuid), ""));
            callQrStatus();
        }
    }

    @Override
    public void onAvailable() {
        if (!isInternetAvailable) {
            isInternetAvailable = true;
        }
    }

    @Override
    public void onLost() {
        isInternetAvailable = false;
        ErrorUtility.getInstance().showNoInternetDialog(this, (!this.isFinishing() && !this.isDestroyed()), getString(R.string.no_internet_error));
    }

    private ArrayList<KeyValueItem> stringStringHashMap = new ArrayList<>();

    private void getAllCompanylist() {
//        156|r05eJQUaHXZ5NmWxeBYWTtBdpEyTJV8Gi1nLzBgY29316e2e
//        securePreferences.getString(getString(R.string.nashid_token), "")
        logger.log("getallcompany: " + securePreferences.getString(getString(R.string.user_companies), "") + "   " + securePreferences.getString(getString(R.string.nashid_token), ""));
        LoaderUtil.showLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
        APIClient.getClient(securePreferences.getString(getString(R.string.user_companies), ""), securePreferences.getString(getString(R.string.nashid_token), "")).create(APIInterface.class)
                .getAllUserCompany().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            if (response.isSuccessful()) {
                                try {
                                    String responseBodyString = responseBody.string();
                                    logger.log("getallcompany: " + responseBodyString);

                                    // Now parse the string into a JSONObject
                                    JSONObject jsonObject = new JSONObject(responseBodyString);
//                                    JSONObject jsonObject = new JSONObject(s);
                                    JSONObject parentData = jsonObject.getJSONObject("data");
                                    JSONObject childData = parentData.getJSONObject("data");
                                    Iterator<String> keys = childData.keys();
                                    logger.log("getallcompany: " + childData);
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        String value = childData.getString(key);
                                        KeyValueItem keyValueItem = new KeyValueItem(key, value);
                                        stringStringHashMap.add(keyValueItem);
                                        logger.log("companyscan: " + key + "  " + value);
                                        // Now you can work with the key and value as needed
                                        if (securePreferences.getString(getString(R.string.company_uuid), "").equals(key)) {
                                            binding.layoutHome.txtCompanyname.setText(value);

                                        }
                                        System.out.println("Key: " + key + ", Value: " + value);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        LoaderUtil.hideLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            logger.log("Company_name: " + securePreferences.getString(getString(R.string.company_name), ""));
            binding.layoutHome.txtCompanyname.setText(securePreferences.getString(getString(R.string.company_name), ""));
        } catch (Exception e) {

        }
    }

    private void callQrStatus() {
        LoaderUtil.showLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
        String companyUUID = securePreferences.getString(getString(R.string.company_uuid), "");
        APIClient.getClient(securePreferences.getString(companyUUID + getString(R.string.backend_base_url), ""), securePreferences.getString(getString(R.string.register_token), "")).create(APIInterface.class)
                .callQrStatus(companyUUID).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            logger.log("qststus" + response.isSuccessful());
                            if (response.isSuccessful()) {
                                String responseBody = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseBody);

                                boolean success = jsonObject.getBoolean("success");
                                JSONObject data = jsonObject.getJSONObject("data");
                                boolean qrStatus = data.getBoolean("qr_status");
                                securePreferences.edit().putBoolean(getString(R.string.qr_code), qrStatus).apply();
                                Log.d("TAG", "onResponse: " + qrStatus);
                                boolean instruction = NashidSDK.isShowInstruction(getApplicationContext());
                                if (qrStatus) {
                                    Intent i = new Intent(MainDrawerScreen.this, QRImplementationActivity.class);
                                    i.putExtra(getResources().getString(R.string.doc_key), selectedDocType);
                                    i.putExtra("instruction", instruction);
                                    startActivity(i);
                                } else {

                                    if (instruction) {
                                        Intent i = new Intent(MainDrawerScreen.this, InstructionScreenActivity.class);
                                        i.putExtra(getResources().getString(R.string.doc_key), selectedDocType);
                                        startActivity(i);
                                    } else {
                                        Intent i = new Intent(MainDrawerScreen.this, StartScanningActivity.class);
                                        i.putExtra(getResources().getString(R.string.doc_key), selectedDocType);
                                        startActivity(i);
                                    }
                                }
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        LoaderUtil.hideLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        logger.log("company scan: " + t);
                        LoaderUtil.hideLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    //    1- for success status
//    0- for failure status
    private void callCompanyScan(JSONObject requestData, String type, int status) {
        LoaderUtil.showLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
        String companyUUID = securePreferences.getString(getString(R.string.company_uuid), "");
        logger.log("company scan:uuid " + companyUUID);
        String scanCode = "";
        boolean qrCode = securePreferences.getBoolean(getString(R.string.qr_code), true);
        if (qrCode) {
            scanCode = securePreferences.getString(getString(R.string.scan_code), "");
        } else {
            scanCode = RandomStringGenerator.generateRandomString(50);
        }
        APIClient.getClient(securePreferences.getString(companyUUID + getString(R.string.backend_base_url), ""), securePreferences.getString(getString(R.string.register_token), "")).create(APIInterface.class)
                .callCompanyScans(type, securePreferences.getString(getString(R.string.company_uuid), ""),
                        requestData.toString(), status, scanCode).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String jsonString = response.body().toString();
                            logger.log("company scan: " + jsonString);

                            if (response.isSuccessful()) {
                            }
                        } catch (Exception e) {

                        }
                        LoaderUtil.hideLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        logger.log("company scan: " + t);
                        LoaderUtil.hideLoader(MainDrawerScreen.this, binding.customLoader.loaderContainer, textSizeConverter);
                    }
                });
    }

}
