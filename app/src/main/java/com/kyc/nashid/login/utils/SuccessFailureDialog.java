package com.kyc.nashid.login.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.kyc.nashid.R;
import com.kyc.nashid.databinding.DialogRegisterFailureBinding;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

public class SuccessFailureDialog {

    public static void showFailureDialog(Activity activity, TextSizeConverter textSizeConverter, String failureMessage, String registerType, boolean isSuccess, DialogSuccessListener dialogSuccessListener) {
//        AlertDialog builder = new AlertDialog.Builder(this);
        final Dialog dialog = new Dialog(activity);
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

        binding.txtScanCompleted.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(18));
        if (failureMessage.equalsIgnoreCase("apifail")) {
            if (registerType.equalsIgnoreCase("phone")) {

                binding.txtScanCompleted.setText(activity.getString(R.string.phone_error));
            } else {
                binding.txtScanCompleted.setText(activity.getString(R.string.email_error));
            }
        } else if (failureMessage.equalsIgnoreCase("phoneemailfail")) {
            if (registerType.equalsIgnoreCase("phone")) {
                binding.txtScanCompleted.setText(activity.getString(R.string.login_phone_error));
            } else {
                binding.txtScanCompleted.setText(activity.getString(R.string.login_email_error));
            }
        } else {
            binding.txtScanCompleted.setText(failureMessage);
        }
        if (isSuccess) {
            binding.imgScanFailure.setAnimation(R.raw.scan_completed);
            binding.btnRetry.setVisibility(View.GONE);
        } else {
            binding.imgScanFailure.setAnimation(R.raw.failure);
            binding.btnRetry.setVisibility(View.VISIBLE);
        }

        binding.imgScanFailure.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isSuccess)
                    dialog.dismiss();
                if (dialogSuccessListener != null) {
                    dialogSuccessListener.OnSuccessClick();
                }
//                updateClicked = false;
//                setFocusable(false);
//                updateTextColor(ContextCompat.getColor(getApplicationContext(), R.color.profile_text_color));
//                ProfileActivity.this.binding.txtBtnNext.setText(getString(R.string.profile_update));

            }
        });

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
                if (dialogSuccessListener != null) {
                    dialogSuccessListener.OnFailClick();
                }
            }
        });
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
