package com.alsaeedcullivan.ourtrips.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alsaeedcullivan.ourtrips.R;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoHolder extends RecyclerView.ViewHolder {

    private View mView;
    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;


    public VideoHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }


    public void setVideo(Context c, String title, String url) {
        // set the title of the video
        TextView vidTitle = mView.findViewById(R.id.video_title);
        vidTitle.setText(title);

        // get a reference to the PlayerView
        mPlayerView = mView.findViewById(R.id.exo);

//        try {
//            BandwidthMeter band = new DefaultBandwidthMeter.Builder().build();
//            TrackSelector track = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(band));
//            mExoPlayer = ExoPlayerFactory.newSimpleInstance(c, track);
//        }
    }

}
