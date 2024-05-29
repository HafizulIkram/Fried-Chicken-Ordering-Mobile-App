package com.example.kfriesapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kfriesapp.R;
import com.example.kfriesapp.model.Menu;
import com.example.kfriesapp.model.Menu_Order;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

public class Menu_OrderAdapter extends RecyclerView.Adapter<Menu_OrderAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {


        public TextView tvMenuName;
        public TextView tvMenuPrice;
        public TextView tvQuantity;

        // Add a method to set the menuList
        public void setMenuList(List<Menu_Order> menuList) {
            mListData = menuList;
            notifyDataSetChanged();
        }

        public ViewHolder(View itemView) {
            super(itemView);
            tvMenuName = itemView.findViewById(R.id.tvMenuName);
            tvMenuPrice = itemView.findViewById(R.id.tvMenuPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }
    }

    private List<Menu_Order> mListData;   // list of order objects
    private List<Menu> mListMenus;

    private Context mContext;       // activity context
    private int currentPos;         // current selected position.

    public Menu_OrderAdapter(Context context, List<Menu_Order> listData ) {
        mListData = listData;

        mContext = context;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public Menu_OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the single item layout for order
        View view = inflater.inflate(R.layout.ordersmenu_list_item, parent, false);

        // Return a new holder instance
        Menu_OrderAdapter.ViewHolder viewHolder = new Menu_OrderAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(Menu_OrderAdapter.ViewHolder holder, int position) {
        // Bind data to the view holder
        Menu_Order menuOrder = mListData.get(position);


        Object id = menuOrder.getMenuID();

        if (id instanceof LinkedTreeMap<?, ?>) {
            LinkedTreeMap<?, ?> idMap = (LinkedTreeMap<?, ?>) id;
            Log.d("MyApp", "idMap: " + idMap.toString());

            Object menuName = idMap.get("menuName");
            Object menuPrice = idMap.get("menuPrice");

            holder.tvMenuName.setText(String.valueOf(menuName));
            // Format the double value with two decimal places
            String formattedPrice = String.format("%.2f", menuPrice);
            holder.tvMenuPrice.setText(String.valueOf(formattedPrice));
            holder.tvQuantity.setText(String.valueOf(menuOrder.getQty()));

            // Now you have id1 and id2, you can use them as needed
        } else {
            // Handle other cases or throw an exception
            Log.e("MyApp", "Unsupported type for menuID: " + id.getClass().getSimpleName());
        }


        }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public Menu_Order getSelectedItem() {
        if (currentPos >= 0 && mListData != null && currentPos < mListData.size()) {
            return mListData.get(currentPos);
        }
        return null;
    }

    // Inside your adapter

}
