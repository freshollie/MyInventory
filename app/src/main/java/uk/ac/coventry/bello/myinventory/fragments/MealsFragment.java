package uk.ac.coventry.bello.myinventory.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.activities.MainActivity;
import uk.ac.coventry.bello.myinventory.adapters.MealsAdapter;
import uk.ac.coventry.bello.myinventory.fragments.templates.MyInventoryFragment;
import uk.ac.coventry.bello.myinventory.inventory.InventoryItem;
import uk.ac.coventry.bello.myinventory.inventory.Meal;
import uk.ac.coventry.bello.myinventory.inventory.MealsList;


public class MealsFragment extends MyInventoryFragment {
    // TODO: Rename parameter arguments, choose names that match

    private final String TAG = "MealsFragment";

    private RecyclerView mRecyclerView;
    private MealsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MealsList mMealsList;
    private TextView mEmptyView;
    private MainActivity mMainActivity;
    private FragmentActivity mActivity;
    private ArrayList<InventoryItem> initialIngredients;

    private OnFragmentInteractionListener mListener;

    public MealsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_meal, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();

        mMealsList = MealsList.getInstance();
        mMealsList.load(mActivity.getApplicationContext());

        setUpMealsListAdapter();

        setUpToolbarLayout();

        setUpFabAction();

        if (initialIngredients != null) {
            launchAddMealFragment(initialIngredients);
            initialIngredients = null;
        }
    }

    public void setUpToolbarLayout() {
        mMainActivity.setAppBarColor(
                ContextCompat.getColor(getContext(), R.color.colorSecondary)
        );
        mMainActivity.setDeleteModeAppBarColor(
                ContextCompat.getColor(getContext(), R.color.colorAccent)
        );

        mMainActivity.setMenuLayout(R.menu.menu_main_meals_list);
        mMainActivity.invalidateOptionsMenu();
    }

    public void launchAddMealFragment(ArrayList<InventoryItem> initialIngredients){
        AddMealFragment addMealFragment = new AddMealFragment();

        if (initialIngredients != null) {
            addMealFragment.setPresetIngredients(initialIngredients);
        }

        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();

        addMealFragment.setAdapter(mAdapter);
        addMealFragment.show(fragmentManager, "AddMealFragment");
    }

    public void launchUpdateMealFragment(Meal meal){
        UpdateMealFragment updateMealFragment = new UpdateMealFragment();
        updateMealFragment.setEditMeal(meal);

        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();

        updateMealFragment.setAdapter(mAdapter);
        updateMealFragment.show(fragmentManager, "UpdateMealFragment");
    }

    public void setInitialIngredients(ArrayList<InventoryItem> ingredients){
        initialIngredients = ingredients;
        if (mActivity != null) {
            launchAddMealFragment(ingredients);
            initialIngredients = null;
        }
    }

    public void setUpFabAction() {
        mMainActivity.setFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAddMealFragment(null);
            }
        });

    }

    public void setUpMealsListAdapter(){

        if(mRecyclerView == null) {
            mRecyclerView = (RecyclerView) mActivity.findViewById(R.id.meals_recycler_view);
        }
        mEmptyView = (TextView) getActivity().findViewById(R.id.empty_meals_view);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(100);
        itemAnimator.setRemoveDuration(100);
        mRecyclerView.setItemAnimator(itemAnimator);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MealsAdapter(mMealsList, this);
        mRecyclerView.setAdapter(mAdapter);

        onMealsListChanged();
    }

    public void onMealsListChanged(){
        if (mMealsList.isEmpty()) {
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

    public void onSelectionModeChanged(){
        if(mAdapter.isSelectionMode()) {
            mActivity.findViewById(R.id.meals_recycler_view).setOnClickListener(new View.OnClickListener() {
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
                menuLayout = R.menu.menu_main_delete;

            } else {
                menuLayout = R.menu.menu_main_meals_list;
            }

        } else {
            menuLayout = R.menu.menu_main_meals_list;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_delete_item:
                new AlertDialog.Builder(this.getContext())
                        .setMessage(getString(R.string.delete_meals_confirmation))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.no), null)
                        .setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAdapter.onDeleteSelectedMeals();
                                mAdapter.setSelectionMode(false);
                            }
                        })
                        .show();
                return true;
        }
        return false;
    }
}
