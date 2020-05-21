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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FriendAdapter extends ArrayAdapter<UserSummary> {

    private ArrayList<UserSummary> items;
    private Context context;


    public FriendAdapter(@NonNull Context context, int resource, ArrayList<UserSummary> items) {
        super(context, resource, items);
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
            convertView = Objects.requireNonNull(inflater).inflate(R.layout.friend_item, parent, false);
        }

        // get reference to TextViews
        TextView name = convertView.findViewById(R.id.friend_name);
        TextView email = convertView.findViewById(R.id.friend_email);

        // fill TextView with appropriate data
        UserSummary user = items.get(position);
        // friend name
        name.setText(user.getName());
        // friend email
        email.setText(user.getEmail());

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
