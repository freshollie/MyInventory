package uk.ac.coventry.bello.myinventory.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.activities.AddItemActivity;
import uk.ac.coventry.bello.myinventory.activities.AddMealActivity;
import uk.ac.coventry.bello.myinventory.activities.MainActivity;
import uk.ac.coventry.bello.myinventory.inventory.MealsAdapter;
import uk.ac.coventry.bello.myinventory.inventory.MealsList;


public class MealsFragment extends MyInventoryFragment {
    // TODO: Rename parameter arguments, choose names that match

    private final String TAG = "InventoryFragment";

    private RecyclerView mRecyclerView;
    private MealsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MealsList mMealsList;
    private TextView mEmptyView;
    private MainActivity mMainActivity;
    private FragmentActivity mActivity;

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

        mMainActivity.setAppBarColor(
                ContextCompat.getColor(getContext(), R.color.colorSecondary)
        );
        mMainActivity.setDeleteModeAppBarColor(
                ContextCompat.getColor(getContext(), R.color.colorAccent)
        );

        mMainActivity.setMenuLayout(R.menu.menu_main_meals_list);
        mMainActivity.invalidateOptionsMenu();

        mMainActivity.setFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(MealsFragment.this.getActivity(), AddMealActivity.class);
                startActivity(mIntent);
            }
        });
    }

    public void setUpMealsListAdapter(){

        if(mRecyclerView == null) {
            mRecyclerView = (RecyclerView) mActivity.findViewById(R.id.meals_recycler_view);
        }
        mEmptyView = (TextView) getActivity().findViewById(R.id.empty_meals_view);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
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

    public void onDeleteModeChange(){
        if(mAdapter.isDeleteMode()) {
            mActivity.findViewById(R.id.main_activity_content_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Click");
                    mAdapter.setDeleteMode(false);
                }
            });
        } else {
            mActivity.findViewById(R.id.main_activity_content_layout).setOnClickListener(null);
        }

        int menuLayout;

        if(mAdapter!=null){
            if(mAdapter.isDeleteMode()){
                menuLayout = R.menu.menu_main_delete;

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
        if(mAdapter.isDeleteMode()){ // Turn the delete mode off if we are in delete mode
            mAdapter.setDeleteMode(false);
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
                        .setMessage("Are you sure you want to delete the selected item(s)?")
                        .setCancelable(true)
                        .setPositiveButton("No", null)
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAdapter.onRemoveSelectedItems();
                                mAdapter.setDeleteMode(false);
                                onMealsListChanged();
                            }
                        })
                        .show();
                return true;
        }
        return false;
    }
}
