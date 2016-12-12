package com.errorstation.christmassms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.google.firebase.crash.FirebaseCrash;
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
  private FirebaseAnalytics mFirebaseAnalytics;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    navigationView.setItemIconTintList(null);
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    smsLV = (ListView) findViewById(R.id.smsLV);
    progressBar = (ProgressBar) findViewById(R.id.progressBar);
    Realm.init(this);
    realm = Realm.getDefaultInstance();

    MobileAds.initialize(getApplicationContext(), "ca-app-pub-4958954259926855~4931623724");
    AdView mAdView = (AdView) findViewById(R.id.bannerADV);
    AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice("EB7E6FA39C4BDD75B5A17F5285A52364")
        .build();
    mAdView.loadAd(adRequest);
    if (isInternetAvailable(this)) {
      showSMS("-1");
      navigationView.getMenu().getItem(0).setChecked(true);
      getSupportActionBar().setTitle(Html.fromHtml(
          "<font color=\"#FFFFFF\">" + getResources().getString(R.string.featured) + "</font>"));
    }
    else
    {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Attention");
      builder.setMessage("Internet is not available. Please enable Internet connection to use this app!");
      builder.setNegativeButton("Exit App",new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          //do things
          finish();
        }
      });
      AlertDialog dialog = builder.create();
      dialog.setCancelable(false);
      dialog.show();
    }
  }

  public static boolean isInternetAvailable(Context context) {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
  }

  private void showShortlistedSMS() {
    smsLV.setVisibility(View.GONE);
    List<Sm> smss = new ArrayList<>();
    smss.clear();
    RealmResults<SMSDB> sms = realm.where(SMSDB.class).findAll();
    if (sms.size() > 0) {
      for (int i = 0; i < sms.size(); i++) {
        Sm sm = new Sm();
        sm.setTitle(sms.get(i).getTitle());
        sm.setId(sms.get(i).getId());
        sm.setDescription(sms.get(i).getDetails());

        smss.add(i, sm);
      }

      SMSAdapter smsAdapter = new SMSAdapter(MainActivity.this, smss);
      smsLV.setAdapter(smsAdapter);
      smsLV.setVisibility(View.VISIBLE);
    } else {
      smsLV.setVisibility(View.GONE);
    }
  }

  private void showSMS(String s) {
    try {
      smsLV.setVisibility(View.GONE);
      progressBar.setVisibility(View.VISIBLE);
      SMSApi.Factory.getInstance().getFeaturedSMS(s).enqueue(new Callback<SMS>() {
        @Override public void onResponse(Call<SMS> call, Response<SMS> response) {
          sms.clear();
          sms = response.body().getSms();
          SMSAdapter smsAdapter = new SMSAdapter(MainActivity.this, sms);
          smsLV.setAdapter(smsAdapter);
          progressBar.setVisibility(View.GONE);
          smsLV.setVisibility(View.VISIBLE);
        }

        @Override public void onFailure(Call<SMS> call, Throwable t) {

        }
      });
    }catch (RuntimeExecutionException e)
    {
      FirebaseCrash.report(e);
    }
  }

  @Override public void onBackPressed() {
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

  @SuppressWarnings("StatementWithEmptyBody") @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    if (id == R.id.featured) {
      showSMS("-1");
      getSupportActionBar().setTitle(Html.fromHtml(
          "<font color=\"#FFFFFF\">" + getResources().getString(R.string.featured) + "</font>"));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "-1");
      bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "featured");
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "category");
      mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
      // Handle the camera action
    } else if (id == R.id.inspiration) {
      showSMS("2");
      getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">"
          + getResources().getString(R.string.inspiration_and_blessings)
          + "</font>"));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2");
      bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "inspiration");
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "category");
      mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
      // Handle the camera action
    } else if (id == R.id.peace) {
      showSMS("1");
      getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">"
          + getResources().getString(R.string.peace_and_happiness)
          + "</font>"));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
      bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "peace");
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "category");
      mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    } else if (id == R.id.thanks) {
      showSMS("3");
      getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">"
          + getResources().getString(R.string.thanks_giving)
          + "</font>"));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "3");
      bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "thanks");
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "category");
      mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    } else if (id == R.id.love) {
      showSMS("4");
      getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">"
          + getResources().getString(R.string.love_and_romance)
          + "</font>"));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "4");
      bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "love");
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "category");
      mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    } else if (id == R.id.friend) {

      showSMS("5");
      getSupportActionBar().setTitle(Html.fromHtml(
          "<font color=\"#FFFFFF\">" + getResources().getString(R.string.friendship) + "</font>"));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "5");
      bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "friend");
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "category");
      mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    } else if (id == R.id.shortListed) {

      showShortlistedSMS();
      getSupportActionBar().setTitle(Html.fromHtml(
          "<font color=\"#FFFFFF\">" + getResources().getString(R.string.shortlisted) + "</font>"));
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "6");
      bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "shortListed");
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "category");
      mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}
