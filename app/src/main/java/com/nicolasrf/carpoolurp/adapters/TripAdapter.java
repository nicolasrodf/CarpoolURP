package com.nicolasrf.carpoolurp.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nicolasrf.carpoolurp.R;
import com.nicolasrf.carpoolurp.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends ArrayAdapter<Trip> {

    private ArrayList<Trip> tripList;
    private Context context;
    private int layoutId;

    public TripAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Trip> trips) {
        super(context, resource, trips);

        this.context = context;
        this.layoutId = resource;
        tripList = new ArrayList<>(trips);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(layoutId, null);

            holder = new ViewHolder();
            holder.addressTextView = (TextView) convertView.findViewById(R.id.address);
            holder.dateTextView = (TextView) convertView.findViewById(R.id.date_string);
            convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }

            Trip trip = tripList.get(position);
            holder.addressTextView.setText(String.valueOf(trip.getAddress()));
            holder.dateTextView.setText(String.valueOf(trip.getDateString()));

        return convertView;

    }

    public class ViewHolder {

        public TextView addressTextView;
        public TextView dateTextView;

    }

}