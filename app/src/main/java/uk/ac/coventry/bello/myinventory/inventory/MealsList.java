package uk.ac.coventry.bello.myinventory.inventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.coventry.bello.myinventory.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Freshollie on 27/11/2016.
 */

public class MealsList extends ArrayList<Meal>{
    private final String TAG = "MealsList";
    private static final MealsList INSTANCE = new MealsList();
    private Inventory mInventory;

    private MealsList() {
        mInventory = Inventory.getInstance();
    }

    public static MealsList getInstance(){
        return INSTANCE;
    }

    /**
     * Makes a string set of json strings representing
     * meals in the MealsList.
     * @return stringSet
     */
    public Set<String> getSaveStringSet(){

        Set<String> stringSet = new HashSet<>();
        for (Meal meal: this) {
            stringSet.add(meal.getJson().toString());
        }
        return stringSet;
    }

    public boolean isMealName(String name){
        for (Meal meal: this){
            if (meal.getName().toLowerCase().equals(name.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public void sort() {
        Collections.sort(this, new Comparator<Meal>() {
            @Override
            public int compare(Meal meal, Meal t1) {
                return meal.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
            }
        });
    }

    public ArrayList<Meal> getListCopy() {
        return new ArrayList<>(this);
    }

    /**
     * Saves the MealList to shared preferences with each meal as
     * a json object string
     * @param context
     */
    public void save(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences(context.getString(R.string.save_key), 0);
        SharedPreferences.Editor editor = mPrefs.edit();


        editor.putStringSet(context.getString(R.string.saved_meals_key), getSaveStringSet());
        editor.commit();
    }

    /**
     * Loads the list from the shared preferences
     * @param context
     */
    public void load(Context context){
        clear(); // Cleared the old list

        SharedPreferences mPrefs = context.getSharedPreferences(context.getString(R.string.save_key), Context.MODE_PRIVATE);

        Set<String> set = mPrefs.getStringSet(context.getString(R.string.saved_meals_key), null);

        if (set != null) {
            for (String stringMeal : set) {
                try {
                    JSONObject jsonMeal = new JSONObject(stringMeal);

                    String name = jsonMeal.getString("name");
                    String category = jsonMeal.getString("category");


                    ArrayList<InventoryItem> ingredients = new ArrayList<>();

                    JSONArray ingredientsList = jsonMeal.getJSONArray("ingredients");

                    for (int i = 0; i < ingredientsList.length(); i++) {

                        InventoryItem ingredient = mInventory.getItemFromName(ingredientsList.getString(i));

                        if (ingredient != null) {
                            ingredients.add(ingredient); // Build the ingredients list
                        }

                    }

                    add(new Meal(name, ingredients, category)); // Add that saved meal to the list


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
