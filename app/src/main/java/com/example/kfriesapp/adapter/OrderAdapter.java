package com.example.kfriesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kfriesapp.R;
import com.example.kfriesapp.model.Orders;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView txtOrderID;
        public TextView txtOrderStatus;
        public TextView txtOrderDate;

        public ViewHolder(View itemView) {
            super(itemView);

            txtOrderID = (TextView) itemView.findViewById(R.id.txtOrderID);
            txtOrderDate = (TextView) itemView.findViewById(R.id.txtOrderDate);
            txtOrderStatus = (TextView) itemView.findViewById(R.id.txtOrderStatus);


            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }
    }

    private List<Orders> mListData;   // list of order objects
    private Context mContext;       // activity context
    private int currentPos;         // current selected position.

    public OrderAdapter(Context context, List<Orders> listData) {
        mListData = listData;
        mContext = context;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the single item layout for order
        View view = inflater.inflate(R.layout.order_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder
        Orders orders = mListData.get(position);
        holder.txtOrderID.setText("Order ID: " + orders.getOrderID());
        holder.txtOrderStatus.setText("Order Status: " + orders.getOrderStatus());
        holder.txtOrderDate.setText("Order Date: " + orders.getOrderDate());

    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public Orders getSelectedItem() {
        if (currentPos >= 0 && mListData != null && currentPos < mListData.size()) {
            return mListData.get(currentPos);
        }
        return null;
    }
}