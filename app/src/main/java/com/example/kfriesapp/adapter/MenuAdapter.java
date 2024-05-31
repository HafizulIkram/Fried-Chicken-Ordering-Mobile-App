package com.example.kfriesapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kfriesapp.R;
import com.example.kfriesapp.model.Menu;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private List<Menu> menuList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onAddToCartClick(Menu menu);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

        /**
         * Create ViewHolder class to bind list item view
         */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        public TextView txtMenuName;
        public TextView txtMenuPrice;

        public TextView  addToCartButton;
        public EditText etCount;
        public LinearLayout addMoreLayout;


            private OnItemClickListener mListener;

            public void setOnItemClickListener(OnItemClickListener listener){
                mListener = listener;
            }


        public ViewHolder(View itemView) {
            super(itemView);

            txtMenuName = (TextView) itemView.findViewById(R.id.txtMenuName);
            txtMenuPrice = (TextView) itemView.findViewById(R.id.txtMenuPrice);
            etCount = itemView.findViewById(R.id.etCount);
            addMoreLayout  = itemView.findViewById(R.id.addMoreLayout);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            itemView.setOnLongClickListener(this);
        }




            @Override
            public boolean onLongClick(View view) {
                currentPos = getAdapterPosition();
                return false;
            }
    }

    private List<Menu> mListData;   // list of order objects
    private Context mContext;       // activity context
    private int currentPos;         // current selected position.

    public MenuAdapter(Context context, List<Menu> listData) {
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
        View view = inflater.inflate(R.layout.activity_ordermenu_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // bind data to the view holder
        Menu menus = mListData.get(position);
        holder.txtMenuName.setText("" + menus.getMenuName());

        // Format the double value with two decimal places
        String formattedPrice = String.format("%.2f", menus.getMenuPrice());
        holder.txtMenuPrice.setText("RM " + formattedPrice);

        // Set the OnClickListener for addToCartButton
        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle your other logic here
                menus.setQuantity(1);

                holder.addMoreLayout.setVisibility(View.VISIBLE);
                holder.addToCartButton.setVisibility(View.GONE);
                holder.etCount.setText(String.valueOf(menus.getQuantity()));

                if (mListener != null) {
                    mListener.onAddToCartClick(menus);
                }
            }
        });

        // Set a TextWatcher on the EditText to capture the user input
        holder.etCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Update the quantity in the Menu object when the user types in the EditText
                // If the input is null or empty, set the quantity to 0
                menus.setQuantity(charSequence.toString().isEmpty() ? 0 : Integer.parseInt(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });




    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public Menu getSelectedItem() {
        if (currentPos >= 0 && mListData != null && currentPos < mListData.size()) {
            return mListData.get(currentPos);
        }
        return null;
    }




}