package com.alsaeedcullivan.ourtrips.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.models.UserSummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FriendAdapter extends ArrayAdapter<UserSummary> {

    private ArrayList<UserSummary> items;
    private Context context;


    public FriendAdapter(@NonNull Context context, int resource, ArrayList<UserSummary> items) {
        super(context, resource);
        this.context = context;
        this.items = items;
    }

    // getters //

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(inflater).inflate(R.layout.activity_friend, parent, false);
        }

        // get reference to TextViews
        TextView requests = convertView.findViewById(R.id.request_list);

        // fill TextView with appropriate data
        UserSummary user = items.get(position);
        // friend name

        // friend email


        // return view
        return convertView;
//        return super.getView(position, convertView, parent);

    }

    // adders //


    @Override
    public void add(@Nullable UserSummary object) {
        super.add(object);
        items.add(object);
    }

    @Override
    public void addAll(UserSummary... items) {
        super.addAll(items);
        this.items.addAll(Arrays.asList(items));
    }

}
