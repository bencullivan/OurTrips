package com.alsaeedcullivan.ourtrips.adapters;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoHolder extends RecyclerView.ViewHolder {
    
    private View mView;
    private SimpleExoPlayer mExoPlayer;
    private PlayerView mExoPlayerView;


    public VideoHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }


    public void setVideo(final Application a, String title, String url) {

    }

}
