#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_kyc_nashid_login_networking_AllUrls_loginDevUrl(
        JNIEnv* env,
        jclass clazz) {
//    https://dashboard.dev.projectnid.com/api/check
    std::string baseURL = "https://dashboard.dev.projectnid.com/api/";
    return env->NewStringUTF(baseURL.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_kyc_nashid_login_networking_AllUrls_loginProUrl(
        JNIEnv* env,
        jclass clazz) {
//    https://dashboard.dev.projectnid.com/api/check
    std::string baseURL = "https://dashboard.projectnid.com/api/";
    return env->NewStringUTF(baseURL.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_kyc_nashid_login_networking_AllUrls_mainDevUrl(
        JNIEnv* env,
        jclass clazz) {
//    https://dashboard.dev.projectnid.com/api/check
    std::string baseURL = "https://dashboard.dev.projectnid.com/api/";
    return env->NewStringUTF(baseURL.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_kyc_nashid_login_networking_AllUrls_mainProUrl(
        JNIEnv* env,
        jclass clazz) {
//    https://dashboard.dev.projectnid.com/api/check
    std::string baseURL = "https://dashboard.projectnid.com/api/";
    return env->NewStringUTF(baseURL.c_str());
}
