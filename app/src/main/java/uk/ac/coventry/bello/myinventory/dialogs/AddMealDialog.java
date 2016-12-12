package uk.ac.coventry.bello.myinventory.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.adapters.MealsAdapter;
import uk.ac.coventry.bello.myinventory.inventory.Inventory;
import uk.ac.coventry.bello.myinventory.inventory.InventoryItem;
import uk.ac.coventry.bello.myinventory.inventory.Meal;
import uk.ac.coventry.bello.myinventory.inventory.MealCategories;
import uk.ac.coventry.bello.myinventory.inventory.MealsList;

/**
 * Created by bello on 28/11/2016.
 */

public class AddMealDialog extends DialogFragment {
    private final String TAG = "AddMealDialog";

    private View mView;
    private MealsAdapter mAdapter;
    private int numIngredientSpinners = 0;

    private ArrayList<InventoryItem> presetIngredients;
    private String presetName;
    private String presetCategory;

    private AppCompatSpinner mLastSpinner;
    private ArrayList<String> ingredientsList;
    private String[] ingredientsNameArray;

    private int positiveButtonTextStringId = R.string.add_button_text;
    private int cancelButtonTextStringId = R.string.cancel_button_text;
    private int titleTextStringId = R.string.title_activity_add_meal;

    private View.OnClickListener positiveButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (saveMeal()) {
                dismiss();
                if (getAdapter() != null) {
                    getAdapter().notifyMealsListChanged();

                }
            }
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mView = inflater.inflate(R.layout.add_meal_dialog_content, null);

