package uk.ac.coventry.bello.myinventory.fragments;

import android.view.View;

import java.util.ArrayList;

import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.inventory.InventoryItem;
import uk.ac.coventry.bello.myinventory.inventory.Meal;
import uk.ac.coventry.bello.myinventory.inventory.MealCategories;
import uk.ac.coventry.bello.myinventory.inventory.MealsList;

/**
 * Created by Freshollie on 03/12/2016.
 */

public class UpdateMealFragment extends AddMealFragment {
    private Meal editMeal;

    public UpdateMealFragment() {

        setPositiveButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (updateMealAttributes()) {
                    dismiss();
                    if (getAdapter() != null) {
                        getAdapter().notifyDataSetChanged();
                    }
                }
            }
        });

        setPositiveButtonTextStringId(R.string.update_button_text);
        setTitleTextStringId(R.string.title_activity_update_meal);
    }

    public void setEditMeal(Meal meal) {
        editMeal = meal;

        setPresetName(meal.getName());
        setPresetCategory(meal.getCategory());
        setPresetIngredients(meal.getIngredients());
    }

    private boolean updateMealAttributes() {
        ArrayList<InventoryItem> ingredients = getSelectedIngredients();
        String category = MealCategories.getInstance().getValidCategory(getCategory());
        String name = getMealName();

        if (validate(name, category, ingredients)) {
            editMeal.setCategory(category);
            editMeal.setIngredients(ingredients);
            editMeal.setName(name);

            handleCategory(category);

            MealsList.getInstance().save(getContext());
            return true;
        }
        return false;
    }

}
