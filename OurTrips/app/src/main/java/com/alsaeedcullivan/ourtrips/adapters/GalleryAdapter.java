package com.alsaeedcullivan.ourtrips.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.glide.GlideApp;
import com.alsaeedcullivan.ourtrips.holders.GalleryImageHolder;
import com.alsaeedcullivan.ourtrips.models.Pic;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryImageHolder> {

    private ArrayList<Pic> mPictures;
    private Context mContext;

    // public constructor
    public GalleryAdapter(Context context, ArrayList<Pic> pictures) {
        mPictures = pictures;
        mContext = context;
    }

    @NonNull
    @Override
    public GalleryImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryImageHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryImageHolder holder, int position) {
        Log.d(Const.TAG, "onBindViewHolder: " + mPictures.get(position).getPicPath());
        Log.d(Const.TAG, "onBindViewHolder: " + holder.getImage());
        // update this holder's data
        holder.setPic(mPictures.get(position));
        holder.setPosition(position);
        // load this picture into the appropriate ImageView
        GlideApp.with(mContext)
                .load(FirebaseStorage.getInstance().getReference(mPictures.get(position).getPicPath()))
                .into(holder.getImage());
    }

    @Override
    public int getItemCount() {
        return mPictures.size();
    }
}
