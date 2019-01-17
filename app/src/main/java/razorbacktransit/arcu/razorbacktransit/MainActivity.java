package razorbacktransit.arcu.razorbacktransit;

import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LiveMapFragment.OnFragmentInteractionListener,
        ParkingMapFragment.OnFragmentInteractionListener,
        SchedulesFragment.OnFragmentInteractionListener,
        RoutesFragment.OnFragmentInteractionListener,
        ViewScheduleFragment.OnFragmentInteractionListener,
        ViewRouteFragment.OnFragmentInteractionListener
{

    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    private LiveMapFragment liveMapFragment;
    private SchedulesFragment schedulesFragment;
    private RoutesFragment routesFragment;
    private ParkingMapFragment parkingMapFragment;
    private ViewScheduleFragment viewScheduleFragment;
    private ViewRouteFragment viewRouteFragment;
    private FirebaseAnalytics mFirebaseAnalytics;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    int lastMenuItemId = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        liveMapFragment = new LiveMapFragment();
        schedulesFragment = new SchedulesFragment();
        routesFragment = new RoutesFragment();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, liveMapFragment);
        fragmentTransaction.add(R.id.container, schedulesFragment);
        fragmentTransaction.add(R.id.container, routesFragment);
        fragmentTransaction.commit();

        navigationView.getMenu().getItem(0).setChecked(true);
        MenuItem menuItem = navigationView.getMenu().getItem(0);
        onNavigationItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (lastMenuItemId == id) {
            if (lastMenuItemId == navigationView.getMenu().getItem(1).getItemId() || lastMenuItemId == navigationView.getMenu().getItem(2).getItemId()) {
                lastMenuItemId = id;
            } else {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        } else {
            lastMenuItemId = id;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (id == R.id.nav_live_map) {

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Live Map");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tab Selected");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            fragmentTransaction.show(liveMapFragment);
            fragmentTransaction.hide(schedulesFragment);
            fragmentTransaction.hide(routesFragment);

            if (parkingMapFragment != null) {
                fragmentTransaction.hide(parkingMapFragment);
            }
            if (viewScheduleFragment != null) {
                fragmentTransaction.hide(viewScheduleFragment);
            }
            if (viewRouteFragment != null) {
                fragmentTransaction.hide(viewRouteFragment);
            }

            fragmentTransaction.commit();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.live_map_nav_bar_header);
            }

        } else if (id == R.id.nav_schedules) {

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Schedules");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tab Selected");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            fragmentTransaction.hide(liveMapFragment);
            fragmentTransaction.show(schedulesFragment);
            fragmentTransaction.hide(routesFragment);

            if (parkingMapFragment != null) {
                fragmentTransaction.hide(parkingMapFragment);
            }
            if (viewScheduleFragment != null) {
                fragmentTransaction.hide(viewScheduleFragment);
            }
            if (viewRouteFragment != null) {
                fragmentTransaction.hide(viewRouteFragment);
                fragmentManager.popBackStack();
            }

            fragmentTransaction.commit();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.schedules_nav_bar_header);
            }

        } else if (id == R.id.nav_routes) {

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Routes");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tab Selected");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            fragmentTransaction.hide(liveMapFragment);
            fragmentTransaction.hide(schedulesFragment);
            fragmentTransaction.show(routesFragment);

            if (parkingMapFragment != null) {
                fragmentTransaction.hide(parkingMapFragment);
            }

            if (viewScheduleFragment != null) {
                fragmentTransaction.hide(viewScheduleFragment);
                fragmentManager.popBackStack();
            }
            if (viewRouteFragment != null) {
                fragmentTransaction.hide(viewRouteFragment);
            }

            fragmentTransaction.commit();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.routes_nav_bar_header);
            }

        } else if (id == R.id.nav_parking) {

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Parking Map");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tab Selected");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            if (parkingMapFragment != null) {
                fragmentTransaction.show(parkingMapFragment);
            } else {
                parkingMapFragment = new ParkingMapFragment();
                fragmentTransaction.add(R.id.container, parkingMapFragment);
            }

            fragmentTransaction.hide(liveMapFragment);
            fragmentTransaction.hide(schedulesFragment);
            fragmentTransaction.hide(routesFragment);

            if (viewScheduleFragment != null) {
                fragmentTransaction.hide(viewScheduleFragment);
            }
            if (viewRouteFragment != null) {
                fragmentTransaction.hide(viewRouteFragment);
            }

            fragmentTransaction.commit();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.parking_map_nav_bar_header);
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(ViewScheduleFragment fragment, String title) {
        viewScheduleFragment = fragment;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, title);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Schedule Viewed");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        fragmentTransaction.add(R.id.container, viewScheduleFragment).addToBackStack(viewScheduleFragment.getClass().getSimpleName());
        fragmentTransaction.show(viewScheduleFragment);

        if (viewRouteFragment != null) {
            fragmentTransaction.hide(viewRouteFragment);
        }

        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(ViewRouteFragment fragment, String title) {
        viewRouteFragment = fragment;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, title);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "BusRoute Viewed");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        fragmentTransaction.add(R.id.container, viewRouteFragment).addToBackStack(viewRouteFragment.getClass().getSimpleName());
        fragmentTransaction.show(viewRouteFragment);

        if (viewScheduleFragment != null) {
            fragmentTransaction.hide(viewScheduleFragment);
        }

        fragmentTransaction.commit();
    }
}