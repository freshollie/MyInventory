package uk.ac.coventry.bello.myinventory.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;

import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.fragments.MealsFragment;
import uk.ac.coventry.bello.myinventory.inventory.Inventory;
import uk.ac.coventry.bello.myinventory.inventory.InventoryItem;
import uk.ac.coventry.bello.myinventory.inventory.Meal;
import uk.ac.coventry.bello.myinventory.inventory.MealsList;


/**
 * Created by Freshollie on 27/11/2016.
 */


public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealViewHolder> {
    private MealsList mMealsListObject;
    private ArrayList<Meal> mMealsList;
    private ArrayList<Meal> mOldMealList = new ArrayList<>();
    private MealsFragment mParentMealsFragment;
    private String TAG = "MealsAdapter";
    private ArrayList<Integer> selectedMeals;
    private boolean selectionMode;

    public String SORT_BY_NAME = "sort_by_name";
    public String SORT_BY_PRICE = "sort_by_price";
    public String SORT_BY_QUANTITY = "sort_by_quantity";
    public String SORT_BY_CATEGORY = "sort_by_category";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MealViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mMealNameText;
        public TextView mMealPriceText;
        public TextView mMealCategoryText;
        public TableLayout mTableLayout;
        public CardView mCardView;
        public View mView;
        public ColorStateList defaultTextColors;

        public MealViewHolder(View v) {
            super(v);
            mMealNameText = (TextView) v.findViewById(R.id.meal_card_name_text);
            mMealPriceText = (TextView) v.findViewById(R.id.meal_card_price_text);
            mMealCategoryText = (TextView) v.findViewById(R.id.meal_card_category_text);

            defaultTextColors = mMealCategoryText.getTextColors();

            mTableLayout = (TableLayout) v.findViewById(R.id.meal_card_table);
            mCardView = (CardView) v.findViewById(R.id.meal_card_view);

            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MealsAdapter(MealsList mealsListObject, MealsFragment mealFragment) {
        mMealsListObject = mealsListObject;
        collectMealsList();

        mParentMealsFragment = mealFragment;
        selectionMode = false;
        resetSelectedMeals();
    }

    public void collectMealsList() {
        mMealsList = mMealsListObject.getListCopy();
        Collections.sort(mMealsList, new Comparator<Meal>() {
            @Override
            public int compare(Meal meal, Meal t1) {
                return meal.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
            }
        });
    }

    public void handleDataSetChangedAnimations() {
        ArrayList<Meal> reversedOldMealList = new ArrayList<>(mOldMealList);
        Collections.reverse(reversedOldMealList);

        for (Meal meal: reversedOldMealList) {
            if (!mMealsList.contains(meal)) {
                notifyItemRemoved(mOldMealList.indexOf(meal));
            }
        }

        for (Meal meal: mMealsList) {
            if (!mOldMealList.contains(meal)) {
                notifyItemInserted(mMealsList.indexOf(meal));
            }
        }

        notifyItemRangeChanged(0, getItemCount());
/*
        for (InventoryItem item: mItemList) {
            if (mItemList.indexOf(item) != mOldItemList.indexOf(item)) {
                Log.v(TAG, "Item moved from " + String.valueOf(mItemList.indexOf(item)));
                notifyItemMoved(mOldItemList.indexOf(item), mItemList.indexOf(item));
            }
        }
        */

    }

    public void notifyMealsListChanged(){
        mOldMealList = mMealsList;
        collectMealsList();

        handleDataSetChangedAnimations();
        mParentMealsFragment.onMealsListChanged();
    }

    public void setSelectionMode(boolean mode) {
        resetSelectedMeals();
        selectionMode = mode;
        mParentMealsFragment.onSelectionModeChanged();

        notifyMealsListChanged();
    }

    public boolean isSelectionMode(){
        return selectionMode;
    }

    public void resetSelectedMeals(){
        selectedMeals = new ArrayList<>();
    }

    public void appendSelectedMeal(int i){
        selectedMeals.add(i);
        notifyItemChanged(i);
    }

    public void removeSelectedMeal(int i){
        selectedMeals.remove((Object)i);

        notifyItemChanged(i);

        if(selectedMeals.size() < 1) {
            setSelectionMode(false);

        }
    }

    public ArrayList<Integer> getSelectedMealIndexes(){
        return selectedMeals;
    }

    public ArrayList<Meal> getSelectedMeals() {
        ArrayList<Meal> items = new ArrayList<>();

        for (int i: getSelectedMealIndexes()) {
            items.add(mMealsList.get(i));
        }

        return items;
    }

    public void setSort(String sort){
        // Stub
    }

    public void onDeleteSelectedMeals(){
        for(Meal meal: getSelectedMeals()){
            mMealsListObject.remove(meal);
        }
        resetSelectedMeals();

        mMealsListObject.save(mParentMealsFragment.getContext());

        notifyMealsListChanged();

        if(getItemCount() < 1){
            mOldMealList = mMealsList;
            notifyDataSetChanged();
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meal_card_layout, parent, false);

        // set the view's size, margins, paddings and layout parameters
        MealViewHolder vh = new MealViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MealViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        DecimalFormat twoDForm = new DecimalFormat("0.00");

        final int holderPosition = position;

        final Meal meal = mMealsList.get(position);

        // Set all the card text attributes

        holder.mMealNameText.setText(meal.getName());
        holder.mMealPriceText.setText(mParentMealsFragment.getString(R.string.item_card_price_text, twoDForm.format(meal.getPrice())));
        holder.mMealCategoryText.setText(meal.getCategory());

        // Change the card colour based on if the user has highlighted it for deletion

        if(selectedMeals.contains(position)) {
            holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(mParentMealsFragment.getContext(), android.R.color.darker_gray));

        } else {
            holder.mCardView.setCardBackgroundColor(Color.WHITE);
            holder.mMealNameText.setTextColor(holder.defaultTextColors);
            holder.mMealPriceText.setTextColor(holder.defaultTextColors);
            holder.mMealCategoryText.setTextColor(holder.defaultTextColors);
        }

        if (!Inventory.getInstance().isNotMissing(meal.getIngredients()) && !isSelectionMode()) { // We don't have all the ingredients for this item
            holder.mMealNameText.setTextColor(Color.LTGRAY);
            holder.mMealPriceText.setTextColor(Color.LTGRAY);
            holder.mMealCategoryText.setTextColor(Color.LTGRAY);
        }


        // Set up the buttons on the card based on what mode we are in, delete or normal

        holder.mTableLayout.setOnClickListener(null); // Reset click listeners to noting
        holder.mTableLayout.setOnLongClickListener(null);

        // Set these click listeners if we are not in delete mode
        if (!isSelectionMode()) {
            holder.mTableLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setSelectionMode(true);
                    appendSelectedMeal(holderPosition);
                    return true;
                }
            });
        }

        holder.mTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSelectionMode()) {
                    if(getSelectedMealIndexes().contains(holderPosition)) {
                        removeSelectedMeal(holderPosition);
                    } else {
                        appendSelectedMeal(holderPosition);
                    }
                } else {
                    mParentMealsFragment.launchUpdateMealFragment(meal);
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMealsList.size();
    }
}

