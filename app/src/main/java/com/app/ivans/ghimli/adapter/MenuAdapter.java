package com.app.ivans.ghimli.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ivans.ghimli.R;
import com.app.ivans.ghimli.model.Menu;
import com.app.ivans.ghimli.net.API;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private Context mContext;
    private ArrayList<Menu> mMenus;
    private MenuAdapter.onItemClickListener mClickedLayer;
    private MenuAdapter.onItemClickListener mClickedCutter;
    private MenuAdapter.onItemClickListener mClickedBundle;
    private MenuAdapter.onItemClickListener mClickedStockIn;
    private MenuAdapter.onItemClickListener mClickedStockOut;
    private MenuAdapter.onItemClickListener mClickedAbout;

    public interface onItemClickListener {
        void onClick(View view, int position, Menu menu);
    }

    public MenuAdapter(Context mContext, ArrayList<Menu> mMenus, onItemClickListener mClickedLayer, onItemClickListener mClickedCutter, onItemClickListener mClickedBundle, onItemClickListener mClickedStockIn, onItemClickListener mClickedStockOut, onItemClickListener mClickedAbout) {
        this.mContext = mContext;
        this.mMenus = mMenus;
        this.mClickedLayer = mClickedLayer;
        this.mClickedCutter = mClickedCutter;
        this.mClickedBundle = mClickedBundle;
        this.mClickedStockIn = mClickedStockIn;
        this.mClickedStockOut = mClickedStockOut;
        this.mClickedAbout = mClickedAbout;
    }

    @NonNull
    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.MenuViewHolder holder, int position) {
        Menu menu = mMenus.get(position);

        holder.tvName.setText(menu.getName());

        Glide.with(mContext)
                .load(menu.getImg())
                .into(holder.ivImg);

        if (position == 0) {
            holder.btnCutter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (API.currentUser(mContext).getRole().getName().equals("bundle")){
                        Toast.makeText(mContext, "You are not allowed to access this menu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mClickedLayer.onClick(v, holder.getAdapterPosition(), menu);
                }
            });
        } else if (position == 1) {
            holder.btnCutter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (API.currentUser(mContext).getRole().getName().equals("bundle")){
                        Toast.makeText(mContext, "You are not allowed to access this menu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mClickedCutter.onClick(v, holder.getAdapterPosition(), menu);
                }
            });
        } else if (position == 2) {
            holder.btnCutter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (API.currentUser(mContext).getRole().getName().equals("cutter")){
                        Toast.makeText(mContext, "You are not allowed to access this menu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mClickedBundle.onClick(v, holder.getAdapterPosition(), menu);
                }
            });
        } else if (position == 3) {
            holder.btnCutter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (API.currentUser(mContext).getRole().getName().equals("cutter")){
                        Toast.makeText(mContext, "You are not allowed to access this menu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mClickedStockIn.onClick(v, holder.getAdapterPosition(), menu);
                }
            });
        } else if (position == 4) {
            holder.btnCutter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (API.currentUser(mContext).getRole().getName().equals("cutter")){
                        Toast.makeText(mContext, "You are not allowed to access this menu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mClickedStockOut.onClick(v, holder.getAdapterPosition(), menu);
                }
            });
        } else if (position == 5) {
            holder.btnCutter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickedAbout.onClick(v, holder.getAdapterPosition(), menu);
                }
            });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.menu_not_found), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return mMenus.size() == 0 ? 0 : mMenus.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView ivImg;
        private CardView btnCutter;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            ivImg = itemView.findViewById(R.id.iv_img);
            btnCutter = itemView.findViewById(R.id.btn_cutter);
        }
    }
}
