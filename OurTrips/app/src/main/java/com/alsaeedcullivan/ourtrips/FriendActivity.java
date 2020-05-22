package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alsaeedcullivan.ourtrips.adapters.FriendAdapter;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.fragments.CustomDialogFragment;
import com.alsaeedcullivan.ourtrips.models.User;
import com.alsaeedcullivan.ourtrips.models.UserSummary;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    private static final String LIST_KEY = "requests";
    private static final String NAME_KEY = "name";

    private FirebaseUser mUser;
    private FriendAdapter mAdapter;
    private ArrayList<UserSummary> mList;
    private ListView mListView;
    private int selectedIndex;
    private UserSummary selectedFriend;
    private String mName = "";
    private LinearLayout mFriendLayout;
    private ProgressBar mSpinner;
    private TextView mLoadingText;
    private TextView mMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        // set title and back button
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_activity_findFriends));

        // get the user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // get a reference to the widgets
        mListView = findViewById(R.id.request_list);
        mSpinner = findViewById(R.id.friend_spinner);
        mFriendLayout = findViewById(R.id.friend_linear);
        mLoadingText = findViewById(R.id.friend_loading_text);
        mMessage = findViewById(R.id.friend_text);

        // set initial visibility
        mFriendLayout.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoadingText.setVisibility(View.VISIBLE);

        // if there is a list in savedInstanceState, there is no need to load from the db
        if (savedInstanceState != null && savedInstanceState.getStringArrayList(LIST_KEY) != null) {
            mList = savedInstanceState.getParcelableArrayList(LIST_KEY);
            mName = savedInstanceState.getString(NAME_KEY);
            if (mName == null) mName = "";
            if (mList != null) {
                // set the adapter to the list view with the saved list
                mAdapter = new FriendAdapter(this, R.layout.activity_friend, new ArrayList<UserSummary>());
                mAdapter.addAll(mList);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(listListener());
                makeListAppear();
                if (mList.size() == 0) mMessage.setText(R.string.no_req);
                else mMessage.setText(R.string.pending_requests);
            }
        } else if (mUser != null) {
            // instantiate the adapter
            mAdapter = new FriendAdapter(this, R.layout.activity_friend, new ArrayList<UserSummary>());
            // set the adapter to the ListView
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(listListener());
            // get all the requests
            Task<List<UserSummary>> emailTask = AccessDB.getFriendRequests(mUser.getUid());
            emailTask.addOnCompleteListener(new OnCompleteListener<List<UserSummary>>() {
                @Override
                public void onComplete(@NonNull Task<List<UserSummary>> task) {
                    if (task.isSuccessful()) {
                        mList = (ArrayList<UserSummary>) task.getResult();
                        Log.d(Const.TAG, "onComplete: " + mList);
                        // add them to an adapter
                        if (mList != null) {
                            mAdapter.addAll(mList);
                            mAdapter.notifyDataSetChanged();
                            if (mList.size() == 0) mMessage.setText(R.string.no_req);
                            else mMessage.setText(R.string.pending_requests);
                        }
                    }
                }
            });
            // get the name of this user
            Task<String> nameTask = AccessDB.getUserName(mUser.getUid());
            nameTask.addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) mName = task.getResult();
                    else mName = "";
                    makeListAppear();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.send_request_button) {
            requestDialog();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mList != null) outState.putParcelableArrayList(LIST_KEY, mList);
        if (mName != null) outState.putString(NAME_KEY, mName);
    }

    /**
     * requestDialog()
     * display a dialog that allows the user to send a friend request
     */
    private void requestDialog() {
        CustomDialogFragment.newInstance(CustomDialogFragment.FRIEND_ID)
                .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
    }

    /**
     * sendRequest()
     * called when a user presses "send"
     *
     * @param email the email of the person they are sending the request to
     */
    public void sendRequest(String email) {
        // get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        Log.d(Const.TAG, "sendRequest: " + email);
        Log.d(Const.TAG, "sendRequest: " + user.getEmail());
        // if there is a user, send the friend request
        AccessDB.sendFriendRequest(user.getUid(), user.getEmail(), mName, email);
    }

    /**
     * acceptRequest()
     * called when a user accepts a friend request
     *
     * @param friendId the email of the new friend
     */
    public void acceptRequest(String friendId, String friendEmail, String friendName) {
        // get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        Log.d(Const.TAG, "acceptRequest: " + friendId);
        Log.d(Const.TAG, "acceptRequest: " + mName);
        if (mName == null) {
            return;
        }
        // if there is a user, accept the friend requests
        AccessDB.acceptFriendRequest(user.getUid(), user.getEmail(), mName, friendId, friendEmail, friendName);
    }

    /**
     * declineRequest()
     * declines a friend request
     * @param id the id of the person that sent the request
     */
    public void declineRequest(String id) {
        // get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        Log.d(Const.TAG, "declineRequest: " + id);
        AccessDB.deleteRequest(user.getUid(), id);
    }

    /**
     * removeRequest()
     * removes a request from the list view
     *
     * @param position the position of the request that will be removed
     */
    public void removeRequest(int position) {
        mList.remove(position);
        mAdapter.clear();
        mAdapter.addAll(mList);
        mAdapter.notifyDataSetChanged();
    }

    // returns an OnClickListener for the list view
    private AdapterView.OnItemClickListener listListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // save the data and display the dialog
                selectedIndex = position;
                selectedFriend = mList.get(position);
                CustomDialogFragment.newInstance(CustomDialogFragment.ACCEPT_ID)
                        .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
            }
        };
    }

    // makes the list view visible
    private void makeListAppear() {
        mSpinner.setVisibility(View.GONE);
        mLoadingText.setVisibility(View.GONE);
        mFriendLayout.setVisibility(View.VISIBLE);
    }

    // GETTERS

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public UserSummary getSelectedFriend() {
        return selectedFriend;
    }
}