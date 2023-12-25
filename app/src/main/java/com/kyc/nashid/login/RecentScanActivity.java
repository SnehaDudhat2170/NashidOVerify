package com.kyc.nashid.login;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.kyc.nashid.databinding.ActivityRecentScanBinding;
import com.kyc.nashid.login.adapter.ExpandableRecyclerAdapter;
import com.kyc.nashid.login.adapter.RecentScanListAdapter;
import com.kyc.nashid.login.networking.APIClient;
import com.kyc.nashid.login.networking.APIInterface;
import com.kyc.nashid.login.pojoclass.ChildItem;
import com.kyc.nashid.login.pojoclass.ParentItem;
import com.kyc.nashid.login.pojoclass.login.Login;
import com.kyc.nashid.login.pojoclass.recentscan.RecentScan;
import com.kyc.nashid.login.utils.LoaderUtil;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashid.Logger;
import com.kyc.nashid.R;
import com.kyc.nashid.login.utils.SuccessFailureDialog;
import com.kyc.nashidmrz.NashidSDK;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentScanActivity extends AppCompatActivity {

    private ActivityRecentScanBinding binding;
    private TextSizeConverter textSizeConverter;
    private Logger logger = Logger.withTag(this.getClass().getSimpleName());
    private SharedPreferences securePreferences;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private LinearLayoutManager linearLayoutManager;
    private int page = 1;
    private RecentScanListAdapter recentScanListAdapter;
    private String token="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecentScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initClick();
        getAllCompanylist();
    }

    private void initClick() {
        binding.recyclerviewRecentScan.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreData();
                    }
                }
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
        setLayoutAndTextSize();
        token=securePreferences.getString(getString(R.string.register_token), "");
        callRecentScan(token);
        if (securePreferences.getBoolean(getString(R.string.dealer),false)) {
            binding.cardSelectCompany.setVisibility(View.VISIBLE);
        }else{
            binding.cardSelectCompany.setVisibility(View.GONE);
        }
    }

    private void setLayoutAndTextSize() {

        securePreferences = new SecurePreferences(this);

        textSizeConverter = new TextSizeConverter(getApplicationContext());
        textSizeConverter.changeStatusBarColor(RecentScanActivity.this);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.txtHelp.setText(getString(R.string.history_header));
        binding.layoutHeader.txtHelp.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(17));

        int padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.lytMainRecentScan.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams marginLyoutParam;
        marginLyoutParam = (LinearLayout.LayoutParams) binding.txtHeader.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(20), 0, textSizeConverter.getPaddingORMarginValue(30));
        binding.txtHeader.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.layoutHeader.txtHelp.getLayoutParams();
        marginLyoutParam.setMargins(textSizeConverter.getPaddingORMarginValue(20), 0, 0, 0);
        binding.layoutHeader.txtHelp.setLayoutParams(marginLyoutParam);
        binding.txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(28));

        binding.txtHeader.setText(getString(R.string.history_title));
        binding.cardSelectCompany.setRadius(textSizeConverter.calculateRadius(8));

        marginLyoutParam = (LinearLayout.LayoutParams) binding.recyclerviewRecentScan.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(30), 0, 0);
        binding.recyclerviewRecentScan.setLayoutParams(marginLyoutParam);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerviewRecentScan.setLayoutManager(linearLayoutManager);
        recentScanListAdapter = new RecentScanListAdapter(getApplicationContext(), new ArrayList<>());
        binding.recyclerviewRecentScan.setAdapter(recentScanListAdapter);
    }

    private void callRecentScan(String token) {
        LoaderUtil.showLoader(RecentScanActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        SharedPreferences securePreferences = new SecurePreferences(this);
        String companyUUID=securePreferences.getString(getString(R.string.company_uuid),"");
        Log.d("TAG", "callRecentScan: "+securePreferences.getString(companyUUID+getString(R.string.backend_base_url), "")+"\n"+token);
        APIClient.getClient(securePreferences.getString(companyUUID+getString(R.string.backend_base_url), ""), token).create(APIInterface.class)
                .callGetRecentScans(page)
                .enqueue(new Callback<RecentScan>() {
                    @Override
                    public void onResponse(Call<RecentScan> call, Response<RecentScan> response) {
                        logger.log("callingfeaturescan: " + response.body());
                        LoaderUtil.hideLoader(RecentScanActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        try {
                            if (response.isSuccessful()) {

//                            String jsonResponse = response.body().string();
                                logger.log("calling featurescan: " + response.body());
                                Log.d("TAG", "addAll:activity "+response.body().getData().getScans().getData().size());
                                if (response.body().getData().getScans().getNextPageUrl() == null) {
                                    isLastPage = true;
                                }else{
                                    page=page+1;
                                }
                                recentScanListAdapter.addAll(response.body().getData().getScans().getData());
                                // Allow more data to be loaded.
                                isLoading = false;

                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailure(Call<RecentScan> call, Throwable t) {
                        logger.log("featurescan: " + t);
                        LoaderUtil.hideLoader(RecentScanActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
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

    private HashMap<String, String> stringStringHashMap = new HashMap<>();

    private void getAllCompanylist() {
//        156|r05eJQUaHXZ5NmWxeBYWTtBdpEyTJV8Gi1nLzBgY29316e2e
//        securePreferences.getString(getString(R.string.nashid_token), "")

        logger.log("getallcompany: " + securePreferences.getString(getString(R.string.nashid_token), ""));
        APIClient.getClient(securePreferences.getString(getString(R.string.user_companies), ""), securePreferences.getString(getString(R.string.nashid_token), "")).create(APIInterface.class)
                .getAllUserCompany().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        logger.log("getallcompany: " + response.body());
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            if (response.isSuccessful()) {
                                try {
                                    String responseBodyString = responseBody.string();
                                    logger.log("getallcompany: " + responseBodyString);

                                    // Now parse the string into a JSONObject
                                    JSONObject jsonObject = new JSONObject(responseBodyString);
                                    JSONObject parentData = jsonObject.getJSONObject("data");
                                    JSONObject childData = parentData.getJSONObject("data");
                                    Iterator<String> keys = childData.keys();
                                    List<ParentItem> parentItems = new ArrayList<>();
                                    List<ChildItem> childItems = new ArrayList<>();
                                    int parentITem=0;
                                    int i=0;
                                    while (keys.hasNext()) {

                                        String key = keys.next();
                                        String value = childData.getString(key);
                                        stringStringHashMap.put(key, value);
                                        childItems.add(new ChildItem(value));
                                        // Now you can work with the key and value as needed
                                        System.out.println("Key---: " + key + ", Value: " + value);
                                        if (securePreferences.getString(getString(R.string.company_uuid), "").equalsIgnoreCase(key)){
                                            parentITem=i;
                                        }
                                        i++;
                                    }

                                    parentItems.add(new ParentItem(childItems.get(parentITem).getName(), childItems));
//                                String[] data=new String[stringStringHashMap.values().size()];
//                                ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_selected, strings);
//                                adapter.setDropDownViewResource(R.layout.spinner_item_selected);
//
//                                binding.spinner.setAdapter(adapter);
                                    ExpandableRecyclerAdapter adapter = new ExpandableRecyclerAdapter(getApplicationContext(), parentItems);
                                    adapter.setOnChildItemClickListener(childName -> {
                                        // Handle the child item click here
                                        List<ParentItem> parentItems1 = new ArrayList<>();
                                        parentItems1.add(new ParentItem(childName, childItems));
                                        adapter.setItem(parentItems1);
                                        for (Map.Entry<String, String> entry : stringStringHashMap.entrySet()) {

                                            // if give value is equal to value from entry
                                            // print the corresponding key
                                            if (entry.getValue().equalsIgnoreCase(childName)) {
                                                callSwitchCompany(entry.getKey());
                                                break;
                                            }
                                        }

                                    });

                                    binding.recyclerviewSelectCompany.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    binding.recyclerviewSelectCompany.setAdapter(adapter);
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

                    }
                });
    }

    private void callSwitchCompany(String companyUUID) {
        logger.log("switchcompany:" + securePreferences.getString(getString(R.string.nashid_token), "") + " " + companyUUID);
        APIClient.getClient(securePreferences.getString(getString(R.string.switch_company), ""), securePreferences.getString(getString(R.string.nashid_token), "")).create(APIInterface.class)
                .callSwitchCompany(companyUUID).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        logger.log("calling: switch api " + response.body());
                        if (response.isSuccessful()) {
                            try {
                                    String jsonResponse = response.body().string();
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
                                        logger.log("Company_name:switch "+securePreferences.getString(getString(R.string.company_name),""));                                        page=1;
                                        isLastPage=false;
                                        isLoading=false;
                                        recentScanListAdapter = new RecentScanListAdapter(getApplicationContext(), new ArrayList<>());
                                        binding.recyclerviewRecentScan.setAdapter(recentScanListAdapter);
                                        token=registerData.getString("token");
                                        callRecentScan(token);
                                    }else{
                                    }
//                                securePreferences.edit().putString(getString(R.string.register_name), login.get).apply();

                            } catch (Exception e) {

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });

    }

    private void loadMoreData() {
        callRecentScan(token);
//        // Load the next page of data using the API's next_page_url.
//        // Update your dataset with the new data.
//
//        // Example: Make a network request using Retrofit or any other method.
//        // You should adjust this code according to your API client.
//        apiService.getScans(nextPageUrl)
//                .enqueue(new Callback<YourApiResponse>() {
//                    @Override
//                    public void onResponse(Call<YourApiResponse> call, Response<YourApiResponse> response) {
//                        // Update your data list with the new items.
//
//                        // Check if this is the last page.
//                        if (response.body().data.scans.next_page_url == null) {
//                            isLastPage = true;
//                        }
//
//                        // Allow more data to be loaded.
//                        isLoading = false;
//                    }
//
//                    @Override
//                    public void onFailure(Call<YourApiResponse> call, Throwable t) {
//                        // Handle the error.
//                        isLoading = false;
//                    }
//                });
    }

}
