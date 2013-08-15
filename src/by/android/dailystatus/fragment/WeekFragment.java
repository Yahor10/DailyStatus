package by.android.dailystatus.fragment;

import java.util.Calendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.joda.time.LocalDate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import by.android.dailystatus.ChartsActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.application.Constants;
import by.android.dailystatus.orm.model.DayORM;

public class WeekFragment extends BaseChartsFragment {

	private GraphicalView mChartView;
	private View inflate;

	CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	DefaultRenderer mRenderer = new DefaultRenderer();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		inflate = inflater.inflate(R.layout.xy_chart, null);
		return inflate;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);
		mRenderer.setZoomEnabled(false);
		mRenderer.setPanEnabled(false);
		mRenderer.setInScroll(true);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onResume() {

		LinearLayout layout = (LinearLayout) inflate.findViewById(R.id.chart);
		Context applicationContext = getActivity().getApplicationContext();
		mChartView = ChartFactory.getPieChartView(applicationContext, mSeries,
				mRenderer);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		layout.addView(mChartView, params);
		layout.setGravity(Gravity.CENTER);

		int weekDay = getActivity().getIntent().getIntExtra(
				ChartsActivity.WEEK, 0);
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.DAY_OF_YEAR, weekDay);
		
		LocalDate date = new LocalDate(instance.getTime());
		int year = date.getYear();
		short badDays = 0;
		short goodDays = 0;

		do {
			date = date.minusDays(1);
		} while (date.getDayOfWeek() != 1);
		
		for (int i = 0; i < 7; i++) {
			LocalDate plusDays = date.plusDays(i);
			DayORM day = DayORM.getDay(applicationContext,
					plusDays.getDayOfYear(), year);
			if (day != null && day.status == 1) {
				goodDays++;
				Log.v(Constants.TAG, "GOOD DAY" + day);
			} else if (day != null && day.status == -1) {
				badDays++;
				Log.v(Constants.TAG, "BAD DAY" + day);
			}
		}

		mSeries.add("Bad" + (mSeries.getItemCount() + 1), badDays);

		SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
		mRenderer.addSeriesRenderer(renderer);

		mSeries.add("Good " + (mSeries.getItemCount() + 1), goodDays);

		SimpleSeriesRenderer renderer2 = new SimpleSeriesRenderer();
		renderer2
				.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
		mRenderer.addSeriesRenderer(renderer2);

		mChartView.repaint();

		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);
		mRenderer.setZoomEnabled(false);
		mRenderer.setPanEnabled(false);
		mRenderer.setInScroll(true);
		super.onResume();
	}

	@Override
	public void onPause() {
		mSeries.clear();
		mRenderer.removeAllRenderers();
		super.onPause();
	}

	public static Fragment newInstance() {
		return new WeekFragment();
	}

}
