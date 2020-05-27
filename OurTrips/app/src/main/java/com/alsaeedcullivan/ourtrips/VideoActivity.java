package com.alsaeedcullivan.ourtrips;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        final VideoView vid = findViewById(R.id.video_view);
        MediaController m = new MediaController(this);
        m.setAnchorView(vid);
        vid.setMediaController(m);
        FirebaseStorage.getInstance().getReference("9B2BAAC8-B550-42E5-8B21-DE5AD86314F0.mov").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        vid.setVideoURI(uri);
                        vid.seekTo(1);
                        //vid.start();
                    }
                });
    }
}
