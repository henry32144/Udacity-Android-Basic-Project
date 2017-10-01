package com.henry.android.tourguideapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InSchoolFragment extends Fragment {


    public InSchoolFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spot_list, container, false);

        final ArrayList<Spot> spots = new ArrayList<Spot>();

        spots.add(new Spot(getString(R.string.category_school), "","", R.drawable.school_label));
        spots.add(new Spot(getString(R.string.school_church), getString(R.string.type_attraction),getString(R.string.school_church_info), R.drawable.school_church));
        spots.add(new Spot(getString(R.string.school_bell), getString(R.string.type_attraction),getString(R.string.school_bell_info), R.drawable.school_bell));
        spots.add(new Spot(getString(R.string.school_lake), getString(R.string.type_attraction),getString(R.string.school_lake_info), R.drawable.school_lake));
        spots.add(new Spot(getString(R.string.school_milkshop), getString(R.string.type_food),getString(R.string.school_milkshop_info), R.drawable.school_milkshop));
        spots.add(new Spot(getString(R.string.school_ranch), getString(R.string.type_attraction),getString(R.string.school_ranch_info), R.drawable.school_ranch));
        spots.add(new Spot(getString(R.string.school_road), getString(R.string.type_attraction),getString(R.string.school_road_info), R.drawable.school_road));

        // Create an {@link WordAdapter}, whose data source is a list of {@link Word}s. The
        // adapter knows how to create list items for each item in the list.
        SpotAdapter adapter = new SpotAdapter(getActivity(), spots);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // spot_list.xml layout file.
        ListView listView = (ListView) rootView.findViewById(R.id.list);

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get the {@link Word} object at the given position the user clicked on
                Spot spot = spots.get(position);
                int image = spot.getImageResource();
                String info = spot.getSpotInfo();
                //Check if spot item is not label
                if (spot.getSpotType().toString() != "") {
                    Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                    //Create extras to pass data
                    Bundle extras = new Bundle();
                    extras.putString("name", spot.getSpotName());
                    extras.putInt("image", image);
                    extras.putString("info", info);
                    detailIntent.putExtras(extras);
                    startActivity(detailIntent);
                }
            }
        });
        return rootView;
    }

}
