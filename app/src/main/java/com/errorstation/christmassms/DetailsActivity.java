package com.errorstation.christmassms;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.google.firebase.crash.FirebaseCrash;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

  private static final String TAG = "DetailsActivity";
  Toolbar dToolbar;
  Realm realm;
  boolean shortlisted = false;
  CoordinatorLayout activity_details;
  NativeExpressAdView adView;
  private FirebaseAnalytics mFirebaseAnalytics;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);

    dToolbar = (Toolbar) findViewById(R.id.dToolbar);
    final FloatingActionButton shareFAB = (FloatingActionButton) findViewById(R.id.share);
    final FloatingActionButton shortListFAB = (FloatingActionButton) findViewById(R.id.shortList);
    setSupportActionBar(dToolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
    upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    getSupportActionBar().setHomeAsUpIndicator(upArrow);

    activity_details = (CoordinatorLayout) findViewById(R.id.activity_details);
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    MobileAds.initialize(getApplicationContext(), "ca-app-pub-4958954259926855~4931623724");
    adView = (NativeExpressAdView) findViewById(R.id.adView);
    AdRequest request = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice("EB7E6FA39C4BDD75B5A17F5285A52364")
        .build();
    adView.loadAd(request);

    Realm.init(this);
    realm = Realm.getDefaultInstance();

    Intent intent = getIntent();
    int count = intent.getIntExtra("background", 0);
    final String details = intent.getStringExtra("details");
    final String title = intent.getStringExtra("title");
    final String id = intent.getStringExtra("id");

    // getSupportActionBar().setTitle(title);
  /*  Iterable<String> result = Splitter.fixedLength(25).split(title);
    String[] parts = Iterables.toArray(result, String.class);
      Log.d(TAG, "onCreate: "+parts[0]);*/
    getSupportActionBar().setTitle(Html.fromHtml(
        "<font color=\"#FFFFFF\" style=\"font-family: Roboto Mono\">" + title + "..." + "</font>"));

    SMSDB sms = realm.where(SMSDB.class).equalTo("id", id).findFirst();

    if (sms != null) {
      shortlisted = true;
      shortListFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_all_black_24dp));
    }

    View view = findViewById(R.id.detailsView);
    TextView detailsTV = (TextView) findViewById(R.id.detailsTV);
    Typeface typeFace = Typeface.createFromAsset(getAssets(), "RobotoMonoRegular.ttf");
    detailsTV.setTypeface(typeFace);
    detailsTV.setText(details);
    try {

      SMSApi.Factory.getInstance().reportView(id).enqueue(new Callback<SMS>() {
        @Override public void onResponse(Call<SMS> call, Response<SMS> response) {
          Log.d(TAG, "onResponse: Success = "+response.body().getSuccess());
        }

        @Override public void onFailure(Call<SMS> call, Throwable t) {

        }
      });
    } catch (RuntimeExecutionException e) {
      FirebaseCrash.report(e);
    }

    Bundle bundle = new Bundle();
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, details);
    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "sms");
    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

    if (count == 0) {
      view.setBackground(getResources().getDrawable(R.drawable.ic_color_1_big));
    } else if (count == 1) {
      view.setBackground(getResources().getDrawable(R.drawable.ic_color_2_big));
    } else if (count == 2) {
      view.setBackground(getResources().getDrawable(R.drawable.ic_color_3_big));
    } else if (count == 3) {
      view.setBackground(getResources().getDrawable(R.drawable.ic_color_4_big));
    } else if (count == 4) {
      view.setBackground(getResources().getDrawable(R.drawable.ic_color_5_big));
    } else if (count == 5) {
      view.setBackground(getResources().getDrawable(R.drawable.ic_color_6_big));
    }

    shareFAB.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        tapAnimation(shareFAB);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, details);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "sms");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, details);
        startActivity(Intent.createChooser(sharingIntent, "Send SMS via"));
      }
    });
    shortListFAB.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        tapAnimation(shortListFAB);
        if (!shortlisted) {
          realm.beginTransaction();
          SMSDB smsdb = realm.createObject(SMSDB.class);
          smsdb.setId(id);
          smsdb.setDetails(details);
          smsdb.setTitle(title);
          realm.commitTransaction();
          shortListFAB.setImageDrawable(
              getResources().getDrawable(R.drawable.ic_done_all_black_24dp));
          shortlisted = true;
          Snackbar snackbar = Snackbar.make(activity_details,
              "SMS shortlisted! You can now find this sms in 'SHORTLISTED' category.",
              Snackbar.LENGTH_LONG);

          snackbar.show();
          Bundle bundle = new Bundle();
          bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
          bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, details);
          bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "sms");
          mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, bundle);
        } else if (shortlisted) {

          RealmResults<SMSDB> sms = realm.where(SMSDB.class).equalTo("id", id).findAll();
          realm.beginTransaction();
          sms.deleteAllFromRealm();
          realm.commitTransaction();
          shortListFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
          shortlisted = false;
          Snackbar snackbar =
              Snackbar.make(activity_details, "SMS removed from shortlist!", Snackbar.LENGTH_LONG);

          snackbar.show();
        }
      }
    });
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }
  public void onPause() {
    if (adView != null) {
      adView.pause();
    }
    super.onPause();
  }

  /** Called when returning to the activity */
  @Override
  public void onResume() {
    super.onResume();
    if (adView != null) {
      adView.resume();
    }
  }

  /** Called before the activity is destroyed */
  @Override
  public void onDestroy() {
    if (adView != null) {
      adView.destroy();
    }
    super.onDestroy();
  }
  public void tapAnimation(View view) {

    final Animation myAnim = AnimationUtils.loadAnimation(DetailsActivity.this, R.anim.bounce);

    // Use bounce interpolator with amplitude 0.2 and frequency 20
    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.08, 20);
    myAnim.setInterpolator(interpolator);
    view.startAnimation(myAnim);
  }

  class MyBounceInterpolator implements android.view.animation.Interpolator {
    double mAmplitude = 1;
    double mFrequency = 10;

    MyBounceInterpolator(double amplitude, double frequency) {
      mAmplitude = amplitude;
      mFrequency = frequency;
    }

    public float getInterpolation(float time) {
      return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
          Math.cos(mFrequency * time) + 1);
    }
  }
}
