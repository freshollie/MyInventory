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
import uk.ac.coventry.bello.myinventory.inventory.Meal;
import uk.ac.coventry.bello.myinventory.inventory.MealsList;


/**
 * Created by Freshollie on 27/11/2016.
 */


public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealViewHolder> {
    private MealsList mMealsList;
    private MealsFragment mParentMealsFragment;
    private String TAG = "MealsAdapter";
    private ArrayList<Integer> deleteItems;
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
        resetDeleteItems();
    }

    public void setDeleteMode(boolean mode) {
        resetDeleteItems();
        deleteMode = mode;
        mParentMealsFragment.onDeleteModeChange();
        notifyDataSetChanged();
    }

    public boolean isDeleteMode(){
        return deleteMode;
    }

    public void resetDeleteItems(){
        deleteItems = new ArrayList<>();
    }

    public void appendDeleteItem(int i){
        deleteItems.add(i);
        notifyItemChanged(i);
    }

    public void removeDeleteItem(int i){
        deleteItems.remove((Object)i);

        notifyItemChanged(i);

        if(deleteItems.size() < 1) {
            setDeleteMode(false);

        }
    }

    public ArrayList<Integer> getDeleteItems(){
        return deleteItems;
    }

    public void setSort(String sort){

    }

    public void onRemoveSelectedItems(){
        for(int i: deleteItems){
            Meal meal = mMealsList.get(i);
            mMealsList.remove(meal);
        }
        resetDeleteItems();

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

        if(deleteItems.contains(position)) {
            holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(mParentMealsFragment.getContext(), android.R.color.darker_gray));

        } else if (!Inventory.getInstance().isNotMissing(meal.getIngredients()) && (!isDeleteMode())){ // We don't have all the ingredients for this item
            holder.mCardView.setCardBackgroundColor(Color.GRAY);
        } else {
            holder.mCardView.setCardBackgroundColor(Color.WHITE);
        }

        // Set up the buttons on the card based on what mode we are in, delete or normal

        holder.mTableLayout.setOnClickListener(null); // Reset click listeners to noting
        holder.mTableLayout.setOnLongClickListener(null);

        // Set these click listeners if we are not in delete mode
        if (!isDeleteMode()) {
            holder.mTableLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setDeleteMode(true);
                    appendDeleteItem(holderPosition);
                    return true;
                }
            });
        }

        holder.mTableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDeleteMode()){

                    if(getDeleteItems().contains(holderPosition)) {

                        removeDeleteItem(holderPosition);

                    } else {
                        appendDeleteItem(holderPosition);
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

