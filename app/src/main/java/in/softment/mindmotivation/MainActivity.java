package in.softment.mindmotivation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import in.softment.mindmotivation.Fragment.ExploreFragment;
import in.softment.mindmotivation.Fragment.FavouritesFragment;
import in.softment.mindmotivation.Fragment.ProfileFragment;
import in.softment.mindmotivation.Fragment.RelaxFragment;
import in.softment.mindmotivation.Util.NonSwipeAbleViewPager;
import in.softment.mindmotivation.Util.Services;


public class MainActivity extends AppCompatActivity  {

    private TabLayout tabLayout;
    private NonSwipeAbleViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private  RelaxFragment relaxFragment;


    private final int[] tabIcons = {
            R.drawable.relax,
            R.drawable.explore,
            R.drawable.favourite,
            R.drawable.user,

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Services.fullScreen(this);
        relaxFragment = new RelaxFragment();

        //ViewPager
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(5);


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {

                    relaxFragment.pauseBreathe();
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons();

        viewPager.setCurrentItem(1);


    }

    public void setPagerItem(int item){
        viewPager.setCurrentItem(item);
    }

    private void setupTabIcons() {

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);


    }

    private void setupViewPager(ViewPager viewPager) {

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrag(relaxFragment);
        viewPagerAdapter.addFrag(new ExploreFragment());
        viewPagerAdapter.addFrag(new FavouritesFragment());
        viewPagerAdapter.addFrag(new ProfileFragment());
        viewPager.setAdapter(viewPagerAdapter);

    }

    static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {


            return mFragmentList.get(position);
        }

        @Override
        public int getItemPosition(@NonNull @NotNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "Relax";
                case 1 : return "Explore";
                case 2 : return "Favourites";
                case 3 : return "Profile";
                default:return "";
            }
        }

        public void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);

        }



    }




}


