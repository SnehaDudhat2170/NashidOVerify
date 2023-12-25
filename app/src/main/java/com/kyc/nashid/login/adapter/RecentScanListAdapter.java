package com.kyc.nashid.login.adapter;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.kyc.nashid.login.pojoclass.recentscan.Datum;
import com.kyc.nashid.databinding.ItemRecentScanBinding;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class RecentScanListAdapter extends RecyclerView.Adapter<RecentScanListAdapter.ViewHolder> {

    private ArrayList<Datum> recentScanData=new ArrayList<>();
    private Context context;

    public RecentScanListAdapter(Context context,ArrayList<Datum> recentScanData) {
        this.context = context;
        this.recentScanData=recentScanData;
    }
    public void addAll(ArrayList<Datum> recentScanData){
        this.recentScanData.addAll(recentScanData);
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemRecentScanBinding binding = ItemRecentScanBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemRecentScanBinding.txtRecentRefid.setText(recentScanData.get(position).getSlug());
        holder.itemRecentScanBinding.txtRecentDate.setText(getDateFormate(recentScanData.get(position).getUpdatedAt(),true));
        holder.itemRecentScanBinding.txtRecentTime.setText(getDateFormate(recentScanData.get(position).getUpdatedAt(),false));

    }
    private String getDateFormate(String originalDate,boolean isDate){
        try {
            // Define the input format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Parse the original date
            Date date = inputFormat.parse(originalDate);

            // Define the output format with the device's default time zone
            SimpleDateFormat outputFormat ;
            // To use the device's time zone, don't set the time zone explicitly here
            if (isDate) {
                outputFormat = new SimpleDateFormat("dd MMM yyyy");
            } else {
                outputFormat = new SimpleDateFormat("HH:mm aa");
            }
            // Format the date
            String formattedDate = outputFormat.format(date);

            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "Date Conversion Error";
        }

    }

    @Override
    public int getItemCount() {
        return recentScanData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemRecentScanBinding itemRecentScanBinding;

        public ViewHolder(ItemRecentScanBinding itemSelectDocumentBinding) {
            super(itemSelectDocumentBinding.getRoot());
            itemSelectDocumentBinding.txtRecentRef.setTextSize(TypedValue.COMPLEX_UNIT_PX, new TextSizeConverter(context).getTextSize(12));
            itemSelectDocumentBinding.txtRecentRefid.setTextSize(TypedValue.COMPLEX_UNIT_PX, new TextSizeConverter(context).getTextSize(16));
            itemSelectDocumentBinding.txtRecentDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, new TextSizeConverter(context).getTextSize(12));
            itemSelectDocumentBinding.txtRecentTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, new TextSizeConverter(context).getTextSize(16));

            TextSizeConverter textSizeConverter=new TextSizeConverter(context);

            LinearLayout.LayoutParams cardLayoutParam = (LinearLayout.LayoutParams) itemSelectDocumentBinding.recentDivider.getLayoutParams();
            cardLayoutParam.setMargins(0, textSizeConverter.getPaddingORMarginValue(10), 0,  textSizeConverter.getPaddingORMarginValue(10));
            itemSelectDocumentBinding.recentDivider.setLayoutParams(cardLayoutParam);
            this.itemRecentScanBinding = itemSelectDocumentBinding;
        }
    }
}