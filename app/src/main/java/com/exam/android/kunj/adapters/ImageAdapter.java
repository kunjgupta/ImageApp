package com.exam.android.kunj.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.exam.android.kunj.R;
import com.exam.android.kunj.db.models.ImageModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<ImageModel> mImageModelList;

    // Constructor
    public ImageAdapter(Context context, List<ImageModel> imageModelList) {
        mContext = context;
        mImageModelList = imageModelList;
    }

    public int getCount() {
        return mImageModelList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.photo_gallery_adapter, parent, false);
        } else {
            view = convertView;
        }

        ImageModel imageModel = mImageModelList.get(position);

        imageView = view.findViewById(R.id.image);
        if (imageModel.getType() == ImageModel.LOCAL_FILE) {
            Picasso.get()
                    .load(new File(imageModel.getPath()))
                    .placeholder(R.drawable.image_not_available)
                    .fit()
                    .centerCrop()
                    .into(imageView);
        } else {
            Picasso.get()
                    .load(imageModel.getPath())
                    .placeholder(R.drawable.image_not_available)
                    .fit()
                    .centerCrop()
                    .into(imageView);
        }
        return view;
    }
}
