package com.alsaeedcullivan.ourtrips.holders;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alsaeedcullivan.ourtrips.GalleryActivity;
import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.ViewPictureActivity;
import com.alsaeedcullivan.ourtrips.models.Pic;
import com.alsaeedcullivan.ourtrips.utils.Const;

public class GalleryImageHolder extends RecyclerView.ViewHolder {

    private Pic pic;
    private ImageView image;
    private View parent;
    private int position;

    public GalleryImageHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        image = itemView.findViewById(R.id.gallery_pic);

        // set a listener for when this image is clicked
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext() == null || pic == null || !(v.getContext() instanceof
                        GalleryActivity)) return;
                // send the user to view picture activity with this photo
                GalleryActivity g = (GalleryActivity) v.getContext();
                Intent intent = new Intent(g, ViewPictureActivity.class);
                intent.putExtra(Const.TRIP_ID_TAG, g.getTripId());
                intent.putExtra(Const.PIC_TAG, pic);
                intent.putExtra(Const.POSITION_TAG, position);
                intent.putExtra(Const.GALLERY_TAG, g.getPics());
                g.startActivity(intent);
                // finish the current activity
                g.finish();
            }
        });
    }

    // setters and getters

    public void setPic(Pic pic) {
        this.pic = pic;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ImageView getImage() {
        return image;
    }
}
