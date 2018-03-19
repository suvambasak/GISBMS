package com.example.codebox.gisbms;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by codebox on 7/8/17.
 */

public class JobLocationAdapter extends ArrayAdapter<JobLocation> {
    private static final String LOG_TAG = JobLocationAdapter.class.getSimpleName();

    public JobLocationAdapter(Activity context, ArrayList<JobLocation> jobLocation) {

        super(context, 0, jobLocation);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        JobLocation currentJobLocation = getItem(position);

        TextView jobTitle = (TextView) listItemView.findViewById(R.id.issue_title);
        jobTitle.setText(currentJobLocation.getJobTile());

        TextView latDisplay = (TextView) listItemView.findViewById(R.id.lat_display);
        latDisplay.setText(currentJobLocation.getLat());

        TextView lngDisplay = (TextView) listItemView.findViewById(R.id.lng_display);
        lngDisplay.setText(currentJobLocation.getLng());

        return listItemView;
    }
}
