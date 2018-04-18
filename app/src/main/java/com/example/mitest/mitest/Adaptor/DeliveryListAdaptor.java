package com.example.mitest.mitest.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mitest.mitest.Constant;
import com.example.mitest.mitest.DetailActivity;
import com.example.mitest.mitest.Util.ImageLoader;
import com.example.mitest.mitest.Model.Delivery;
import com.example.mitest.mitest.R;

import java.util.ArrayList;

public class DeliveryListAdaptor extends RecyclerView.Adapter<DeliveryListAdaptor.DeliveryViewHolder> {
    private ArrayList<Delivery> mDeliveries = new ArrayList<>();
    private Context mContext;

    public DeliveryListAdaptor(Context context, ArrayList<Delivery> deliveries) {
        mContext = context;
        mDeliveries = deliveries;
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adaptor, parent, false);
        DeliveryViewHolder viewHolder = new DeliveryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        holder.bindDelivery(mDeliveries.get(position));
    }

    @Override
    public int getItemCount() {
        return mDeliveries == null ? 0 : mDeliveries.size();
    }

    public class DeliveryViewHolder extends RecyclerView.ViewHolder {
        ImageView mDeliveryImageView;
        TextView mDeliveryTextView;

        private Context mContext;
        private ImageLoader mImageLoader;

        public DeliveryViewHolder(View itemView) {
            super(itemView);
            mDeliveryImageView = itemView.findViewById(R.id.imageView);
            mDeliveryTextView = itemView.findViewById(R.id.textView);
            mContext = itemView.getContext();
            mImageLoader = new ImageLoader(mContext);
        }

        public void bindDelivery(final Delivery delivery) {
            mImageLoader.DisplayImage(delivery.imageUrl, mDeliveryImageView);
            mDeliveryTextView.setText(delivery.description);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constant.INTENT_NAME, delivery);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
