package uk.ac.coventry.bello.myinventory.inventory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Freshollie on 24/11/2016.
 */

public class Meal {
    private ArrayList<InventoryItem> ingredients;
    private String category;
    private String name;

    public Meal(String mealName, ArrayList<InventoryItem> mealIngredients, String mealCategory){
        ingredients = mealIngredients;
        category = mealCategory;
        name = mealName;
    }

    public String getCategory(){
        return category;
    }

    public Double getPrice(){
        double price = 0;

        for(InventoryItem item: ingredients){
            price += item.getPrice();
        }

        return price;
    }

    public ArrayList<InventoryItem> getIngredients(){
        return ingredients;
    }

    public String getName(){
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setIngredients(ArrayList<InventoryItem> newIngredients) {
        ingredients = newIngredients;
    }

    public void setCategory(String newCategory) {
        category = newCategory;
    }

    /**
     *
     * @return JSONObject containing all the information to build
     *         this object
     */
    public JSONObject getJson(){
        JSONObject jsonMeal = new JSONObject();
        JSONArray mealsArray = new JSONArray();

        for (InventoryItem item: ingredients){
            mealsArray.put(item.getName());
        }

        try{
            jsonMeal.put("name", getName());
            jsonMeal.put("category", getCategory());
            jsonMeal.put("ingredients", mealsArray);

        } catch(JSONException e){
            e.printStackTrace();
        }

        return jsonMeal;
    }
}
