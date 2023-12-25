package com.kyc.nashid.login.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kyc.nashid.R;
import com.kyc.nashid.databinding.ItemParentBinding;
import com.kyc.nashid.login.pojoclass.ChildItem;
import com.kyc.nashid.login.pojoclass.ParentItem;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;

import java.util.List;

public class ExpandableRecyclerAdapter extends RecyclerView.Adapter<ExpandableRecyclerAdapter.ViewHolder> {
    private List<ParentItem> parentItems;
    private Context context;
    private OnChildItemClickListener onChildItemClickListener;

    public void setOnChildItemClickListener(OnChildItemClickListener listener) {
        this.onChildItemClickListener = listener;
    }

    public ExpandableRecyclerAdapter(Context context, List<ParentItem> parentItems) {
        this.context = context;
        this.parentItems = parentItems;
    }

    public void setItem(List<ParentItem> parentItems) {
        this.parentItems = parentItems;
        notifyDataSetChanged();
    }
    // Adapter methods - onCreateViewHolder, onBindViewHolder, getItemViewType, getItemCount...


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemParentBinding binding = ItemParentBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent, parent, false);
//        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParentItem parentItem = parentItems.get(position);
        holder.binding.parentTextView.setText(parentItem.getTitle());

        if (parentItem.isExpanded()) {
            // Show child items
            holder.binding.imgDropdown.setImageResource(R.drawable.arrow_up);
            holder.binding.childLayout.setVisibility(View.VISIBLE);
            holder.binding.childLayout.removeAllViews(); // Clear previous child items
            for (ChildItem childItem : parentItem.getChildren()) {
                View childView = LayoutInflater.from(context).inflate(R.layout.item_child, null);
                TextView childTextView = childView.findViewById(R.id.childTextView);
                View divider = childView.findViewById(R.id.divider);
                TextSizeConverter textSizeConverter = new TextSizeConverter(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width (MATCH_PARENT or a specific width in pixels or dp)
                        LinearLayout.LayoutParams.WRAP_CONTENT  // Height (MATCH_PARENT or a specific height in pixels or dp)
                );
                layoutParams.setMargins(textSizeConverter.getPaddingORMarginValue(20), textSizeConverter.getPaddingORMarginValue(18), textSizeConverter.getPaddingORMarginValue(20), textSizeConverter.getPaddingORMarginValue(18));
                childTextView.setLayoutParams(layoutParams);

                layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width (MATCH_PARENT or a specific width in pixels or dp)
                        textSizeConverter.getPaddingORMarginValue(1)  // Height (MATCH_PARENT or a specific height in pixels or dp)
                );
                layoutParams.setMargins(textSizeConverter.getPaddingORMarginValue(20), 0, textSizeConverter.getPaddingORMarginValue(20), 0);
                divider.setLayoutParams(layoutParams);

                childTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
                childTextView.setText(childItem.getName());
                childTextView.setOnClickListener(view -> {
                    parentItem.setExpanded(!parentItem.isExpanded());
                    if (onChildItemClickListener != null) {
                        onChildItemClickListener.onChildItemClick(childItem.getName());
                    }
                });

                holder.binding.childLayout.addView(childView);
            }
        } else {
            holder.binding.imgDropdown.setImageResource(R.drawable.arrow_down);
            // Hide child items
            holder.binding.childLayout.setVisibility(View.GONE);
        }

        // Set a click listener for the parent item
        holder.binding.layoutParent.setOnClickListener(view -> {

            parentItem.setExpanded(!parentItem.isExpanded());
            notifyDataSetChanged(); // Notify data change
        });
    }

    @Override
    public int getItemCount() {
        return parentItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemParentBinding binding;

        ViewHolder(ItemParentBinding itemParentBinding) {
            super(itemParentBinding.getRoot());
            TextSizeConverter textSizeConverter = new TextSizeConverter(context);
            itemParentBinding.parentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Width (MATCH_PARENT or a specific width in pixels or dp)
                    LinearLayout.LayoutParams.WRAP_CONTENT  // Height (MATCH_PARENT or a specific height in pixels or dp)
            );
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(textSizeConverter.getPaddingORMarginValue(20), textSizeConverter.getPaddingORMarginValue(18), 0, textSizeConverter.getPaddingORMarginValue(18));
            itemParentBinding.imgHomeCompany.setLayoutParams(layoutParams);

            layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Width (MATCH_PARENT or a specific width in pixels or dp)
                    LinearLayout.LayoutParams.WRAP_CONTENT  // Height (MATCH_PARENT or a specific height in pixels or dp)
            );
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(0, 0, textSizeConverter.getPaddingORMarginValue(20), 0);
            itemParentBinding.imgDropdown.setLayoutParams(layoutParams);

            layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // Width (MATCH_PARENT or a specific width in pixels or dp)
                    LinearLayout.LayoutParams.WRAP_CONTENT  // Height (MATCH_PARENT or a specific height in pixels or dp)
                    , 1f);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(textSizeConverter.getPaddingORMarginValue(10), 0, textSizeConverter.getPaddingORMarginValue(10), 0);
            itemParentBinding.parentTextView.setLayoutParams(layoutParams);

            ViewGroup.LayoutParams layoutParams2 = itemParentBinding.imgDropdown.getLayoutParams();
            layoutParams2.width = textSizeConverter.getWidth(12);
            layoutParams2.height = textSizeConverter.getHeight(12);
            itemParentBinding.imgDropdown.setLayoutParams(layoutParams2);

            layoutParams2 = itemParentBinding.imgHomeCompany.getLayoutParams();
            layoutParams2.width = textSizeConverter.getWidth(26);
            layoutParams2.height = textSizeConverter.getHeight(20);
            itemParentBinding.imgHomeCompany.setLayoutParams(layoutParams2);

            binding = itemParentBinding;
        }
    }
}
