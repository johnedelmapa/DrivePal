package com.example.asus.drivepal;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    private ViewPager mslideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;

    private SliderAdapter sliderAdapter;

    private Button mNextBtn;
    private Button mPrevBtn;

    //private Button mFinishBtn;

    private int mCurrentPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mslideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        mNextBtn = (Button) findViewById(R.id.nextBtn);
        mPrevBtn = (Button) findViewById(R.id.prevBtn);

        sliderAdapter = new SliderAdapter(this);

        mslideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        mslideViewPager.addOnPageChangeListener(viewListener);

        //OnClickListeners
//        mNextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mslideViewPager.setCurrentItem(mCurrentPage+1);
//            }
//        });
//
//        mPrevBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mslideViewPager.setCurrentItem(mCurrentPage-1);
//            }
//        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mslideViewPager.getCurrentItem() == mDots.length-1) {
                    Toast.makeText(MainActivity.this, "Let's get started", Toast.LENGTH_LONG).show();
                    //Goto my intent
                    Intent myIntent= new Intent(MainActivity.this,WelcomeActivity.class);
                    startActivity(myIntent);
                } else {
                    mslideViewPager.setCurrentItem(mCurrentPage + 1);
                }
            }
        });

    }

    public void addDotsIndicator(int position){
        mDots = new TextView[3];
        mDotLayout.removeAllViews();


            for(int i = 0; i< mDots.length; i++){
                mDots[i] = new TextView(this);
                mDots[i].setText(Html.fromHtml("&#8226"));
                mDots[i].setTextSize(35);
                mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

                mDotLayout.addView(mDots[i]);
            }

            if(mDots.length > 0){
                mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
            }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = i;


            if(i == 0) {
                mNextBtn.setEnabled(true);
                mPrevBtn.setEnabled(false);
                mPrevBtn.setVisibility(View.INVISIBLE);

                mNextBtn.setText("Next");
                mNextBtn.setText("");
            } else if (i == mDots.length -1 ) {
                mNextBtn.setEnabled(true);
                mPrevBtn.setEnabled(true);
                mPrevBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("Finish");
                mPrevBtn.setText("Back");
            } else {
                mNextBtn.setEnabled(true);
                mPrevBtn.setEnabled(true);
                mPrevBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("Next");
                mPrevBtn.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };



}
