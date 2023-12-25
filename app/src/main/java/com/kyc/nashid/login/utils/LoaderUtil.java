package com.kyc.nashid.login.utils;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyc.nashid.R;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

public class LoaderUtil {

    // Method to show the loader
    public static void showLoader(Activity activity, FrameLayout loaderView, TextSizeConverter textSizeConverter) {
        if (loaderView != null) {
            ImageView imageView = loaderView.findViewById(R.id.img_loader);
            TextView textView = loaderView.findViewById(R.id.txt_validating);

            ViewGroup.LayoutParams layoutParams2 = imageView.getLayoutParams();
            layoutParams2.width = textSizeConverter.getWidth(24);
            layoutParams2.height = textSizeConverter.getHeight(24);
            imageView.setLayoutParams(layoutParams2);

            LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
            layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(16), 0, 0);
            textView.setLayoutParams(layoutParams);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(20));
            textView.setText("Loading...");
            textView.setTextColor(Color.WHITE);

            Animation rotation = AnimationUtils.loadAnimation(activity, R.anim.rotate);
            rotation.setFillAfter(true);
            imageView.startAnimation(rotation);

            loaderView.setVisibility(View.VISIBLE);
            textSizeConverter.changeProgressStatusBarColor(activity);
        }
    }

    // Method to hide the loader
    public static void hideLoader(Activity activity,View loaderView, TextSizeConverter textSizeConverter) {
        if (loaderView != null) {
            loaderView.setVisibility(View.GONE);
            textSizeConverter.changeStatusBarColor(activity);
        }
    }
}
