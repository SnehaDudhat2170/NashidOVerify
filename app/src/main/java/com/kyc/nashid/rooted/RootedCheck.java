package com.kyc.nashid.rooted;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;


import java.io.File;
import java.io.IOException;

public class RootedCheck {

    // private static instance variable to hold the singleton instance
    private static volatile RootedCheck INSTANCE = null;

    // private constructor to prevent instantiation of the class
    private RootedCheck() {
    }

    // public static method to retrieve the singleton instance
    public static RootedCheck getInstance() {
        // Check if the instance is already created
        if (INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (RootedCheck.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new RootedCheck();
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
    }

    public void showRootedDeviceDialog(Activity activity, String title, String desc, String buttonName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(desc);
        builder.setPositiveButton(buttonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish(); // Close the app
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //    public void showDebuggingDeviceDialog(Activity activity) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("Disable USB Debugging");
//        builder.setMessage("Please disable USB debugging before using this app.");
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                activity.finish(); // Close the app
//            }
//        });
//        builder.setCancelable(false);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
    private static final String GOLDFISH = "goldfish";
    private static final String RANCHU = "ranchu";
    private static final String SDK = "sdk";

    public static boolean isEmulator() {
        // TODO(mrober): Consider other heuristics to decide if we are running on an Android emulator.
        return Build.PRODUCT.contains(SDK)
                || Build.HARDWARE.contains(GOLDFISH)
                || Build.HARDWARE.contains(RANCHU);
    }

    public boolean isRootedDevice(Context context) {
        // No reliable way to determine if an android phone is rooted, since a rooted phone could
        // always disguise itself as non-rooted. Some common approaches can be found on SO:
        //   http://stackoverflow.com/questions/1101380/determine-if-running-on-a-rooted-device
        //
        // http://stackoverflow.com/questions/3576989/how-can-you-detect-if-the-device-is-rooted-in-the-app
        //
        // http://stackoverflow.com/questions/7727021/how-can-androids-copy-protection-check-if-the-device-is-rooted
        final boolean isEmulator = isEmulator();
        final String buildTags = Build.TAGS;
        if (!isEmulator && buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        // Superuser.apk would only exist on a rooted device:
        File file = new File("/system/app/Superuser.apk");
        if (file.exists()) {
            return true;
        }

        // su is only available on a rooted device (or the emulator)
        // The user could rename or move to a non-standard location, but in that case they
        // probably don't want us to know they're root and they can pretty much subvert
        // any check anyway.
        file = new File("/system/xbin/su");
        if (!isEmulator && file.exists()) {
            return true;
        }
        return false;
    }

    //    public  boolean isRootedDevice(Context context) {
//
//        boolean rootedDevice = false;
//        /*String buildTags = android.os.Build.TAGS;
//        if (buildTags != null && buildTags.contains("test-keys")) {
//            Log.e("Root Detected", "1");
//            rootedDevice = true;
//        }
//
//        // check if /system/app/Superuser.apk is present
//        try {
//            File file = new File("/system/app/Superuser.apk");
//            if (file.exists()) {
//                Log.e("Root Detected", "2");
//                rootedDevice = true;
//            }
//        } catch (Throwable e1) {
//            //Ignore
//        }
//
//        //check if SU command is executable or not
//        try {
//            Runtime.getRuntime().exec("su");
//            Log.e("Root Detected", "3");
//            rootedDevice = true;
//        } catch (IOException localIOException) {
//            //Ignore
//        }
//
//        //check weather busy box application is installed
//        String packageName = "stericson.busybox"; //Package for busy box app
//        PackageManager pm = context.getPackageManager();
//        try {
//            Log.e("Root Detected", "4");
//            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
//            rootedDevice = true;
//        } catch (PackageManager.NameNotFoundException e) {
//            //App not installed
//        }
//*/
//        Process process = null;
//        try {
//            process = Runtime.getRuntime().exec("su");
//            rootedDevice=true;
//        } catch (Exception e) {
//        } finally {
//            if (process != null) {
//                try {
//                    process.destroy();
//                } catch (Exception e) { }
//            }
//        }
//
////        RootBeer rootBeer = new RootBeer(context);
////        return rootBeer.isRooted();
//        return rootedDevice;
//    }
    public boolean isEmulator(Context context) {
        return EmulatorDetector.isEmulator(context);
//        return Build.MANUFACTURER.contains("Genymotion")
//                || Build.MODEL.contains("google_sdk")
//                || Build.MODEL.toLowerCase().contains("droid4x")
//                || Build.MODEL.contains("Emulator")
//                || Build.MODEL.contains("Android SDK built for x86")
//                || Build.HARDWARE == "goldfish"
//                || Build.HARDWARE == "vbox86"
//                || Build.HARDWARE.toLowerCase().contains("nox")
//                || Build.FINGERPRINT.startsWith("generic")
//                || Build.PRODUCT == "sdk"
//                || Build.PRODUCT == "google_sdk"
//                || Build.PRODUCT == "sdk_x86"
//                || Build.PRODUCT == "vbox86p"
//                || Build.PRODUCT.toLowerCase().contains("nox")
//                || Build.BOARD.toLowerCase().contains("nox")
//                || (Build.BRAND.startsWith("generic") &&    Build.DEVICE.startsWith("generic"));
    }

    public void setFlag(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }
}
