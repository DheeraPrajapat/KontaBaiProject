package com.example.kontabaiproject.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kontabaiproject.Fragments.AcceptedRideFragment;
import com.example.kontabaiproject.Fragments.PendingRideFragment;
import com.example.kontabaiproject.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RelativeLayout relativeLayoutUser,relativeLayoutDriver;
    TextView bookRide,rideStatus;
    TabLayout tabLayout;

    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_pager);
        setUpTablayout();
        bookRide.setOnClickListener(view -> bookTheRide());
        rideStatus.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,UserRecentsBooking.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
    }
    private void initViews() {
        relativeLayoutDriver=findViewById(R.id.driverRelativeLayout);
        relativeLayoutUser=findViewById(R.id.userRelativeLayout);
        bookRide=findViewById(R.id.bookTheRide);
        viewPager=findViewById(R.id.view_pager);
        rideStatus=findViewById(R.id.requestStatus);
    }

    private void getAndSetData(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("AllUsers").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String type=snapshot.child("Saved").getValue().toString();
                    if(type.equals("user")){
                        relativeLayoutUser.setVisibility(View.VISIBLE);
                        relativeLayoutDriver.setVisibility(View.GONE);
                    }
                    if(type.equals("driver")){
                        relativeLayoutUser.setVisibility(View.GONE);
                        relativeLayoutDriver.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"failed :"+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpTablayout(){
        ViewPagerAdapter viewPagerAdapter= new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new AcceptedRideFragment(),"Accepted");
        viewPagerAdapter.addFragment(new PendingRideFragment(),"Pending");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void bookTheRide() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        getAndSetData();
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm)
        {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment,String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}