package uk.ac.coventry.bello.myinventory.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.WindowManager;
import android.os.Handler;

import uk.ac.coventry.bello.myinventory.fragments.InventoryFragment;
import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.inventory.MyInventoryFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private final int INVENTORY_FRAGMENT_MENU_ID = 0;
    private final int MEALS_FRAGMENT_MENU_ID = 1;

    private Integer menuLayout;
    private FloatingActionButton mFab;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private DrawerArrowDrawable mDrawerArrow;
    private Integer mCurrentFragmentId;
    private NavigationView mNavigationView;
    private MyInventoryFragment mCurrentFragment;
    private FragmentManager mFragmentManager;
    private Handler mHandler;

    private Integer statusBarPrimaryColor;
    private Integer toolbarPrimaryColor;
    private Integer statusBarAccentColor;
    private Integer toolbarAccentColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(mIntent);
            }
        });


        mDrawer = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0); // this disables the animation
            }
        };
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawer.addDrawerListener(mDrawerToggle);

        mDrawerArrow = new DrawerArrowDrawable(this);
        mDrawerArrow.setColor(Color.WHITE);
        mDrawerToggle.setDrawerArrowDrawable(mDrawerArrow);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mFragmentManager = getSupportFragmentManager();

        setMainColors(
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary),
                Color.TRANSPARENT
        );
        setSecondaryColors(
                ContextCompat.getColor(getApplicationContext(), R.color.colorAccent),
                ContextCompat.getColor(getApplicationContext(), R.color.colorAccentDark)
        );

        mDrawerToggle.syncState();

        mCurrentFragmentId = 0;
        menuLayout = 0;
        onLoadCurrentFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (menuLayout == 0) {
            return true;
        }
        getMenuInflater().inflate(menuLayout, menu);
        return true;
    }

    public void setMainColors(Integer toolbar, Integer statusBar) {
        toolbarPrimaryColor = toolbar;
        statusBarPrimaryColor = statusBar;
    }

    public void setSecondaryColors(Integer toolbar, Integer statusBar) {
        toolbarAccentColor = toolbar;
        statusBarAccentColor = statusBar;
    }

    public void onPrepareAnimateToolbarChangeAnimation(Integer colorFrom, Integer colorTo, Integer colorStatusFrom, Integer colorStatusTo){

        if(colorFrom != colorTo) {

            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            ValueAnimator colorStatusAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorStatusFrom, colorStatusTo);

            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mToolbar.setBackgroundColor((Integer) animator.getAnimatedValue());


                }
            });

            colorAnimation.setDuration(3000);
            colorAnimation.setStartDelay(0);
            colorAnimation.start();
        }

        if( colorStatusFrom != colorStatusTo) {
            // Something is going very wrong here


            if (menuLayout == R.menu.menu_main_inventory) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                ValueAnimator colorStatusAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorStatusFrom, colorStatusTo);
                colorStatusAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                    }
                });
                /*
                colorStatusAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (menuLayout == R.menu.menu_main_inventory) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        }
                    }
                });
                */
                colorStatusAnimation.setDuration(3000);
                colorStatusAnimation.setStartDelay(0);
                colorStatusAnimation.start();
            }

        }

    }


    public void onPrepareUpdateToolbar(){
        final ActionBar actionBar = getSupportActionBar();

        Integer colorFrom = ((ColorDrawable) findViewById(R.id.toolbar).getBackground()).getColor(); //Get the current toolbar color
        Integer colorTo = ((ColorDrawable) findViewById(R.id.toolbar).getBackground()).getColor();
        Integer colorStatusFrom = getWindow().getStatusBarColor();
        Integer colorStatusTo = getWindow().getStatusBarColor();

        if(menuLayout != 0) {

            if (menuLayout != R.menu.menu_main_delete) {

                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDrawer.openDrawer(GravityCompat.START);
                    }
                });

                ObjectAnimator.ofFloat(mDrawerArrow, "progress", 0).start();
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


                colorTo = toolbarPrimaryColor;

                colorStatusTo = statusBarPrimaryColor;

                mFab.show();

            } else {

                // We are in delete mode

                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                ObjectAnimator.ofFloat(mDrawerArrow, "progress", 1).start(); // Make draw arrow switch to back position

                colorTo = toolbarAccentColor;

                colorStatusTo = statusBarAccentColor;

                mFab.hide();
            }
        }

        onPrepareAnimateToolbarChangeAnimation(colorFrom, colorTo, colorStatusFrom, colorStatusTo);
    }

    public void setMenuLayout(int newMenuLayout){
        menuLayout = newMenuLayout;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        Log.v(TAG, "Prepareing menu options");
        menu.clear(); // Clear the old menu

        onPrepareUpdateToolbar(); // Change the color of the new toolbar if required
        onCreateOptionsMenu(menu);

        return true;
    }

    public MyInventoryFragment getCurrentFragmentInstance(){
        switch(mCurrentFragmentId){
            case INVENTORY_FRAGMENT_MENU_ID:
                return new InventoryFragment();
        }
        return null;
    }

    public String getCurrentFragmentTitle(){
        // Return the title for the current fragment

        return mNavigationView.getMenu().getItem(mCurrentFragmentId).getTitle().toString();
    }

    public void updateSelectedNavMenuItem(){
        // Updates the Nav Bar with the correct selected index
        mNavigationView.getMenu().getItem(mCurrentFragmentId).setChecked(true);
    }

    private void updateToolbarTitleForFragment() {

        Log.v(TAG, "Setting title to: " + getCurrentFragmentTitle());
        getSupportActionBar().setTitle(getCurrentFragmentTitle());
    }

    private void onLoadCurrentFragment() {
        // selecting appropriate nav menu item
        updateSelectedNavMenuItem();

        // set toolbar title
        updateToolbarTitleForFragment();

        // if user select the current navigation menu again or no fragment exists for that id, don't do anything
        // just close the navigation drawer

        if (mFragmentManager.findFragmentByTag(getCurrentFragmentTitle()) != null || getCurrentFragmentInstance() == null){
            mDrawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        mCurrentFragment = getCurrentFragmentInstance();

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_activity_content_layout, mCurrentFragment, getCurrentFragmentTitle());
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        mDrawer.closeDrawers();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);

        } else if(mCurrentFragment != null) {
            if (!mCurrentFragment.onBackPressed()) { // If the fragment didn't do anything with the press
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        if(mCurrentFragment.onOptionsItemSelected(item)) { //The fragment also needs to know that a menu item was selected
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_inventory:
                mCurrentFragmentId = INVENTORY_FRAGMENT_MENU_ID;
                break;

            case R.id.nav_meals:
                mCurrentFragmentId = MEALS_FRAGMENT_MENU_ID;
                break;

            case R.id.nav_manage:
                mDrawer.closeDrawer(GravityCompat.START);
                return true;
        }

        onLoadCurrentFragment();
        return true;


    }
}