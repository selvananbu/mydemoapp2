package com.mydemoapp2;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;


public class MainActivity extends JSBundleManagerActivity  implements  JSBundleManager.Interface , DefaultHardwareBackBtnHandler, NavigationView.OnNavigationItemSelectedListener {


    private ReactInstanceManager mReactInstanceManager;
    private android.app.Fragment  mViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initializeNavigationDrawerWithToolBar();

        mReactInstanceManager =
                ((MainApplication) getApplication()).getReactNativeHost().getReactInstanceManager();

        mViewFragment = new HelloFragment();
        getFragmentManager().beginTransaction().add(R.id.container, mViewFragment).commit();
    }

    private void initializeNavigationDrawerWithToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    //Over ride Methods..

    public static String getProductName() {
        return "LIS.helloworld";
    }

    public static String getDevice() {
        return "android";
    }

    public static String getUpdateFileName() {
        return "update.json";
    }

    public static String getMetaDataURL() {
        // return "http://ed0d4f2b.ngrok.io";
        return "http://10.128.1.2:6030";

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(updater == null) {
            updater = getBundleManager(getApplicationContext());

        }
        updater.setParentActivity(this);
        if(!_mAlreadyUpdated) {
            updater.checkForUpdates();
        }
    }


    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause();
        }
    }

    /*
     * Same as onPause - need to call onHostResume
     * on our ReactInstanceManager
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
            _mAlreadyUpdated = false;
        }
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
        getMenuInflater().inflate(R.menu.test_navigation, menu);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_connection) {

            Intent newconnectionIntent = new Intent(this,ListConnectionActivity.class);
            startActivity(newconnectionIntent);

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
