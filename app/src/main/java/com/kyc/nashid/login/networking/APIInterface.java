package com.kyc.nashid.login.networking;

import com.google.gson.JsonObject;
import com.kyc.nashid.login.pojoclass.companydomain.CompanyDomain;
import com.kyc.nashid.login.pojoclass.featurecheck.FeatureCheck;
import com.kyc.nashid.login.pojoclass.login.Login;
import com.kyc.nashid.login.pojoclass.recentscan.RecentScan;
import com.kyc.nashid.login.pojoclass.registration.Registration;
import com.kyc.nashid.login.pojoclass.resetpasscod.ResetPassword;
import com.kyc.nashidmrz.networking.models.ApiResponse;
import com.kyc.nashidmrz.networking.models.AutoCrop;
import com.kyc.nashidmrz.networking.models.ValidationResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIInterface {

    @Headers("Content-Type:application/json")
    @POST("v1/images:annotate")
    Call<ApiResponse> doCreateUserWithField(@Query("key") String key, @Body JsonObject jsonObject);

    @POST("/api/check")
    Call<ValidationResponse> validateKey(@Query("action") String action);

    @POST("company-domain")
    Call<CompanyDomain> callCompanyDomain(@Query("code") String action);

//    @POST("/api/check")
//    Call<ValidationResponse> callAutoCrop(@Query("action") String action);

    @FormUrlEncoded
    @POST("get-otp")
    Call<ResponseBody> callOTP(@Field("username") String action);

    @FormUrlEncoded
    @POST("verify-otp")
    Call<ResponseBody> callVerifyOTP(@Field("username") String username,@Field("token") String token,@Field("otp") String otp);

    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> callRegistration(@Field("first_name") String firstName,@Field("last_name") String lastName, @Field("username") String username, @Field("password") String password,
                                        @Field("password_confirmation") String passwordConfirmation, @Field("company_uuid") String companyUuid, @Field("otp") String otp,
                                        @Field("otp_token") String otpToken);

    @FormUrlEncoded
    @POST("reset-password")
    Call<ResetPassword> callResetPassword(@Field("username") String username, @Field("password") String password,
                                          @Field("password_confirmation") String passwordConfirmation, @Field("otp") String otp,
                                          @Field("otp_token") String otpToken);

    @FormUrlEncoded
    @POST("v2/login")
    Call<ResponseBody> callLogin(@Field("user") String user, @Field("password") String password);

    @FormUrlEncoded
    @POST("check-user")
    Call<ResponseBody> callCheckUser(@Field("username") String username);

    @FormUrlEncoded
    @POST("feature-check")
    Call<FeatureCheck> callFeatureCheck(@Field("uuid") String uuid);

    @POST("/segment/v1/")
    @Multipart
    Call<AutoCrop> callAutoCrop(@Part("api_key") RequestBody apiKey, @Part("api_secret") RequestBody apiSecret, @Part MultipartBody.Part image);

//    @POST("user-companies")
//    Call<ResponseBody> callUserCompanies();

    @GET("get-profile")
    Call<ResponseBody> callGetProfile();

    @GET("get-scans")
    Call<RecentScan> callGetRecentScans(@Query("page") int page);

    @FormUrlEncoded
    @POST("update-profile")
    Call<ResponseBody> callUpdateProfile(@Field("first_name") String firstName,@Field("last_name") String lastName,@Field("email") String email,@Field("phone") String phone);

    @FormUrlEncoded
    @POST("company-scans")
    Call<ResponseBody> callCompanyScans(@Field("scan_type") String scanType, @Field("uuid")String uuid,
                                        @Field("scan_data")String scandata,@Field("scan_status")int scanStatus,@Field("scan_code")String scanCode);

    @GET("user-companies")
    Call<ResponseBody> getAllUserCompany();

    @FormUrlEncoded
    @POST("switch-company")
    Call<ResponseBody> callSwitchCompany(@Field("uuid") String uuid);

    @FormUrlEncoded
    @POST("get-qr-status")
    Call<ResponseBody> callQrStatus(@Field("uuid") String uuid);

    @FormUrlEncoded
    @POST("validate-scan-code")
    Call<ResponseBody> callValidateScanCode(@Field("scan_code") String scanCode);

}
