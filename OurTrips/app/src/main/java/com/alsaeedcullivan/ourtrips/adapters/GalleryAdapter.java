package com.alsaeedcullivan.ourtrips.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alsaeedcullivan.ourtrips.GalleryActivity;
import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.ViewPictureActivity;
import com.alsaeedcullivan.ourtrips.glide.GlideApp;
import com.alsaeedcullivan.ourtrips.models.Pic;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryHolder> {

    private ArrayList<Pic> mPictures;
    private Context mContext;

    // public constructor
    public GalleryAdapter(Context context, ArrayList<Pic> pictures) {
        mPictures = pictures;
        mContext = context;
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.GalleryHolder holder, int position) {
        Log.d(Const.TAG, "onBindViewHolder: " + mPictures.get(position).getPicPath());
        Log.d(Const.TAG, "onBindViewHolder: " + holder.image);
        // load this picture into the appropriate ImageView
        holder.pic = mPictures.get(position);
        GlideApp.with(mContext)
                .load(FirebaseStorage.getInstance().getReference(mPictures.get(position).getPicPath()))
                .into(holder.image);

//        // set an onClickListener for this picture
//        holder.parent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mPictures.size();
    }

    static class GalleryHolder extends RecyclerView.ViewHolder {

        Pic pic;
        ImageView image;
        View parent;

        GalleryHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView;
            image = itemView.findViewById(R.id.gallery_pic);
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getContext() == null || pic == null || !(v.getContext() instanceof
                            GalleryActivity)) return;
                    Intent intent = new Intent(v.getContext(), ViewPictureActivity.class);
                    intent.putExtra(Const.TRIP_ID_TAG, ((GalleryActivity)v.getContext()).getTripId());
                    intent.putExtra(Const.PIC_TAG, pic);
                    v.getContext().startActivity(intent);
                    Log.d(Const.TAG, "onClick: " + v.getContext());
                }
            });
        }
    }
}
