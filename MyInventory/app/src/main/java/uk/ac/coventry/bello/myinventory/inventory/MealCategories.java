package uk.ac.coventry.bello.myinventory.inventory;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import uk.ac.coventry.bello.myinventory.R;

/**
 * Created by Freshollie on 27/11/2016.
 */

public class MealCategories extends ArrayList<String> {

    public MealCategories(){
        clear();
    }

    public Set<String> getSaveStringSet(){

        Set<String> stringSet = new HashSet<>();
        for (String category: this) {
            stringSet.add(category);
        }
        return stringSet;
    }

    public void save(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences(context.getString(R.string.save_key), 0);
        SharedPreferences.Editor editor = mPrefs.edit();

        editor.putStringSet(context.getString(R.string.saved_categories_key), getSaveStringSet());
        editor.apply();
    }

    public void load(Context context) {
        clear();

        SharedPreferences mPrefs = context.getSharedPreferences(context.getString(R.string.save_key), Context.MODE_PRIVATE);

        Set<String> categories = mPrefs.getStringSet(context.getString(R.string.saved_categories_key), null);

        if (categories != null) { // We have some saved categories
            for (String category : categories) {
                add(category);
            }
        }

    }

}
