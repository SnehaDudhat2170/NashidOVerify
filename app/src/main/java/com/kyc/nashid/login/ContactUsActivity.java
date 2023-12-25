package com.kyc.nashid.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.kyc.nashid.R;
import com.kyc.nashid.databinding.ActivityContatctUsBinding;
import com.kyc.nashid.rooted.RootedCheck;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

public class ContactUsActivity extends AppCompatActivity {

    private ActivityContatctUsBinding binding;
    private TextSizeConverter textSizeConverter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContatctUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RootedCheck.getInstance().setFlag(ContactUsActivity.this);
        if (RootedCheck.getInstance().isRootedDevice(getApplicationContext())) {
            RootedCheck.getInstance().showRootedDeviceDialog(this,getString(R.string.root_dialog),getString(R.string.root_desc),getString(R.string.root_btn));
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
        binding.cardSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"+getString(R.string.contact_email))); // Replace with the recipient's email address
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Contact"); // Email subject
                    intent.putExtra(Intent.EXTRA_TEXT, ""); // Email body
                    // Specify the package name of the email client (e.g., Gmail)
                    intent.setPackage("com.google.android.gm");

                    try {
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ContactUsActivity.this, "Email client not found.", Toast.LENGTH_SHORT).show();
                }
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
    }

    private void setLayoutAndTextSize() {
        textSizeConverter = new TextSizeConverter(getApplicationContext());
        textSizeConverter.changeStatusBarColor(ContactUsActivity.this);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.txtHelp.setText(getString(R.string.contact_header));
        binding.layoutHeader.txtHelp.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(17));

        int padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.lytContactMain.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams marginLyoutParam;
        marginLyoutParam = (LinearLayout.LayoutParams) binding.txtHeader.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(20), 0, textSizeConverter.getPaddingORMarginValue(30));
        binding.txtHeader.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.layoutHeader.txtHelp.getLayoutParams();
        marginLyoutParam.setMargins(textSizeConverter.getPaddingORMarginValue(20), 0, 0, 0);
        binding.layoutHeader.txtHelp.setLayoutParams(marginLyoutParam);
        binding.txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(28));

        binding.txtHeader.setText(getString(R.string.contact_title));
        binding.txtDesc.setText(getString(R.string.contact_subtitle));

        binding.txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(30), 0, 0);
        binding.cardSupport.setLayoutParams(layoutParams);
        binding.cardSupport.setRadius(textSizeConverter.calculateRadius(8));

        layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(textSizeConverter.getPaddingORMarginValue(25),0 , 0, 0);
        binding.imgSupport.setLayoutParams(layoutParams);

        layoutParams2 = binding.imgSupport.getLayoutParams();
        layoutParams2.width = textSizeConverter.getWidth(24);
        layoutParams2.height = textSizeConverter.getHeight(24);
        binding.imgSupport.setLayoutParams(layoutParams2);

        padding=textSizeConverter.getPaddingORMarginValue(10);
        binding.txtSupport.setPadding(padding, padding,padding,padding);

    }
}
