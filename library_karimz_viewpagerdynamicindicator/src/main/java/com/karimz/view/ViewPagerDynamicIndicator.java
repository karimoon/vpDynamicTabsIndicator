package com.karimz.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.karimz.library.viewpagerindicator.R;

import java.util.List;


public class ViewPagerDynamicIndicator extends LinearLayout
{

	private Paint mPaint;

	private Path mPath;

	private int mTranslationX;

	private int mTabVisibleCount;

	private static final int COUNT_DEFAULT_TAB = 4;


	private String mColorTextNormal;
	private String mColorTextHighlight ;


	private List<String> mTitles;

	public ViewPagerDynamicIndicator(Context context)
	{
		this(context, null);
	}

	public ViewPagerDynamicIndicator(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray c = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerIndicator);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerIndicatorColor);

		TypedArray b = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerIndicatorHighlightColor);

		mTabVisibleCount = c.getInt(
				R.styleable.ViewPagerIndicator_visible_tab_count,
				COUNT_DEFAULT_TAB);

		mColorTextNormal = a.getString(
				R.styleable.ViewPagerIndicatorColor_color_text_normal) +"";

		mColorTextHighlight = b.getString(
				R.styleable.ViewPagerIndicatorHighlightColor_color_text_highlight) +"";


		if (mTabVisibleCount < 0)
		{
			mTabVisibleCount = COUNT_DEFAULT_TAB;
		}
		a.recycle();

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.parseColor("#ffffffff"));
		mPaint.setStyle(Style.FILL);
		mPaint.setPathEffect(new CornerPathEffect(3));

	}

	@Override
	protected void dispatchDraw(Canvas canvas)
	{

		canvas.save();

		canvas.translate( mTranslationX, getHeight() + 2);
		canvas.drawPath(mPath, mPaint);

		canvas.restore();

		super.dispatchDraw(canvas);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		initPath();
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();

		int cCount = getChildCount();
		if (cCount == 0)
			return;

		for (int i = 0; i < cCount; i++)
		{
			View view = getChildAt(i);
			LayoutParams lp = (LayoutParams) view
					.getLayoutParams();
			lp.weight = 0;
			lp.width = getScreenWidth() / mTabVisibleCount;
			view.setLayoutParams(lp);
		}

		setItemClickEvent();

	}

	private int getScreenWidth()
	{
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	private void initPath()
	{

		mPath = new Path();
		mPath.moveTo(0, 0);
		mPath.close();

	}

	public void scroll(int position, float offset)
	{
		int tabWidth1 = getTabWidthByPosition(position);

		mTranslationX = (int) (tabWidth1 * (offset + position));

		if(getAccumulatedNextPositionsWidth(position) > getWidth()){
			this.scrollTo(getAccumulatedPreviousPositionsWidth(position)
					+ (int) (tabWidth1 * offset), 0);
		}

		invalidate();

	}

	public int getTabWidthByPosition(int position){

		return getMeasuredWidthOfTitle(mTitles.get(position));

		}

	public int getAccumulatedPreviousPositionsWidth(int position){

		int result= 0;

		for(int i =0 ; i < position ; i++ ){
			result = result + getMeasuredWidthOfTitle(mTitles.get(i));
		}

		return result;

	}

	public int getAccumulatedNextPositionsWidth(int position){

		int result= 0;

		for(int i =position ; i < mTitles.size() ; i++ ){
			result = result + getMeasuredWidthOfTitle(mTitles.get(i));
		}

		return result;

	}

	public void setTabItemTitles(List<String> titles)
	{
		if (titles != null && titles.size() > 0)
		{
			this.removeAllViews();
			mTitles = titles;
			for (String title : mTitles)
			{
				addView(generateTextView(title));
			}

			setItemClickEvent();
		}
	}

	public void setVisibleTabCount(int count)
	{
		mTabVisibleCount = count;
	}

	private View generateTextView(String title)
	{
		TextView tv = new TextView(getContext());
		LayoutParams lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.width= getMeasuredWidthOfTitle(title) ;
		tv.setText(title);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.parseColor(mColorTextNormal));

		tv.setLayoutParams(lp);
		return tv;
	}

	private int getMeasuredWidthOfTitle(String title){

		TextView textView = new TextView(getContext());
		textView.setText(title);
		textView.measure(0, 0);
		int w = textView.getMeasuredWidth() + 80;
		int h =textView.getMeasuredHeight();

		return w;

	}

	private ViewPager mViewPager;

	public interface PageOnchangeListener
	{
		public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels);

		public void onPageSelected(int position);

		public void onPageScrollStateChanged(int state);
	}

	public PageOnchangeListener mListener;

	public void setOnPageChangeListener(PageOnchangeListener listener)
	{
		this.mListener = listener;
	}

	public void setViewPager(ViewPager viewPager, int pos)
	{
		mViewPager = viewPager;
		mViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{

				if (mListener != null)
				{
					mListener.onPageSelected(position);
				}

				highLightTextView(position);
				if (position <= (mTabVisibleCount - 2))
					scrollTo(0, 0);

			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels)
			{
				scroll(position, positionOffset);
				if (mListener != null)
				{
					mListener.onPageScrolled(position, positionOffset,
							positionOffsetPixels);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{

				if (mListener != null)
				{
					mListener.onPageScrollStateChanged(state);
				}

			}
		});
		mViewPager.setCurrentItem(pos);
		highLightTextView(pos);
	}

	private void resetTextViewColor()
	{
		for (int i = 0; i < getChildCount(); i++)
		{
			View view = getChildAt(i);
			if (view instanceof TextView)
			{
				((TextView) view).setTextColor(Color.parseColor(mColorTextNormal));
				}
		}

	}

	private void highLightTextView(int pos)
	{
		resetTextViewColor();
		View view = getChildAt(pos);
		if (view instanceof TextView)
		{
			((TextView) view).setTextColor(Color.parseColor(mColorTextHighlight));
		}
	}

	private void setItemClickEvent()
	{
		int cCount = getChildCount();

		for (int i = 0; i < cCount; i++)
		{
			final int j = i;
			View view = getChildAt(i);

			view.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mViewPager.setCurrentItem(j);
				}
			});

		}

	}

}
