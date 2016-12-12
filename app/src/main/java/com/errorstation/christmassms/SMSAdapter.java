package com.errorstation.christmassms;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rubayet on 08-Dec-16.
 */

public class SMSAdapter extends ArrayAdapter {
  List<Sm> sms = new ArrayList<>();
  Context context;
  LayoutInflater inflater;
  private int lastPosition = -1;

  public SMSAdapter(Context context, List<Sm> sms) {
    super(context, R.layout.list_item, sms);
    this.context = context;
    this.sms = sms;
  }

  @NonNull @Override public View getView(final int position, View convertView, ViewGroup parent) {
    final ViewHolder smsHolder = new ViewHolder();

    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    convertView = inflater.inflate(R.layout.list_item, parent, false);

    smsHolder.headingTV = (TextView) convertView.findViewById(R.id.headingTV);
    smsHolder.backView = convertView.findViewById(R.id.backView);
    smsHolder.frameLayout = (FrameLayout) convertView.findViewById(R.id.frame);

    final int count = position % 6;

    smsHolder.headingTV.setText(sms.get(position).getTitle());
    Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "RobotoMonoBold.ttf");
    smsHolder.headingTV.setTypeface(typeFace);
    if (count == 0) {
      smsHolder.backView.setBackground(context.getResources().getDrawable(R.drawable.ic_color_1));
    } else if (count == 1) {
      smsHolder.backView.setBackground(context.getResources().getDrawable(R.drawable.ic_color_2));
    } else if (count == 2) {
      smsHolder.backView.setBackground(context.getResources().getDrawable(R.drawable.ic_color_3));
    } else if (count == 3) {
      smsHolder.backView.setBackground(context.getResources().getDrawable(R.drawable.ic_color_4));
    } else if (count == 4) {
      smsHolder.backView.setBackground(context.getResources().getDrawable(R.drawable.ic_color_5));
    } else if (count == 5) {
      smsHolder.backView.setBackground(context.getResources().getDrawable(R.drawable.ic_color_6));
    }
    smsHolder.headingTV.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("background", count);
        intent.putExtra("details", sms.get(position).getDescription());
        intent.putExtra("title", sms.get(position).getTitle());
        intent.putExtra("id", sms.get(position).getId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          Bundle bundle1 =
              ActivityOptions.makeSceneTransitionAnimation((Activity) context, smsHolder.backView,
                  smsHolder.backView.getTransitionName()).toBundle();
          context.startActivity(intent, bundle1);
        } else {
          context.startActivity(intent);
        }
      }
    });
    Animation animation = AnimationUtils.loadAnimation(getContext(),
        (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
    smsHolder.frameLayout.startAnimation(animation);
    lastPosition = position;

    return convertView;
  }

  public static class ViewHolder {
    TextView headingTV;
    View backView;
    FrameLayout frameLayout;
  }
}
