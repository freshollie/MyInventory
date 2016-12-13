package uk.ac.coventry.bello.myinventory.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.activities.MainActivity;
import uk.ac.coventry.bello.myinventory.dialogs.AddItemDialog;
import uk.ac.coventry.bello.myinventory.dialogs.UpdateItemDialog;
import uk.ac.coventry.bello.myinventory.fragments.templates.MyInventoryFragment;
import uk.ac.coventry.bello.myinventory.inventory.Inventory;
import uk.ac.coventry.bello.myinventory.adapters.InventoryItemsAdapter;
import uk.ac.coventry.bello.myinventory.inventory.InventoryItem;


public class InventoryFragment extends MyInventoryFragment {
    // TODO: Rename parameter arguments, choose names that match

    private final String TAG = "InventoryFragment";

    private RecyclerView mRecyclerView;
    private InventoryItemsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Inventory mInventory;
    private TextView mEmptyView;
    private MainActivity mMainActivity;
    private FragmentActivity mActivity;

    private OnFragmentInteractionListener mListener;

    public InventoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();

        setUpInventoryListAdapter();

        setUpToolbarLayout();

        setupFabAction();

    }

    public void setUpToolbarLayout() {
        mMainActivity.setAppBarColor(
                ContextCompat.getColor(getContext(), R.color.colorPrimary)
        );
        mMainActivity.setDeleteModeAppBarColor(
                ContextCompat.getColor(getContext(), R.color.colorAccent)
        );

        mMainActivity.setMenuLayout(R.menu.menu_main_inventory);
        mMainActivity.invalidateOptionsMenu();
    }

    /**
     * Called to launch the update item dialog with the provided item
     * @param item
     */
    public void launchUpdateItemDialog(InventoryItem item) {
        UpdateItemDialog updateItemDialog = new UpdateItemDialog();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        updateItemDialog.setEditItem(item);
        updateItemDialog.setAdapter(mAdapter);
        updateItemDialog.show(fragmentManager, "UpdateItemDialog");
    }

    /**
     * Sets the FAB action to open an AddItemDialog
     */
    public void setupFabAction() {
        mMainActivity.setFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AddItemDialog addItemDialog = new AddItemDialog();

                FragmentManager fragmentManager = InventoryFragment.this.getActivity().getSupportFragmentManager();

                addItemDialog.setAdapter(mAdapter);
                addItemDialog.show(fragmentManager, "AddItemDialog");
            }
        });

    }

    public void setUpInventoryListAdapter(){
        mInventory = Inventory.getInstance();
        mInventory.load(mActivity.getApplicationContext());
        if(mRecyclerView == null) {
            mRecyclerView = (RecyclerView) mActivity.findViewById(R.id.inventory_recycler_view);
        }
        mEmptyView = (TextView) getActivity().findViewById(R.id.empty_inventory_view);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(100);
        itemAnimator.setRemoveDuration(100);
        mRecyclerView.setItemAnimator(itemAnimator);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new InventoryItemsAdapter(mInventory, this);
        mRecyclerView.setAdapter(mAdapter);

        onInventoryChanged();
    }

    public void onInventoryChanged(){
        if (mInventory.getInventory().isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mMainActivity = (MainActivity) context;

    }

    public void onSelectModeChanged(){
        if(mAdapter.isSelectionMode()) {
            mActivity.findViewById(R.id.main_activity_content_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Click");
                    mAdapter.setSelectionMode(false);
                }
            });
        } else {
            mActivity.findViewById(R.id.main_activity_content_layout).setOnClickListener(null);
        }

        int menuLayout;

        if(mAdapter!=null){
            if(mAdapter.isSelectionMode()){
                menuLayout = R.menu.menu_main_inventory_highlight;

            } else {
                menuLayout = R.menu.menu_main_inventory;
            }

        } else {
            menuLayout = R.menu.menu_main_inventory;
        }

        mMainActivity.setMenuLayout(menuLayout);

        mMainActivity.invalidateOptionsMenu(); //Calls MainActivity.onPrepareOptionsMenu to change toolbar menu
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean onBackPressed(){
        if(mAdapter.isSelectionMode()){ // Turn the delete mode off if we are in delete mode
            mAdapter.setSelectionMode(false);
            return true;
        }
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Tries to push the list of needed items
     * to google keep and displays an error message
     * if the user doesn't have google keep.
     */
    public void pushToKeep(){
        if (!mInventory.getMissingItemsNameList().isEmpty()) { // We actually have items to put into the shopping list
            try {

                String date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                // Get the date format

                Intent keepIntent = new Intent(Intent.ACTION_SEND);

                keepIntent.setType("text/plain");
                keepIntent.setPackage("com.google.android.keep");

                keepIntent.putExtra(Intent.EXTRA_SUBJECT, getContext().getString(R.string.shopping_list_title, date));
                //Sets the subject to Shopping List + date

                keepIntent.putExtra(Intent.EXTRA_TEXT, TextUtils.join("\n", mInventory.getMissingItemsNameList()));
                // Sets the text to items separated by new line characters

                startActivity(keepIntent);
                // Sends the intent

            } catch (Exception e) {
                // Google keep doesn't exist as a package

                Snackbar.make(mActivity.findViewById(R.id.main_activity_content_layout), getContext().getString(R.string.no_google_keep), Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .show();
            }

        } else {
            // No items for the list
            Snackbar.make(mActivity.findViewById(R.id.main_activity_content_layout), getContext().getString(R.string.no_shopping_items), Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
        }
    }

    public void makeMealFromItems() {
        ArrayList<InventoryItem> ingredients = mAdapter.getSelectedItems();
        mAdapter.setSelectionMode(false);
        onSelectModeChanged();

        mMainActivity.setCurrentFragmentId(MainActivity.MEALS_FRAGMENT_MENU_ID);
        ((MealsFragment) mMainActivity.getCurrentFragment()).setInitialIngredients(ingredients);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_list_to_keep:
                pushToKeep();
                return true;

            case R.id.action_delete_item:
                new AlertDialog.Builder(this.getContext())
                        .setMessage(getString(R.string.delete_items_confirmation))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.no), null)
                        .setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAdapter.onDeleteSelectedItems();
                                mAdapter.setSelectionMode(false);
                                onInventoryChanged();
                            }
                        })
                        .show();
                return true;

            case R.id.action_make_meal:
                makeMealFromItems();
                return true;
        }
        return false;
    }
}
