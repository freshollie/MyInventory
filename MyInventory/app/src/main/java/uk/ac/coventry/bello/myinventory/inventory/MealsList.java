package uk.ac.coventry.bello.myinventory.inventory;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.coventry.bello.myinventory.R;

/**
 * Created by Freshollie on 27/11/2016.
 */

public class MealsList extends ArrayList<Meal>{
    private static final MealsList INSTANCE = new MealsList();
    private Inventory mInventory;

    private MealsList() {
        mInventory = Inventory.getInstance();
    }

    public static MealsList getInstance(){
        return INSTANCE;
    }

    public Set<String> getSaveStringSet(){

        Set<String> stringSet = new HashSet<>();
        for (Meal meal: this) {
            stringSet.add(meal.getJson().toString());
        }
        return stringSet;
    }

    public void save(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences(context.getString(R.string.save_key), 0);
        SharedPreferences.Editor editor = mPrefs.edit();


        editor.putStringSet(context.getString(R.string.saved_meals_key), getSaveStringSet());
        editor.commit();
    }

    public void load(Context context){
        clear();

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

                        InventoryItem ingredient = mInventory.getItemByName(ingredientsList.getString(i));

                        if (ingredient != null) {
                            ingredients.add(ingredient);
                        }

                    }

                    add(new Meal(name, ingredients, category));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
