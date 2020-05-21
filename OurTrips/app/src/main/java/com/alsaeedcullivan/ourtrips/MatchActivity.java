package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.models.UserSummary;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class MatchActivity extends AppCompatActivity {

    List<Date> mUserDates;
    List<UserSummary> mFriends;
    FirebaseUser mUser;
    ListView mListView;
    UserSummary mSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        setTitle("Match Dates");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // get a reference to the ListView
        mListView = findViewById(R.id.match_list);

        if (mUser != null) {
            // load this user's list of friends
            AccessDB.getFriendsList(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<List<UserSummary>>() {
                @Override
                public void onComplete(@NonNull Task<List<UserSummary>> task) {
                    if (task.isSuccessful()) {
                        mFriends = task.getResult();
                    } else {
                        // TODO:
                        // could not load
                    }
                }
            });
            // load this user's dates from the db
            AccessDB.getUserDatesForCal(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<List<Date>>() {
                @Override
                public void onComplete(@NonNull Task<List<Date>> task) {
                    if (task.isSuccessful()) {
                        mUserDates = task.getResult();
                    } else {
                        // TODO:
                        // could not load dates
                    }
                }
            });
        }
    }

    /**
     * onMatchClicked()
     * called from a dialog when a user clicks "match"
     */
    public void onMatchClicked() {
        if (mSelected == null) return;
        match(mUserDates, mSelected.getUserId());
    }

    /**
     * match()
     * called when a user selects one of their friends and clicks "match"
     * @param userDates the dates that this user is available
     * @param friendId the id of the friend that they selected
     */
    private void match(List<Date> userDates, String friendId) {
        // perform a match to see which dates you are both available
        AccessDB.matchDates(userDates, friendId).addOnCompleteListener(new OnCompleteListener<long[]>() {
            @Override
            public void onComplete(@NonNull Task<long[]> task) {
                if (task.isSuccessful()) {
                    long[] matched = task.getResult();
                    if (matched != null && matched.length > 0) {
                        // start CalendarActivity and pass it an array of all the matched dates
                        Intent intent = new Intent(MatchActivity.this, CalendarActivity.class);
                        intent.putExtra(Const.SOURCE_TAG, Const.MATCH_TAG);
                        intent.putExtra(Const.MATCH_ARR_TAG, matched);
                        startActivity(intent);
                    } else {
                        // the two users have no dates in common
                        Toast t = Toast.makeText(MatchActivity.this, "You and " +
                                mSelected.getName() + " have no dates in common.", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                } else {
                    // unable to be matched
                    Toast t = Toast.makeText(MatchActivity.this, "You and " +
                            mSelected.getName() + " were not able to be matched.", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                    t.show();
                }
            }
        });
    }

    // getters

    public UserSummary getFriend() {
        return mSelected;
    }
}
