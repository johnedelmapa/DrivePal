package com.example.asus.drivepal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    //Arrays
    public int[] slide_images = {
            R.mipmap.ic_group1_foreground,
            R.mipmap.ic_group2_foreground,
            R.mipmap.ic_group3_foreground


    };


    public String[] slide_headings = {
            "ROAD SAFETY PRECAUTIONS",
            "REPORT VIOLATION MECHANISM",
            "MINIMIZE DISTRACTIONS"
    };

    public String[]slide_desc = {
        "That guides user while driving, giving the users with warnings and alerts through screen display, vibration, or audio.",
            "For reporting of road violation detected by the application which is then sent to the admin site.",
            "By blocking all notifications from other applications of the phone."
    };


    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout) o;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slideImageView);
        TextView slideHeading = (TextView) view.findViewById(R.id.slideHeading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slideDescription);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_desc[position]);

        container.addView(view);

        return view;

    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
