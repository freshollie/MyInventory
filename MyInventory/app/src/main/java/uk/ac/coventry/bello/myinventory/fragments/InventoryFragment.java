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

        setUpFabAction();

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

    public void setUpFabAction() {
        mMainActivity.setFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AddItemFragment addItemFragment = new AddItemFragment();

                FragmentManager fragmentManager = InventoryFragment.this.getActivity().getSupportFragmentManager();

                addItemFragment.setAdapter(mAdapter);
                addItemFragment.show(fragmentManager, "AddItemFragment");
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

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
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
        if(mAdapter.isSelectMode()) {
            mActivity.findViewById(R.id.main_activity_content_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Click");
                    mAdapter.setSelectMode(false);
                }
            });
        } else {
            mActivity.findViewById(R.id.main_activity_content_layout).setOnClickListener(null);
        }

        int menuLayout;

        if(mAdapter!=null){
            if(mAdapter.isSelectMode()){
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
        if(mAdapter.isSelectMode()){ // Turn the delete mode off if we are in delete mode
            mAdapter.setSelectMode(false);
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

    public void pushToKeep(){
        if (!mInventory.getMissingItemsNameList().isEmpty()) { // We actually have items to put into the shopping list
            try {

                String date = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
                Intent keepIntent = new Intent(Intent.ACTION_SEND);

                keepIntent.setType("text/plain");
                keepIntent.setPackage("com.google.android.keep");
                keepIntent.putExtra(Intent.EXTRA_SUBJECT, getContext().getString(R.string.shopping_list_title, date));
                keepIntent.putExtra(Intent.EXTRA_TEXT, TextUtils.join("\n", mInventory.getMissingItemsNameList()));

                startActivity(keepIntent);

            } catch (Exception e) {

                Snackbar.make(mActivity.findViewById(R.id.main_activity_content_layout), getContext().getString(R.string.no_google_keep), Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .show();
            }

        } else {
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
        mAdapter.setSelectMode(false);
        onSelectModeChanged();

        mMainActivity.setCurrentFragmentId(MainActivity.MEALS_FRAGMENT_MENU_ID);
        ((MealsFragment) mMainActivity.getCurrentFragment()).setInitalIngredients(ingredients);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_list_to_keep:
                pushToKeep();
                return true;

            case R.id.action_delete_item:
                new AlertDialog.Builder(this.getContext())
                        .setMessage("Are you sure you want to delete the selected item(s)?")
                        .setCancelable(true)
                        .setPositiveButton("No", null)
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAdapter.onDeleteSelectedItems();
                                mAdapter.setSelectMode(false);
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
