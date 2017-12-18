package csci3310gp10.cusocmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    Menu navigationMenu = null;
    MenuItem loginOption = null;
    MenuItem logoutOption = null;
    MenuItem memberListOption = null;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor preferenceEditor;

    Boolean hasLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationMenu = navigationView.getMenu();
        loginOption = navigationMenu.findItem(R.id.nav_Login);
        logoutOption = navigationMenu.findItem(R.id.nav_Logout);
        memberListOption = navigationMenu.findItem(R.id.nav_member_list);

        //Get login information, change UI
        //preferenceEditor = sharedPreferences.edit();
        sharedPreferences = getApplicationContext().
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        setLoginStatus(sharedPreferences.getBoolean(getString(R.string.preference_user_has_login), false));
        changeUIOnLoginStatus();

        //Set the fragment initially
        NewsFragment fragment = new NewsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            //Set the fragment initially
            NewsFragment fragment = new NewsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            // Handle the camera action
        }
        else if (id == R.id.nav_events) {
            EventsFragment fragment = new EventsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
        else if (id == R.id.nav_feedback) {
            FeedBackFragment fragment = new FeedBackFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
//        else if (id == R.id.nav_settings) {
//
//        }
        else if (id == R.id.nav_Login) {
            LoginFragment fragment = new LoginFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
        else if (id == R.id.nav_Logout) {
            if (hasLogin) {
                sharedPreferences = getApplicationContext().
                        getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                preferenceEditor = sharedPreferences.edit();
                preferenceEditor.putBoolean(getString(R.string.preference_user_has_login), false);
                preferenceEditor.apply();
                setLoginStatus(false);
                Toast.makeText(getApplicationContext(), "Logout successfully", Toast.LENGTH_SHORT).show();
                changeUIOnLoginStatus();
            }
        }
        else if (id == R.id.nav_member_list) {
            MemberListFragment fragment = new MemberListFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setLoginStatus(Boolean hasLogin) {
        this.hasLogin = hasLogin;
    }

    public boolean getLoginStatus() {
        return hasLogin;
    }

    public void changeUIOnLoginStatus() {
        if (hasLogin) {
            loginOption.setVisible(false);
            logoutOption.setVisible(true);
            memberListOption.setVisible(true);
        }
        else {
            loginOption.setVisible(true);
            logoutOption.setVisible(false);
            memberListOption.setVisible(false);
        }
    }
}
