package com.kyc.nashid.rooted;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.util.List;

public class EmulatorDetector {

    public static boolean isEmulator(Context context) {
        return (checkBuild() || checkDevice() || checkPackages(context) || checkFiles() || checkRoot());
    }

    private static boolean checkBuild() {
        return Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
                "google_sdk".equals(Build.PRODUCT);
    }

    private static boolean checkDevice() {
        return "sdk".equals(Build.PRODUCT) || "google_sdk".equals(Build.PRODUCT);
    }

    private static boolean checkPackages(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo app : apps) {
            if (app.packageName.equals("com.bluestacks.appmart") ||
                app.packageName.equals("com.bluestacks.BstCommandProcessor") ||
                app.packageName.equals("com.bluestacks.BstCommandProcessor32") ||
                app.packageName.equals("com.android.vending")) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkFiles() {
        return new File("/system/app/Superuser.apk").exists() ||
                new File("/sbin/su").exists() ||
                new File("/system/bin/su").exists() ||
                new File("/system/xbin/su").exists() ||
                new File("/data/local/xbin/su").exists() ||
                new File("/data/local/bin/su").exists() ||
                new File("/system/sd/xbin/su").exists() ||
                new File("/system/bin/failsafe/su").exists() ||
                new File("/data/local/su").exists();
    }

    private static boolean checkRoot() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            process.waitFor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
