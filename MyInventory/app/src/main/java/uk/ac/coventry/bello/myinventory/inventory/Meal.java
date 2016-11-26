package uk.ac.coventry.bello.myinventory.inventory;

import java.util.ArrayList;

/**
 * Created by Freshollie on 24/11/2016.
 */

public class Meal {
    private ArrayList<InventoryItem> mIngredients;

    public Meal(ArrayList<InventoryItem> mealIngredients){
        mIngredients = mealIngredients;
    }

    public Double getMealPrice(){

        return 0.0;
    }
}
