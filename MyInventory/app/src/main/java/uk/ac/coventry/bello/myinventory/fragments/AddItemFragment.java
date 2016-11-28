package uk.ac.coventry.bello.myinventory.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.inventory.Inventory;
import uk.ac.coventry.bello.myinventory.inventory.InventoryItem;

/**
 * Created by bello on 28/11/2016.
 */

public class AddItemFragment extends DialogFragment {
    private final String TAG = "AddItemFragment";
    private View mView;
    private boolean shouldCloseFlag;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mView = inflater.inflate(R.layout.add_item_dialog, null);
        setUpNumberPicker();
        shouldCloseFlag = true;

        builder.setView(mView)
                // Add action buttons
                .setPositiveButton(R.string.add_button_text, null)
                .setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddItemFragment.this.getDialog().cancel();
                    }
                });

        builder.setCancelable(true);



        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog) getDialog();

        if(d != null)
        {
            d.setCanceledOnTouchOutside(true);

            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (saveItem()) {
                        dismiss();
                    }
                }
            });
        }
    }


    public void setUpNumberPicker() {
        NumberPicker np = (NumberPicker) mView.findViewById(R.id.item_quantity_picker);
        np.setMinValue(0);
        np.setMaxValue(99);
        np.setWrapSelectorWheel(false);
    }

    public String getItemName(){
        return ((EditText) mView.findViewById(R.id.item_name_input)).getText().toString();
    }

    public double getItemPrice(){
        String itemPrice = ((EditText) mView.findViewById(R.id.item_price_input)).getText().toString();
        if(!itemPrice.equals("")) {
            return Double.parseDouble(itemPrice);
        } else {
            return 0;
        }
    }

    public int getItemQuantity(){
        return ((NumberPicker) mView.findViewById(R.id.item_quantity_picker)).getValue();
    }

    public InventoryItem getItem(){
        return new InventoryItem(
                getItemName(),
                getItemPrice()
        );
    }

    private boolean saveItem() {
        Inventory inventory = Inventory.getInstance();
        Log.v(TAG, String.valueOf(inventory.isItem(getItemName())));

        if (getItemName().equals("") | getItemPrice() == 0) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Please enter valid data")
                    .setCancelable(true)
                    .setPositiveButton("Ok", null)
                    .show();
            return false;
        }

        if (!inventory.isItem(getItemName())) {
            inventory.setItem(getItem(), getItemQuantity());
            inventory.save(getActivity().getApplicationContext());
            return true;
        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("This item already exists")
                    .setCancelable(true)
                    .setPositiveButton("Ok", null)
                    .show();
        }
        return false;
    }


}





