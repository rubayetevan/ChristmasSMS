package com.errorstation.christmassms;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class DetailsActivity extends AppCompatActivity {

    Toolbar dToolbar;
    Realm realm;
    boolean shortlisted = false;
    CoordinatorLayout activity_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        dToolbar = (Toolbar) findViewById(R.id.dToolbar);
        final FloatingActionButton shareFAB = (FloatingActionButton) findViewById(R.id.share);
        final FloatingActionButton shortListFAB = (FloatingActionButton) findViewById(R.id.shortList);
        setSupportActionBar(dToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        activity_details = (CoordinatorLayout) findViewById(R.id.activity_details);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();
        int count = intent.getIntExtra("background", 0);
        final String details = intent.getStringExtra("details");
        final String title = intent.getStringExtra("title");
        final String id = intent.getStringExtra("id");


        SMSDB sms = realm.where(SMSDB.class).equalTo("id", id).findFirst();


        if (sms != null) {
            shortlisted = true;
            shortListFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_all_black_24dp));
        }

        View view = findViewById(R.id.detailsView);
        TextView detailsTV = (TextView) findViewById(R.id.detailsTV);

        detailsTV.setText(details);

        if (count == 0) {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_1));
        } else if (count == 1) {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_2));
        } else if (count == 2) {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_3));
        } else if (count == 3) {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_4));
        } else if (count == 4) {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_5));
        } else if (count == 5) {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_6));
        }


        shareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tapAnimation(shareFAB);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, details);
                startActivity(Intent.createChooser(sharingIntent, "Send SMS via"));
            }
        });
        shortListFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tapAnimation(shortListFAB);
                if (!shortlisted) {
                    realm.beginTransaction();
                    SMSDB smsdb = realm.createObject(SMSDB.class);
                    smsdb.setId(id);
                    smsdb.setDetails(details);
                    smsdb.setTitle(title);
                    realm.commitTransaction();
                    shortListFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_all_black_24dp));
                    shortlisted=true;
                    Snackbar snackbar = Snackbar
                            .make(activity_details, "SMS shortlisted! You can now find this sms in 'SHORTLISTED' category.", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
                else if (shortlisted)
                {

                    RealmResults<SMSDB> sms = realm.where(SMSDB.class).equalTo("id", id).findAll();
                    realm.beginTransaction();
                    sms.deleteAllFromRealm();
                    realm.commitTransaction();
                    shortListFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                    shortlisted=false;
                    Snackbar snackbar = Snackbar
                            .make(activity_details, "SMS removed from shortlist!", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
