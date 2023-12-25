package com.kyc.nashid.login.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kyc.nashid.databinding.ItemRecentScanBinding;
import com.kyc.nashid.databinding.ItemTextviewBinding;
import com.kyc.nashid.login.pojoclass.KeyValueItem;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

import java.util.ArrayList;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

    private Context context;
    private OnItemClickListener onChildItemClickListener;
    private ArrayList<KeyValueItem> dataList=new ArrayList<>();

    public SimpleAdapter(Context context,ArrayList<KeyValueItem> dataList) {
        this.dataList = dataList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemTextviewBinding binding = ItemTextviewBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }
    public void setClickListner(OnItemClickListener onChildItemClickListener){
        this.onChildItemClickListener=onChildItemClickListener;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String itemText = dataList.get(position).getValue();
        holder.binding.textViewItem.setText(itemText);
        holder.binding.textViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onChildItemClickListener!=null){
                    onChildItemClickListener.onChildItemClick(dataList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        ItemTextviewBinding binding;

        public ViewHolder(@NonNull ItemTextviewBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
            TextSizeConverter textSizeConverter=new TextSizeConverter(context);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.textViewItem.getLayoutParams();
            int padding = textSizeConverter.getPaddingORMarginValue(20  );
            int padding1 = textSizeConverter.getPaddingORMarginValue(18);
            if (getAdapterPosition() == getItemCount() - 1) {
                layoutParams.setMargins(padding, padding1, padding, padding1);
            }else{
                layoutParams.setMargins(padding, padding1, padding, 0);
            }
            binding.textViewItem.setLayoutParams(layoutParams);
            binding.textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        }
    }
}
