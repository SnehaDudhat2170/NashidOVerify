package com.kyc.nashid.login.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.kyc.nashidmrz.databinding.ItemViewpagerBinding;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

import java.util.ArrayList;

public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String>  strings=new ArrayList<>();

    public CustomPagerAdapter(Context context,ArrayList<String> strings) {
        mContext = context;
        this.strings=strings;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ItemViewpagerBinding binding=ItemViewpagerBinding.inflate(inflater);
//        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_viewpager, collection, false);
        setAllView(binding,position);
        collection.addView(binding.getRoot());
        return binding.getRoot();
    }
    private void setAllView(ItemViewpagerBinding binding, int position){
        TextSizeConverter textSizeConverter = new TextSizeConverter(mContext);
        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(10), 0, 0);
        binding.imgDoc.setLayoutParams(layoutParams);

        binding.txtDoc.setText(strings.get(position));
        binding.txtDoc.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        int padding=textSizeConverter.getPaddingORMarginValue(10);
        binding.imgDoc.setPadding(padding,0,padding,0);
    }


    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return strings.get(position);
    }

}