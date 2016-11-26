package uk.ac.coventry.bello.myinventory.inventory;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.activities.MainActivity;
import uk.ac.coventry.bello.myinventory.fragments.InventoryFragment;


/**
 * Created by Freshollie on 14/11/2016.
 */

public class InventoryItemsAdapter extends RecyclerView.Adapter<InventoryItemsAdapter.ItemViewHolder> {
    private Inventory mInventory;
    private List<InventoryItem> mItemList;
    private InventoryFragment mParentInventoryFragment;
    private String TAG = "InventoryItemAdapter";
    private ArrayList<Integer> deleteItems;
    private boolean deleteMode;

    public String SORT_BY_NAME = "sort_by_name";
    public String SORT_BY_PRICE = "sort_by_price";
    public String SORT_BY_QUANTITY = "sort_by_quantity";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mItemNameText;
        public TextView mItemPriceText;
        public TextView mQuantityText;
        public ImageButton mAddButton;
        public ImageButton mRemoveButton;
        public TableLayout mTableLayout;
        public CardView mCardView;
        public View mView;

        public ItemViewHolder(View v) {
            super(v);
            mItemNameText = (TextView) v.findViewById(R.id.item_card_name_text);
            mItemPriceText = (TextView) v.findViewById(R.id.item_card_price_text);
            mQuantityText = (TextView) v.findViewById(R.id.item_card_quantity_text);

            mAddButton = (ImageButton) v.findViewById(R.id.item_card_add_quantity_button);
            mRemoveButton = (ImageButton) v.findViewById(R.id.item_card_remove_quantity_button);
            mTableLayout = (TableLayout) v.findViewById(R.id.item_card_table);
            mCardView = (CardView) v.findViewById(R.id.card_view);

            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InventoryItemsAdapter(Inventory inventory, InventoryFragment inventoryFragment) {
        mInventory = inventory;
        mItemList = inventory.getItems();
        mParentInventoryFragment = inventoryFragment;
        deleteMode = false;
        resetDeleteItems();
    }

    public void setDeleteMode(boolean mode) {
        resetDeleteItems();
        deleteMode = mode;
        mParentInventoryFragment.onDeleteModeChange();
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
            InventoryItem item = mItemList.get(i);
            mInventory.removeItem(item);
        }
        resetDeleteItems();

        mInventory.save(mParentInventoryFragment.getContext());
        mItemList = mInventory.getItems();
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InventoryItemsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_item_card_layout, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ItemViewHolder vh = new ItemViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        DecimalFormat twoDForm = new DecimalFormat("#.00");

        final int holderPosition = position;

        final InventoryItem item = mItemList.get(position);

        // Set all the card text attributes

        holder.mItemNameText.setText(mItemList.get(position).getName());
        holder.mItemPriceText.setText(mParentInventoryFragment.getString(R.string.item_card_price_text, twoDForm.format(mItemList.get(position).getPrice())));
        holder.mQuantityText.setText(String.valueOf(mInventory.getQuantity(item)));

        // Change the card colour based on if the user has highlighted it for deletion

        if(deleteItems.contains(position)){
            holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(mParentInventoryFragment.getContext(), android.R.color.darker_gray));
        } else {
            holder.mCardView.setCardBackgroundColor(Color.WHITE);
        }

        // Set up the buttons on the card based on what mode we are in, delete or normal

        holder.mTableLayout.setOnClickListener(null); // Reset click listeners to noting
        holder.mAddButton.setOnClickListener(null);
        holder.mRemoveButton.setOnClickListener(null);
        holder.mTableLayout.setOnLongClickListener(null);

        // Set these click listeners if we are not in delete mode
        if (!isDeleteMode()) {
            holder.mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInventory.setItem(item, mInventory.getQuantity(item) + 1);
                    mInventory.save(view.getContext());
                    notifyItemChanged(holderPosition);
                }
            });

            holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mInventory.getQuantity(item) > 0) {
                        mInventory.setItem(item, mInventory.getQuantity(item) - 1);
                        mInventory.save(view.getContext());
                        notifyItemChanged(holder.getAdapterPosition());

                    }
                }
            });

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
        return mItemList.size();
    }
}