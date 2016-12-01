package uk.ac.coventry.bello.myinventory.adapters;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.text.DecimalFormat;

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
    private MealsList mMealsList;
    private MealsFragment mParentMealsFragment;
    private String TAG = "MealsAdapter";
    private ArrayList<Integer> selectedItems;
    private boolean deleteMode;

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

        public MealViewHolder(View v) {
            super(v);
            mMealNameText = (TextView) v.findViewById(R.id.meal_card_name_text);
            mMealPriceText = (TextView) v.findViewById(R.id.meal_card_price_text);
            mMealCategoryText = (TextView) v.findViewById(R.id.meal_card_category_text);

            mTableLayout = (TableLayout) v.findViewById(R.id.meal_card_table);
            mCardView = (CardView) v.findViewById(R.id.meal_card_view);

            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MealsAdapter(MealsList mealsListObject, MealsFragment mealFragment) {
        mMealsList = mealsListObject;
        mParentMealsFragment = mealFragment;
        deleteMode = false;
        resetSelectedItems();
    }

    public void setSelectMode(boolean mode) {
        resetSelectedItems();
        deleteMode = mode;
        mParentMealsFragment.onSelectModeChanged();
        notifyDataSetChanged();
    }

    public boolean isSelectMode(){
        return deleteMode;
    }

    public void resetSelectedItems(){
        selectedItems = new ArrayList<>();
    }

    public void appendSelectedItem(int i){
        selectedItems.add(i);
        notifyItemChanged(i);
    }

    public void removeSelectedItem(int i){
        selectedItems.remove((Object)i);

        notifyItemChanged(i);

        if(selectedItems.size() < 1) {
            setSelectMode(false);

        }
    }

    public ArrayList<Integer> getSelectedItemIndexes(){
        return selectedItems;
    }

    public ArrayList<Meal> getSelectedItems() {
        ArrayList<Meal> items = new ArrayList<>();

        for (int i: getSelectedItemIndexes()) {
            items.add(mMealsList.get(i));
        }

        return items;
    }

    public void setSort(String sort){

    }

    public void onDeleteSelectedItems(){
        for(int i: selectedItems){
            Meal meal = mMealsList.get(i);
            mMealsList.remove(meal);
        }
        resetSelectedItems();

        mMealsList.save(mParentMealsFragment.getContext());
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meals_card_layout, parent, false);

        // set the view's size, margins, paddings and layout parameters
        MealViewHolder vh = new MealViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MealViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        DecimalFormat twoDForm = new DecimalFormat("#.00");

        final int holderPosition = position;

        final Meal meal = mMealsList.get(position);

        // Set all the card text attributes

        holder.mMealNameText.setText(meal.getName());
        holder.mMealPriceText.setText(mParentMealsFragment.getString(R.string.item_card_price_text, twoDForm.format(meal.getPrice())));
        holder.mMealCategoryText.setText(meal.getCategory());

        // Change the card colour based on if the user has highlighted it for deletion

        if(selectedItems.contains(position)) {
            holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(mParentMealsFragment.getContext(), android.R.color.darker_gray));

        } else if (!Inventory.getInstance().isNotMissing(meal.getIngredients()) && (!isSelectMode())){ // We don't have all the ingredients for this item
            holder.mCardView.setCardBackgroundColor(Color.GRAY);
        } else {
            holder.mCardView.setCardBackgroundColor(Color.WHITE);
        }

        // Set up the buttons on the card based on what mode we are in, delete or normal

        holder.mTableLayout.setOnClickListener(null); // Reset click listeners to noting
        holder.mTableLayout.setOnLongClickListener(null);

        // Set these click listeners if we are not in delete mode
        if (!isSelectMode()) {
            holder.mTableLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setSelectMode(true);
                    appendSelectedItem(holderPosition);
                    return true;
                }
            });
        }

        holder.mTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSelectMode()){

                    if(getSelectedItemIndexes().contains(holderPosition)) {

                        removeSelectedItem(holderPosition);

                    } else {
                        appendSelectedItem(holderPosition);
                    }
                } else {
                    holder.mView.findViewById(R.id.item_card_table);
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

