package uk.ac.coventry.bello.myinventory.inventory;

import android.support.v4.app.Fragment;

/**
 * Created by Freshollie on 25/11/2016.
 *
 * Used so I can add onBackPressed to all my used fragments
 */

public class MyInventoryFragment extends Fragment {
    public boolean onBackPressed(){
        return false;
    }
}
