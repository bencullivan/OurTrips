package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alsaeedcullivan.ourtrips.utils.Const;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set-up title
        setTitle(R.string.title_activity_main);
    }

    // handle menu //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                // proceed to settings activity
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                settingsIntent.putExtra(Const.SOURCE_TAG, TAG);
                startActivity(settingsIntent);
                break;
            case R.id.update_profile:
                // proceed to register activity
                Intent EditIntent = new Intent(MainActivity.this, RegisterActivity.class);
                EditIntent.putExtra(Const.SOURCE_TAG, TAG);
                startActivity(EditIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
