package com.kyc.nashid.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.kyc.nashid.databinding.ActivityRegisterAccountDetailBinding;
import com.kyc.nashid.R;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

public class RegisterAccountDetailActivity extends AppCompatActivity {

    private ActivityRegisterAccountDetailBinding binding;
    private TextSizeConverter textSizeConverter;
    private String loginType;
    private String otp;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterAccountDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initClick();
    }

    private void initClick() {
        binding.layoutHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisteredAccountDetailActivity();
            }
        });
    }

    private void initView() {
        loginType = getIntent().getStringExtra("loginType");
        otp = getIntent().getStringExtra("otp");
        username = getIntent().getStringExtra("username");
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
        textSizeConverter.changeStatusBarColor(RegisterAccountDetailActivity.this);

        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.txtHelp.setText(getString(R.string.register_header_title));
        binding.layoutHeader.txtHelp.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(17));

        int padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.lytMainAccountDetail.setPadding(padding, padding, padding, padding);

        padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.btnRegister.setPadding(0, padding, 0, padding);
        binding.btnRegister.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.cardBtnRegister.setRadius(textSizeConverter.calculateRadius(8));

        LinearLayout.LayoutParams marginLyoutParam = (LinearLayout.LayoutParams) binding.txtHeader.getLayoutParams();
        marginLyoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(20), 0, textSizeConverter.getPaddingORMarginValue(30));
        binding.txtHeader.setLayoutParams(marginLyoutParam);

        marginLyoutParam = (LinearLayout.LayoutParams) binding.layoutHeader.txtHelp.getLayoutParams();
        marginLyoutParam.setMargins(textSizeConverter.getPaddingORMarginValue(20), 0, 0, 0);
        binding.layoutHeader.txtHelp.setLayoutParams(marginLyoutParam);
        binding.txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(28));

        binding.txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        binding.edtFirstName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.edtLastName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        binding.cardBtnRegister.setRadius(textSizeConverter.calculateRadius(8));
        int edtPadding = textSizeConverter.getPaddingORMarginValue(12);
        binding.edtFirstName.setPadding(textSizeConverter.getPaddingORMarginValue(24), edtPadding, edtPadding, edtPadding);
        binding.edtLastName.setPadding(textSizeConverter.getPaddingORMarginValue(24), edtPadding, edtPadding, edtPadding);
        layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(10), 0, 0);
        binding.edtLastName.setLayoutParams(layoutParams);

    }

    private void openRegisteredAccountDetailActivity() {
        Intent intent = new Intent(RegisterAccountDetailActivity.this, CreatePinCodeActivity.class);
        intent.putExtra("loginType", loginType);
        intent.putExtra("otp", otp);
        intent.putExtra("username", username);
        intent.putExtra("firstname", binding.edtFirstName.getText().toString());
        intent.putExtra("lastname", binding.edtLastName.getText().toString());
        startActivity(intent);
    }

}
