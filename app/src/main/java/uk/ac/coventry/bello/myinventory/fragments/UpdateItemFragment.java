package uk.ac.coventry.bello.myinventory.fragments;

import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.inventory.Inventory;
import uk.ac.coventry.bello.myinventory.inventory.InventoryItem;

/**
 * Created by Freshollie on 04/12/2016.
 */

public class UpdateItemFragment extends AddItemFragment {

    public UpdateItemFragment() {
        setPositiveButtonTextStringId(R.string.update_button_text);
        setTitleTextStringId(R.string.title_activity_update_item);
    }

    public void setEditItem(InventoryItem item) {
        setPresetItem(item);

        setPresetItemName(item.getName());
        setPresetItemPrice(item.getPrice());
        setPresetItemQuantity(Inventory.getInstance().getQuantity(item));

    }

}
