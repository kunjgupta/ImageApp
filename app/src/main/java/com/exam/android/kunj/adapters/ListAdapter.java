package com.exam.android.kunj.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exam.android.kunj.R;

import com.exam.android.kunj.db.models.ImageModel;
import com.exam.android.kunj.ui.PhotoGalleryScreen;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    private List<ImageModel> mModelList = new ArrayList<>(0);
    private PhotoGalleryScreen.OnItemClickListener listener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTitleTextView;
        TextView mDesTextView;
        ImageView mImageView;

        MyViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.list_adapter_image_view);
            mTitleTextView = (TextView) v.findViewById(R.id.list_adapter_title_text_view);
            mDesTextView = (TextView) v.findViewById(R.id.list_adapter_des_text_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapter(PhotoGalleryScreen.OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setListItem(List<ImageModel> imageModelList) {
        if (!mModelList.isEmpty())
            mModelList.clear();
        mModelList.addAll(imageModelList);
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        MyViewHolder vh;
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_adapter, parent, false);
        vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final ImageModel imageModel = mModelList.get(position);
        holder.mTitleTextView.setText(imageModel.getTitle());
        holder.mDesTextView.setText(imageModel.getDescription());
        if (imageModel.getType() == ImageModel.LOCAL_FILE) {
            Picasso.get()
                    .load(new File(imageModel.getPath()))
                    .placeholder(R.drawable.image_not_available)
                    .fit()
                    .centerCrop()
                    .into(holder.mImageView);
        } else {
            Picasso.get()
                    .load(imageModel.getPath())
                    .placeholder(R.drawable.image_not_available)
                    .fit()
                    .centerCrop()
                    .into(holder.mImageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(imageModel.getId());
            }
        });
    }

    @Override public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
        Picasso.get().cancelRequest(holder.mImageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