        builder.setView(mView)
                // Add action buttons
                .setPositiveButton(positiveButtonTextStringId, null)
                .setNegativeButton(cancelButtonTextStringId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddMealDialog.this.getDialog().cancel();
                    }
                });

        builder.setCancelable(true);

        collectIngredients();

        fillCategoriesSpinner();

        createIngredientsSpinners();

        fillPresetName();

        setTitle(getString(titleTextStringId));

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point

        AlertDialog dialog = (AlertDialog) getDialog();

        if(dialog != null)
        {
            dialog.setCanceledOnTouchOutside(true);

            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(positiveButtonOnClickListener);
        }
    }

    public void collectIngredients() {
        ingredientsList = Inventory.getInstance().getItemNames();
        Collections.sort(ingredientsList);
        ingredientsList.add(0, getString(R.string.input_select_ingredient_text));
        ingredientsNameArray = ingredientsList.toArray(new String[0]);
    }

    public void newIngredientSpinner() {
        numIngredientSpinners ++;
        final int ingredientSpinnerNum = numIngredientSpinners;
        AppCompatSpinner spinner = new AppCompatSpinner(getActivity());


        ArrayAdapter<String> ingredientsAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                ingredientsNameArray
        );

        ingredientsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(ingredientsAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0 && ingredientSpinnerNum == numIngredientSpinners) { // We have selected an ingredient, and the this spinner is the latest spinner
                    newIngredientSpinner(); // Create a new spinner
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        LinearLayout ingredientsSpinnerList = (LinearLayout) mView.findViewById(R.id.meal_ingredients_spinner_list);
        ingredientsSpinnerList.addView(spinner);
        mLastSpinner = spinner;

    }

    public void createIngredientsSpinners() {
        newIngredientSpinner(); // We always make 1

        if (presetIngredients != null) { // We have data for the
            for (InventoryItem ingredient : presetIngredients) {
                mLastSpinner.setSelection(ingredientsList.indexOf(ingredient.getName()));
                newIngredientSpinner();
            }

        } else {
            for (int i = 0; i < 0; i++) {
                newIngredientSpinner();
            }
        }
    }

    public void fillCategoriesSpinner() {
        AppCompatSpinner categorySpinner = (AppCompatSpinner) mView.findViewById(R.id.input_category_spinner);
        MealCategories.getInstance().load(getContext());
        ArrayList<String> categoriesSpinnerList = new ArrayList<>(MealCategories.getInstance());

        Collections.sort(categoriesSpinnerList);

        categoriesSpinnerList.add(0, getString(R.string.input_select_category_text));
        categoriesSpinnerList.add(1, getString(R.string.input_add_category_text));

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                categoriesSpinnerList.toArray(new String[0])
        );


        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 1){
                    mView.findViewById(R.id.meal_category_input).setVisibility(View.VISIBLE);
                } else {
                    mView.findViewById(R.id.meal_category_input).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(categoryAdapter);

        if (presetCategory != null) {
            categorySpinner.setSelection(categoriesSpinnerList.indexOf(presetCategory));
        }
    }

    public void setTitle(String title) {
        ((TextView) mView.findViewById(R.id.add_meal_title_text)).setText(title);
    }

    public void setTitleTextStringId(int id) {
        titleTextStringId = id;
    }

    public void fillPresetName() {
        if (presetName != null) {
            ((EditText) mView.findViewById(R.id.meal_name_input)).setText(presetName);
        }
    }

    public void setPositiveButtonTextStringId(int id) {
        positiveButtonTextStringId = id;
    }

    public void setCancelButtonTextStringId(int id) {
        cancelButtonTextStringId = id;
    }

    public void setPresetIngredients(ArrayList<InventoryItem> ingredients) {
        presetIngredients = ingredients;
    }

    public void setPresetName(String name){
        presetName = name;
    }

    public void setPresetCategory(String category) {
        presetCategory = category;
    }

    public void setPositiveButtonOnClickListener(View.OnClickListener onClickListener) {
        positiveButtonOnClickListener = onClickListener;
    }

    public void setAdapter(MealsAdapter adapter) {
        mAdapter = adapter;
    }

    public MealsAdapter getAdapter() {
        return mAdapter;
    }

    public String getMealName() {
        return ((EditText) mView.findViewById(R.id.meal_name_input)).getText().toString();
    }

    public String getCategory() {
        String category = (String) ((AppCompatSpinner) mView.findViewById(R.id.input_category_spinner)).getSelectedItem();
        if (category.equals(getString(R.string.input_add_category_text))) {
            category = ((EditText) mView.findViewById(R.id.meal_category_input)).getText().toString();
        } else if (category.equals(getString(R.string.input_select_category_text))) {
            category = null;
        }

        return category;
    }

    public ArrayList<InventoryItem> getSelectedIngredients() {

        Inventory inventory = Inventory.getInstance();

        ArrayList<InventoryItem> ingredientsList = new ArrayList<>();

        LinearLayout spinnerList = (LinearLayout) mView.findViewById(R.id.meal_ingredients_spinner_list);

        for (int index = 0; index < spinnerList.getChildCount(); index++) {
            String itemName = (String) ((AppCompatSpinner) spinnerList.getChildAt(index)).getSelectedItem();

            if (!itemName.equals(getString(R.string.input_select_ingredient_text))) {
                ingredientsList.add(inventory.getItemFromName(itemName));
            }
        }

        return ingredientsList;
    }

    public boolean validate(String name, String category, ArrayList<InventoryItem> ingredients) {
        String alertMessage = "";
        if (name.isEmpty()) {
            alertMessage = getString(R.string.not_valid_name);
        } else if (MealsList.getInstance().isMealName(name) && !presetName.equals(name)) {
            alertMessage = getString(R.string.name_already_exist);
        } else if (ingredients.isEmpty()) {
            alertMessage = getString(R.string.no_ingredients_message);
        } else if (category == null) {
            alertMessage = getString(R.string.no_category_message);
        }

        if (!alertMessage.isEmpty()) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(alertMessage)
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.ok), null)
                    .show();
            return false;
        }

        return true;
    }

    public void handleCategory(String category) {
        MealCategories mealCategories = MealCategories.getInstance();
        if (!mealCategories.contains(category)) {
            mealCategories.add(category);
        }
        mealCategories.save(getContext());
    }

    private boolean saveMeal() {
        MealsList mealsList = MealsList.getInstance();

        ArrayList<InventoryItem> ingredients = getSelectedIngredients();
        String category = MealCategories.getInstance().getValidCategory(getCategory());
        String name = getMealName();

        if (validate(name, category, ingredients)) {
            Meal meal = new Meal(name, ingredients, category);
            mealsList.add(meal);
            mealsList.save(getContext());

            handleCategory(category);

            return true;
        }
        return false;
    }


}