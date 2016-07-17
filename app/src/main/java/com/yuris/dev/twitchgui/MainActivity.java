package com.yuris.dev.twitchgui;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.yuris.dev.kodiapi.workers.KodiWorker;
import com.yuris.dev.twitchgui.games.GamesFragment;
import com.yuris.dev.twitchgui.streams.StreamsFragment;
import com.yuris.dev.utils.AsyncTaskResult;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GamesFragment.OnGameSelectedListener,
        StreamsFragment.OnStreamSelectedListener,
        FollowingFragment.OnFollowedStreamSelectedListener {

    private boolean kodiIsIdle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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
        Fragment fragment = null;

        if (id == R.id.nav_browse) {
            fragment = new BrowseFragment().newInstance();
        } else if (id == R.id.nav_following) {
            fragment = new FollowingFragment().newInstance("Oyuret");
        } else if (id == R.id.nav_search) {
            fragment = new SearchFragment().newInstance("test1", "test2");
        }

        // Clear the backstack
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onGameSelected(String gameName) {
        BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);

        browseFragment.goToStreams(gameName);
    }

    @Override
    public void onStreamSelected(String streamName) {

        if(kodiIsIdle) {
            kodiIsIdle = false;

            KodiWorker playStream = new KodiWorker() {
                @Override
                protected void onPostExecute(AsyncTaskResult<String> stringAsyncTaskResult) {
                    if(stringAsyncTaskResult.hasError()) {
                        Log.e("Stream Failed", stringAsyncTaskResult.getError().getMessage());
                    } else {
                        Log.e("Stream", stringAsyncTaskResult.getResult());
                    }
                    kodiIsIdle = true;
                }
            };
            playStream.execute(new String[]{streamName});
        }

    }

    @Override
    public void onFollowedStreamSelected(String streamName) {
        if(kodiIsIdle) {
            kodiIsIdle = false;

            KodiWorker playStream = new KodiWorker() {
                @Override
                protected void onPostExecute(AsyncTaskResult<String> stringAsyncTaskResult) {
                    if(stringAsyncTaskResult.hasError()) {
                        Log.e("Stream Failed", stringAsyncTaskResult.getError().getMessage());
                    } else {
                        Log.e("Stream", stringAsyncTaskResult.getResult());
                    }
                    kodiIsIdle = true;
                }
            };
            playStream.execute(new String[]{streamName});
        }
    }
}
