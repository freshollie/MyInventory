package uk.ac.coventry.bello.myinventory;

import android.content.Context;
import android.support.annotation.BoolRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.text.DecimalFormat;


/**
 * Created by Freshollie on 14/11/2016.
 */

public class InventoryItemsAdapter extends RecyclerView.Adapter<InventoryItemsAdapter.ItemViewHolder> {
    private Inventory mInventory;
    private List<InventoryItem> mItemList;
    private Context mContext;
    private String TAG = "InventoryItemAdapter";
    private ArrayList<InventoryItem> deleteItems;
    private boolean deleteMode;

    public String SORT_BY_NAME = "sort_by_name";
    public String SORT_BY_PRICE = "sort_by_price";
    public String SORT_BY_QUANTITY = ""

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
        public CardView mCardView;
        public View mView;
        public boolean selected;

        public ItemViewHolder(View v) {
            super(v);
            mItemNameText = (TextView)v.findViewById(R.id.item_card_name_text);
            mItemPriceText = (TextView)v.findViewById(R.id.item_card_price_text);
            mQuantityText = (TextView)v.findViewById(R.id.item_card_quantity_text);

            mAddButton = (ImageButton)v.findViewById(R.id.item_card_add_quantity_button);
            mRemoveButton = (ImageButton)v.findViewById(R.id.item_card_remove_quantity_button);

            mCardView = (CardView)v.findViewById(R.id.card_view);

            mView = v;
        }

        public void setButtons(final InventoryItem item, final Inventory inventory, final InventoryItemsAdapter inventoryItemsAdapter, final int position){

            if (!inventoryItemsAdapter.getDeleteMode()) {
                mAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inventory.setItem(item, inventory.getQuantity(item) + 1);
                        inventory.save(view.getContext());
                        inventoryItemsAdapter.notifyItemChanged(position);
                    }
                });

                mRemoveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (inventory.getQuantity(item) > 0) {
                            inventory.setItem(item, inventory.getQuantity(item) - 1);
                            inventory.save(view.getContext());
                            inventoryItemsAdapter.notifyItemChanged(position);

                        }
                    }
                });

                mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ((MainActivity)inventoryItemsAdapter.mContext).getActionBar().
                        return false;
                    }
                });
            }

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(inventoryItemsAdapter.getDeleteMode()){
                        if(selected) {
                            inventoryItemsAdapter.removeDeleteItem(item);
                        } else {
                            inventoryItemsAdapter.appendDeleteItem(item);
                        }
                    } else {
                        mView.findViewById(R.id.item_card_table);
                    }
                }
            });

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InventoryItemsAdapter(Inventory inventory, Context context) {
        mInventory = inventory;
        mItemList = inventory.getItems();
        mContext = context;
        deleteMode = false;
        resetDeleteItems();
    }

    public void setDeleteMode(boolean mode) {
        resetDeleteItems();
        deleteMode = mode;
    }

    public void setDeleteModeTheme(){
        mContext. findViewById(R.id.toolbar);
    }

    public boolean getDeleteMode(){
        return deleteMode;
    }

    public void resetDeleteItems(){
        deleteItems = new ArrayList<InventoryItem>();
    }

    public void appendDeleteItem(InventoryItem item){
        deleteItems.add(item);
    }

    public void removeDeleteItem(InventoryItem item){
        deleteItems.remove(item);
    }

    public setSort(String sort)

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

        InventoryItem item = mItemList.get(position);

        holder.mItemNameText.setText(mItemList.get(position).getName());

        holder.mItemPriceText.setText(mContext.getString(R.string.item_card_price_text, twoDForm.format(mItemList.get(position).getPrice())));

        holder.mQuantityText.setText(String.valueOf(mInventory.getQuantity(item)));

        holder.setButtons(item, mInventory, this, position);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}