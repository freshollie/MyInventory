package uk.ac.coventry.bello.myinventory.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.inventory.Inventory;
import uk.ac.coventry.bello.myinventory.inventory.MealCategories;
import uk.ac.coventry.bello.myinventory.inventory.MealsList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        setUpButtonListeners();
    }

    public void resetInventory() {
        Inventory inventory = Inventory.getInstance();
        inventory.resetInventory();
        inventory.save(getActivity());
    }

    public void resetCategories() {
        MealCategories categories = MealCategories.getInstance();
        categories.clear();
        categories.save(getActivity());

    }

    public void resetMeals() {
        MealsList meals = MealsList.getInstance();
        meals.clear();
        meals.save(getActivity());
    }

    public void resetAll() {
        resetInventory();
        resetCategories();
        resetMeals();
    }

    public void confirm(String message, DialogInterface.OnClickListener yesButton) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.no), null)
                .setNegativeButton(getString(R.string.yes), yesButton)
                .show();
    }

    public void inform(String message) {
        Snackbar.make(getActivity().findViewById(R.id.content_settings), message, Snackbar.LENGTH_SHORT)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
    }

    public void setUpButtonListeners() {
        findPreference(getString(R.string.clear_all_button_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                confirm(
                        getString(R.string.clear_all_confirmation_text),

                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resetAll();
                                inform(getString(R.string.clear_all_done_text));
                            }

                        });
                return true;
            }
        });

        findPreference(getString(R.string.clear_inventory_button_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                confirm(
                        getString(R.string.clear_inventory_confirmation_text),

                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resetInventory();
                                inform(getString(R.string.clear_inventory_done_text));
                            }

                        });
                return true;
            }
        });
        findPreference(getString(R.string.clear_categories_button_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                confirm(
                        getString(R.string.clear_categories_confirmation_text),

                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resetCategories();
                                inform(getString(R.string.clear_categories_done_text));
                            }

                        });
                return true;
            }
        });
        findPreference(getString(R.string.clear_meals_button_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                confirm(
                        getString(R.string.clear_meals_confirmation_text),

                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resetMeals();
                                inform(getString(R.string.clear_meals_done_text));
                            }

                        });
                return true;
            }
        });
    }
}

