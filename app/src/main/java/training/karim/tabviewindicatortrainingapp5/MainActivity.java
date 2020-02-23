package training.karim.tabviewindicatortrainingapp5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.karimz.view.ViewPagerDynamicIndicator;

public class MainActivity extends FragmentActivity
{
	private List<Fragment> mTabContents = new ArrayList<Fragment>();
	private FragmentPagerAdapter mAdapter;
	private ViewPager mViewPager;
	private List<String> mDatas = Arrays.asList("Content providers", "Kotlin", "java", "Rx",
			"Couroutines", "MVP", "OOP", "Broadcast recievers", "Room" ,"retrofit");

	private ViewPagerDynamicIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vp_indicator);

		initView();

//		mIndicator.setItemCount(4);
		mIndicator.setTabItemTitles(mDatas);
		initDatas();

		mViewPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mViewPager,0);

	}

	private void initDatas()
	{

		for (String data : mDatas)
		{
			VpSimpleFragment fragment = VpSimpleFragment.newInstance(data);
			mTabContents.add(fragment);
		}

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
		{
			@Override
			public int getCount()
			{
				return mTabContents.size();
			}

			@Override
			public Fragment getItem(int position)
			{
				return mTabContents.get(position);
			}
		};
	}

	private void initView()
	{
		mViewPager = (ViewPager) findViewById(R.id.id_vp);
		mIndicator = (ViewPagerDynamicIndicator) findViewById(R.id.id_indicator);
	}


}
