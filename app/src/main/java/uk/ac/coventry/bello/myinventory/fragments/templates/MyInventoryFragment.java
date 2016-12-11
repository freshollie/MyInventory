package uk.ac.coventry.bello.myinventory.fragments.templates;

import android.support.v4.app.Fragment;

/**
 * Created by Freshollie on 25/11/2016.
 *
 * Used so I can add onBackPressed to all my used fragments
 */

public class MyInventoryFragment extends Fragment {
    public boolean onBackPressed(){ //Just needed a method of my fragments knowing when the back button was pressed
        return false;
    }
}
