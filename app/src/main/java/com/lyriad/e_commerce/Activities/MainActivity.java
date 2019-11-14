package com.lyriad.e_commerce.Activities;

import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.lyriad.e_commerce.R;
import com.lyriad.e_commerce.Sessions.UserSession;
import com.lyriad.e_commerce.Fragments.*;
import com.lyriad.e_commerce.Utils.FragmentNavigationManager;
import com.lyriad.e_commerce.Utils.Validator;
import com.mikhaellopez.circularimageview.CircularImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private UserSession session;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new UserSession(MainActivity.this);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        FragmentNavigationManager.initialize(MainActivity.this);

        DrawerLayout drawer = findViewById(R.id.home_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.inflateMenu(R.menu.main_drawer);
        navigationView.setCheckedItem(R.id.nav_home);

        View headerView = navigationView.getHeaderView(0);
        Glide.with(this).load(session.getImage()).into((CircularImageView) headerView.findViewById(R.id.nav_header_image));
        ((TextView) headerView.findViewById(R.id.nav_header_name)).setText(session.getName());
        ((TextView) headerView.findViewById(R.id.nav_header_username)).setText(session.getUsername());
        ((TextView) headerView.findViewById(R.id.nav_header_email)).setText(session.getEmail());

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.home_drawer_layout);

        if (!(getSupportFragmentManager().getFragments().get(0) instanceof HomeFragment)) {

            navigationView.setCheckedItem(R.id.nav_home);
            FragmentNavigationManager.getInstance().showFragment(new HomeFragment(), false);

        } else {

            if (drawer.isDrawerOpen(GravityCompat.START)) {

                drawer.closeDrawer(GravityCompat.START);

            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_search:
                break;

            case R.id.action_notifications:
                break;

            case R.id.action_cart:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        displaySelectedFragment(item.getItemId());
        return true;
    }

    private void displaySelectedFragment(int menuItemId) {

        if (menuItemId == R.id.nav_logout) {

            session.logoutUser();
            finish();

        } else {

            Fragment selectedFragment = null;

            switch (menuItemId) {

                case R.id.nav_home:

                    selectedFragment = new HomeFragment();
                    break;

                case R.id.nav_categories:

                    selectedFragment = new CategoryFragment();
                    break;

                case R.id.nav_products:

                    Bundle args = new Bundle();
                    args.putBoolean("all", true);
                    selectedFragment = new ProductsFragment();
                    selectedFragment.setArguments(args);
                    break;

                case R.id.nav_tracking:

                    selectedFragment = new TrackingFragment();
                    break;

                case R.id.nav_locations:

                    selectedFragment = new LocationFragment();
                    break;

                case R.id.nav_profile:

                    selectedFragment = new ProfileFragment();
                    break;

                case R.id.nav_help:

                    selectedFragment = new HelpFragment();
                    break;
            }

            FragmentNavigationManager.getInstance().showFragment(selectedFragment, false);
        }

        DrawerLayout drawer = findViewById(R.id.home_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
