package uk.ac.coventry.bello.myinventory.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;

import uk.ac.coventry.bello.myinventory.fragments.InventoryFragment;
import uk.ac.coventry.bello.myinventory.R;
import uk.ac.coventry.bello.myinventory.fragments.MealsFragment;
import uk.ac.coventry.bello.myinventory.inventory.Inventory;
import uk.ac.coventry.bello.myinventory.fragments.templates.MyInventoryFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    public static final int INVENTORY_FRAGMENT_MENU_ID = 0;
    public static final int MEALS_FRAGMENT_MENU_ID = 1;

    private Integer menuLayout;
    private FloatingActionButton mFab;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private DrawerArrowDrawable mDrawerArrow;
    private NavigationView mNavigationView;

    private MyInventoryFragment mCurrentFragment; //Instance of the currently held fragment
    private Integer mCurrentFragmentId; // Fragment id based on menu
    private FragmentManager mFragmentManager;

    private Handler mHandler;

    private Integer toolbarPrimaryColor;
    private Integer toolbarAccentColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Inventory.getInstance().load(getApplicationContext()); // Make sure that the inventory is loaded

        mHandler = new Handler();

        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mToolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        setUpNavDrawer();

        mFragmentManager = getSupportFragmentManager();
        mCurrentFragmentId = 0; // Default is "Inventory" fragment
        menuLayout = 0;

        setFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        onLoadCurrentFragment();
    }

    public void setFabOnClickListener(View.OnClickListener listener) {
        mFab.setOnClickListener(listener);
    }

    public void setUpNavDrawer() {

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

        mDrawerToggle.syncState();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState hpas occurred.
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

    public void setAppBarColor(Integer toolbar) {
        toolbarPrimaryColor = toolbar;
    }

    public void setDeleteModeAppBarColor(Integer toolbar) {
        toolbarAccentColor = toolbar;
    }

    public void onPrepareAnimateToolbarChangeAnimation(Integer colorFrom, Integer colorTo) {
        /*
        Called to animate a colour change for the toolbar
         */

        if (colorFrom != colorTo) {

            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);

            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) { // every step update the colour
                    mToolbar.setBackgroundColor((Integer) animator.getAnimatedValue());
                    mDrawer.setStatusBarBackground(new ColorDrawable((Integer) animator.getAnimatedValue()));
                    // set status bar background colour keeps the status bar transparent
                }
            });

            colorAnimation.setDuration(150); // duration MS
            colorAnimation.setStartDelay(0);
            colorAnimation.start();
        }
    }


    public void onPrepareUpdateToolbar() {

        Integer colorFrom = ((ColorDrawable) findViewById(R.id.main_activity_toolbar).getBackground()).getColor(); // Get the current toolbar color
        Integer colorTo = colorFrom; // Set the default new color to the old colour

        if (menuLayout != 0) { // If a menulayout has been set

            if (menuLayout != R.menu.menu_main_inventory_highlight && menuLayout != R.menu.menu_main_delete) {
                // Set the default colours if we are not these 2 types of menu

                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDrawer.openDrawer(GravityCompat.START); // Set the drawer to open when the hambuger menu is clicked
                    }
                });

                ObjectAnimator.ofFloat(mDrawerArrow, "progress", 0).start();

                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); // Make the drawer swipeable again

                colorTo = toolbarPrimaryColor;

                mFab.show(); // FAB load animation

            } else {

                // We are in delete mode

                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed(); // set the arrow to do the same as the back button
                    }
                });

                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); // Make the drawer unopenable

                ObjectAnimator.ofFloat(mDrawerArrow, "progress", 1).start(); // Make draw arrow switch to back position

                colorTo = toolbarAccentColor;

                mFab.hide(); // Fab hide animation
            }
        }

        onPrepareAnimateToolbarChangeAnimation(colorFrom, colorTo);
    }

    public void setMenuLayout(int newMenuLayout) {
        menuLayout = newMenuLayout;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear(); // Clear the old menu

        onPrepareUpdateToolbar(); // Change the color of the new toolbar if required
        onCreateOptionsMenu(menu);

        return true;
    }

    public void setCurrentFragmentId(int i) {
        mCurrentFragmentId = i;
        onLoadCurrentFragment();
    }

    public MyInventoryFragment getNewCurrentFragmentInstance() {
        switch (mCurrentFragmentId) {

            case INVENTORY_FRAGMENT_MENU_ID:
                return new InventoryFragment();

            case MEALS_FRAGMENT_MENU_ID:
                return new MealsFragment();
        }
        return null;
    }

    public MyInventoryFragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public String getCurrentFragmentTitle() {
        // Return the title for the current fragment

        return mNavigationView.getMenu().getItem(mCurrentFragmentId).getTitle().toString();
    }

    public void updateSelectedNavMenuItem() {
        // Updates the Nav Bar with the correct selected index
        mNavigationView.getMenu().getItem(mCurrentFragmentId).setChecked(true);
    }

    private void updateToolbarTitleForFragment() {
        getSupportActionBar().setTitle(getCurrentFragmentTitle());
    }

    private void onLoadCurrentFragment() {
        // selecting appropriate nav menu item
        updateSelectedNavMenuItem();

        // set toolbar title
        updateToolbarTitleForFragment();

        // if user select the current navigation menu again or no fragment exists for that id, don't do anything
        // just close the navigation drawer

        if (mFragmentManager.findFragmentByTag(getCurrentFragmentTitle()) != null || getNewCurrentFragmentInstance() == null) {
            mCurrentFragment = (MyInventoryFragment) mFragmentManager.findFragmentByTag(getCurrentFragmentTitle());
            mDrawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app

        mCurrentFragment = getNewCurrentFragmentInstance();

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

        mHandler.post(mPendingRunnable);

        mDrawer.closeDrawers();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);

        } else if (mCurrentFragment != null) {
            if (!mCurrentFragment.onBackPressed()) { // If the fragment didn't do anything with the press
                if (mCurrentFragmentId != INVENTORY_FRAGMENT_MENU_ID) {
                    setCurrentFragmentId(INVENTORY_FRAGMENT_MENU_ID);
                } else {
                    super.onBackPressed();
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        if (mCurrentFragment.onOptionsItemSelected(item)) { //The fragment also needs to know that a menu item was selected
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_inventory:
                setCurrentFragmentId(INVENTORY_FRAGMENT_MENU_ID);
                return true;

            case R.id.nav_meals:
                setCurrentFragmentId(MEALS_FRAGMENT_MENU_ID);
                return true;

            case R.id.nav_manage:
                mDrawer.closeDrawer(GravityCompat.START);
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }
        return false;
    }
}
