package com.Saalai.Salaitvapp.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.Saalai.Salaitvapp.Adapters.OnboardingAdapter;
import com.Saalai.Salaitvapp.Models.OnboardingItem;
import com.Saalai.Salaitvapp.R;
import java.util.ArrayList;
import java.util.List;
public class OnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private TextView btnNext;
    private OnboardingAdapter adapter;
    private List<OnboardingItem> onboardingItems;
    private TextView[] dots;
    LinearLayout layoutNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);


        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black)); // replace with your color
        }

        View rootView = findViewById(R.id.root_view);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && Build.VERSION.SDK_INT <= 34) {
            // For Android 11 to 14, explicitly disable edge-to-edge
            getWindow().setDecorFitsSystemWindows(true);

            if (rootView != null) {
                rootView.setOnApplyWindowInsetsListener((v, insets) -> {
                    v.setPadding(0, 0, 0, 0);
                    return insets;
                });
            }
        } else {
            // Pre-Android 12
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (rootView != null) {
                rootView.setFitsSystemWindows(true); // critical for proper layout
            }
        }





        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btnNext);
        layoutNext= findViewById(R.id.layoutNext);

        // Setup onboarding items
        onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem(
                R.drawable.b1,
                "Unlimited entertainment, one low price",
                "Everthing on Tharai"
        ));
        onboardingItems.add(new OnboardingItem(
                R.drawable.b2,
                "Download and watch offline",
                "No Internet Needed."
        ));
        onboardingItems.add(new OnboardingItem(
                R.drawable.b3,
                "Cancel Online anytime",
                "Join today , No reason to wait. "
        ));

        onboardingItems.add(new OnboardingItem(
                R.drawable.b4,
                "Watch Everwhere",
                "Stream on your phone, Tablet, Laptop, Tv and More."
        ));


        // Setup adapter
        adapter = new OnboardingAdapter(this, onboardingItems);
        viewPager.setAdapter(adapter);

        // Add dots indicator
        addDotsIndicator(0);

        // Button click listener
        layoutNext.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });

        // ViewPager page change listener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);


            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });


    }

    private void addDotsIndicator(int currentPosition) {
        dots = new TextView[onboardingItems.size()];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•"); // Using bullet character as dot
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(
                    i == currentPosition ? R.color.yellow : R.color.white));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);

        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }



}
