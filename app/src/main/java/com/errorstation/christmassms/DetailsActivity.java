package com.errorstation.christmassms;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        int count = intent.getIntExtra("background",0);
        final String details = intent.getStringExtra("details");

        View view = findViewById(R.id.detailsView);
        TextView detailsTV = (TextView) findViewById(R.id.detailsTV);

        detailsTV.setText(details);

        if(count==0)
        {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_1));
        }
        else if (count==1)
        {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_2));
        }
        else if (count==2)
        {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_3));
        }
        else if (count==3)
        {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_4));
        }
        else if (count==4)
        {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_5));
        }
        else if (count==5)
        {
            view.setBackground(getResources().getDrawable(R.drawable.ic_color_6));
        }

        final FloatingActionButton shareFAB = (FloatingActionButton) findViewById(R.id.share);
        final FloatingActionButton shortListFAB = (FloatingActionButton) findViewById(R.id.shortList);
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
            }
        });
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
