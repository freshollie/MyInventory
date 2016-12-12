package uk.ac.coventry.bello.myinventory.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DecimalFormat;

import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.adapters.InventoryItemsAdapter;
import uk.ac.coventry.bello.myinventory.inventory.Inventory;
import uk.ac.coventry.bello.myinventory.inventory.InventoryItem;

/**
 * Created by bello on 28/11/2016.
 */

public class AddItemDialog extends DialogFragment {
    private final String TAG = "AddItemDialog";
    private View mView;
    private InventoryItemsAdapter mAdapter;
    private InventoryItem presetItem;

    private int positiveButtonTextStringId = R.string.add_button_text;
    private int cancelButtonTextStringId = R.string.cancel_button_text;
    private int titleTextStringId = R.string.title_activity_add_item;

    private String presetItemName;
    private double presetItemPrice = -1;
    private int presetItemQuantity = -1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mView = inflater.inflate(R.layout.add_item_dialog_content, null);
        setUpNumberPicker();

        builder.setView(mView)
                // Add action buttons
                .setPositiveButton(positiveButtonTextStringId, null)
                .setNegativeButton(cancelButtonTextStringId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddItemDialog.this.getDialog().cancel();
                    }
                });

        builder.setCancelable(true);

        setTitle(getString(titleTextStringId));

        fillItemName();
        fillItemPrice();
        fillItemQuantity();

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog) getDialog();

        if(d != null)
        {
            d.setCanceledOnTouchOutside(true);

            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (saveItem()) {
                        dismiss();
                        if (mAdapter != null) {
                            mAdapter.notifyInventoryChanged();
                        }
                    }
                }
            });
        }
    }

    public void setAdapter(InventoryItemsAdapter adapter) {
        mAdapter = adapter;
    }

    public void setPositiveButtonTextStringId(int id) {
        positiveButtonTextStringId = id;
    }

    public void setCancelButtonTextStringId(int id) {
        cancelButtonTextStringId = id;
    }

    public void setTitleTextStringId(int id) {
        titleTextStringId = id;
    }

    public void setTitle(String title) {
        ((TextView) mView.findViewById(R.id.add_item_title_text)).setText(title);
    }

    public void setUpNumberPicker() {
        NumberPicker np = (NumberPicker) mView.findViewById(R.id.item_quantity_picker);
        np.setMinValue(0);
        np.setMaxValue(99);
        np.setWrapSelectorWheel(false);
    }

    public String getItemName() {
        return ((EditText) mView.findViewById(R.id.item_name_input)).getText().toString();
    }

    public void setPresetItemName(String name) {
        presetItemName = name;
    }

    public void fillItemName() {
        if (presetItemName != null) {
            ((EditText) mView.findViewById(R.id.item_name_input)).setText(presetItemName);
        }
    }

    public double getItemPrice() {
        String itemPrice = ((EditText) mView.findViewById(R.id.item_price_input)).getText().toString();
        if(!itemPrice.equals("")) {
            return Double.parseDouble(itemPrice);
        } else {
            return 0;
        }
    }

    public void setPresetItemPrice(double price) {
        presetItemPrice = price;
    }

    public void fillItemPrice(){
        if (presetItemPrice > 0) {
            DecimalFormat twoDForm = new DecimalFormat("0.00");
            ((EditText) mView.findViewById(R.id.item_price_input)).setText(String.valueOf(twoDForm.format(presetItemPrice)));
        }
    }

    public int getItemQuantity() {
        return ((NumberPicker) mView.findViewById(R.id.item_quantity_picker)).getValue();
    }

    public void setPresetItemQuantity(int quantity) {
        presetItemQuantity = quantity;
    }

    public void fillItemQuantity() {
        if (presetItemQuantity > -1) {
            ((NumberPicker) mView.findViewById(R.id.item_quantity_picker)).setValue(presetItemQuantity);
        }
    }

    public InventoryItem getItem() {
        if (presetItem != null) {
            presetItem.setName(getItemName());
            presetItem.setPrice(getItemPrice());
            return presetItem;
        }

        return new InventoryItem(
                getItemName(),
                getItemPrice()
        );
    }

    public void setPresetItem(InventoryItem item) {
        presetItem = item;
    }

    public boolean validate(String name, double price, int quantity){
        Inventory inventory = Inventory.getInstance();
        String alertMessage = "";

        if (name.equals("")) {
            alertMessage = getString(R.string.not_valid_name);
        }

        if (price == 0) {
            alertMessage = getString(R.string.not_valid_price);
        }

        if (inventory.isItemName(name) && !name.equals(presetItemName)) {
            alertMessage = getString(R.string.name_already_exist);
        }

        if (!alertMessage.equals("")) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(alertMessage)
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.ok), null)
                    .show();
            return false;
        }
        return true;
    }

    private boolean saveItem() {

        if (validate(getItemName(), getItemPrice(), getItemQuantity())) {
            Inventory inventory = Inventory.getInstance();

            inventory.setItem(getItem(), getItemQuantity());
            inventory.save(getActivity().getApplicationContext());
            return true;
        }
        return false;
    }
}





