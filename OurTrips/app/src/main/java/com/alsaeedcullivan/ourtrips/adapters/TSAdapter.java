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
import com.alsaeedcullivan.ourtrips.models.TripSummary;
import com.alsaeedcullivan.ourtrips.models.UserSummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TSAdapter extends ArrayAdapter<TripSummary> {

    private ArrayList<TripSummary> items;
    private Context context;

    public TSAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TripSummary> objects) {
        super(context, resource, objects);
        this.context = context;
        this.items = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(inflater).inflate(R.layout.trip_summary_item, parent, false);
        }

        // get reference to TextViews
        TextView title = convertView.findViewById(R.id.trip_title);
        TextView date = convertView.findViewById(R.id.trip_date);

        // fill TextView with appropriate data
        TripSummary trip = items.get(position);
        // trip title
        title.setText(trip.getTitle());
        // trip date
        date.setText(trip.getDate());

        return convertView;
    }

    @Override
    public void add(@Nullable TripSummary object) {
        super.add(object);
        items.add(object);
    }

    @Override
    public void addAll(TripSummary... items) {
        super.addAll(items);
        this.items.addAll(Arrays.asList(items));
    }

    @Override
    public void clear() {
        super.clear();
        items.clear();
    }
}
