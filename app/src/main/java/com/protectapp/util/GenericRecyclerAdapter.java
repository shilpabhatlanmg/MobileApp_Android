package com.protectapp.util;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class GenericRecyclerAdapter extends RecyclerView.Adapter<GenericRecyclerAdapter.GenericViewHolder> {

    private int layoutID;
    private GenericItemBinder itemBinder;
    private List objectList;
    public GenericRecyclerAdapter(int layout_id, List objectList, GenericItemBinder item_binder)
    {
        this.layoutID=layout_id;
        this.objectList= objectList;
        this.itemBinder=item_binder;
    }
    public void setData(List objectList)
    {
        this.objectList = objectList;
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GenericViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(layoutID,viewGroup,false));
    }

    @Override
    public int getItemCount() {
        return objectList==null ? 0 : objectList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder genericViewHolder, int i) {
        genericViewHolder.bind();

    }
    public  class GenericViewHolder extends RecyclerView.ViewHolder
    {
        ViewDataBinding binding;
        public GenericViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemBinder!=null)
                        itemBinder.onItemClick(getAdapterPosition());
                }
            });
        }

        public void bind()
        {
            itemBinder.bind(binding,getAdapterPosition());
        }
    }

    public interface GenericItemBinder
    {
        void bind(ViewDataBinding itemBinding,int position);
        void onItemClick(int position);
    }

}
