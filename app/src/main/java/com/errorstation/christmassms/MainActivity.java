package com.errorstation.christmassms;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView smsLV;
    List<Sm> sms = new ArrayList<>();

    ProgressBar progressBar;
    Realm realm;
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
        navigationView.setItemIconTintList(null);

        smsLV = (ListView) findViewById(R.id.smsLV);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        showSMS("");


    }

    private void showShortlistedSMS()
    {
        List<Sm> smss = new ArrayList<>();
        smss.clear();
        RealmResults<SMSDB> sms = realm.where(SMSDB.class).findAll();
        Toast.makeText(this, "DB Size: "+String.valueOf(sms.size()), Toast.LENGTH_SHORT).show();
        if(sms.size()>0)
        {
            for (int i= 0; i<sms.size();i++)
            {
                Sm sm = new Sm();
                sm.setTitle(sms.get(i).getTitle());
                sm.setId(sms.get(i).getId());
                sm.setDescription(sms.get(i).getDetails());

                smss.add(i,sm);
            }
            SMSAdapter smsAdapter = new SMSAdapter(MainActivity.this, smss);
            smsLV.setAdapter(smsAdapter);

        }
        smsLV.setVisibility(View.GONE);
    }

    private void showSMS(String s) {
        progressBar.setVisibility(View.VISIBLE);
        SMSApi.Factory.getInstance().getFeaturedSMS(s).enqueue(new Callback<SMS>() {
            @Override
            public void onResponse(Call<SMS> call, Response<SMS> response) {
                progressBar.setVisibility(View.GONE);
                smsLV.setVisibility(View.VISIBLE);
                sms.clear();
                sms = response.body().getSms();
                SMSAdapter smsAdapter = new SMSAdapter(MainActivity.this, sms);
                smsLV.setAdapter(smsAdapter);

            }

            @Override
            public void onFailure(Call<SMS> call, Throwable t) {
                Toast.makeText(MainActivity.this, String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        });
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

    /*@Override
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
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.inspiration) {
            showSMS("");
            // Handle the camera action
        } else if (id == R.id.peace) {
            showSMS("");

        } else if (id == R.id.thanks) {
            showSMS("");

        } else if (id == R.id.love) {
            showSMS("");

        } else if (id == R.id.friend) {

            showShortlistedSMS();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
