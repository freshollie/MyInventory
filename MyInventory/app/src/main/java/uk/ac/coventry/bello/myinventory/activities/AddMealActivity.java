package uk.ac.coventry.bello.myinventory.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.util.Log;

import uk.ac.coventry.bello.myinventory.inventory.Inventory;
import uk.ac.coventry.bello.myinventory.inventory.InventoryItem;
import uk.ac.coventry.bello.myinventory.R;

public class AddMealActivity extends AppCompatActivity {
    private final String TAG = "AddMealActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        AddMealActivity.this.finish();
    }

    public String getItemName(){
        return ((EditText)findViewById(R.id.item_name_input)).getText().toString();
    }

    public double getItemPrice(){
        String itemPrice = ((EditText)findViewById(R.id.item_price_input)).getText().toString();
        if(!itemPrice.equals("")) {
            return Double.parseDouble(itemPrice);
        } else {
            return 0;
        }
    }

    public int getItemQuantity(){
        return ((NumberPicker)findViewById(R.id.item_quantity_picker)).getValue();
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
            new AlertDialog.Builder(this)
                    .setMessage("Please enter valid data")
                    .setCancelable(true)
                    .setPositiveButton("Ok", null)
                    .show();
            return false;
        }

        if (!inventory.isItem(getItemName())) {
            inventory.setItem(getItem(), getItemQuantity());
            inventory.save(getApplicationContext());
            return true;
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("This item already exists")
                    .setCancelable(true)
                    .setPositiveButton("Ok", null)
                    .show();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_done:
                if (saveItem()) {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }


}
