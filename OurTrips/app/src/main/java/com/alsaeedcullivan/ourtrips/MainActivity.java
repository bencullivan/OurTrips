package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alsaeedcullivan.ourtrips.utils.Const;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set title
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
            case R.id.update_dates:
                // proceed to calendar activity
                Intent datesIntent = new Intent(MainActivity.this, CalendarActivity.class);
                datesIntent.putExtra(Const.SOURCE_TAG, Const.MAIN_TAG);
                startActivity(datesIntent);
                break;
            case R.id.friends:
                // proceed to FriendActivity
                Intent friendIntent = new Intent(MainActivity.this, FriendActivity.class);
                friendIntent.putExtra(Const.SOURCE_TAG, Const.MAIN_TAG);
                startActivity(friendIntent);
                break;
            case R.id.settings:
                // proceed to settings activity
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                settingsIntent.putExtra(Const.SOURCE_TAG, Const.MAIN_TAG);
                startActivity(settingsIntent);
                break;
            case R.id.update_profile:
                // proceed to register activity
                Intent editIntent = new Intent(MainActivity.this, RegisterActivity.class);
                editIntent.putExtra(Const.SOURCE_TAG, Const.MAIN_TAG);
                startActivity(editIntent);
                break;
            case R.id.match_dates:
                Intent matchIntent = new Intent(MainActivity.this, MatchActivity.class);
                matchIntent.putExtra(Const.SOURCE_TAG, Const.MAIN_TAG);
                startActivity(matchIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
