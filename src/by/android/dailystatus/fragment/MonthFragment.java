package by.android.dailystatus.fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.joda.time.LocalDate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import by.android.dailystatus.ChartsActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.application.Constants;
import by.android.dailystatus.orm.model.DayORM;

public class MonthFragment extends BaseChartsFragment {

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

		Context applicationContext = getActivity().getApplicationContext();
		LinearLayout layout = (LinearLayout) inflate.findViewById(R.id.chart);
		TextView chartName = (TextView) inflate.findViewById(R.id.chartName);

		LinearLayout mainChartLayout = (LinearLayout) inflate
				.findViewById(R.id.main_chart_layout);
		RelativeLayout layoutWithEmptyView = (RelativeLayout) inflate
				.findViewById(R.id.view_for_empty_data);
		TextView emptyView = (TextView) inflate.findViewById(R.id.txt_empty);

		mChartView = ChartFactory.getPieChartView(applicationContext, mSeries,
				mRenderer);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		layout.addView(mChartView, params);
		int month = getActivity().getIntent().getIntExtra(ChartsActivity.MONTH,
				0);
		int year = getActivity().getIntent()
				.getIntExtra(ChartsActivity.YEAR, 0);

		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.MONTH, month - 1);
		Date time = instance.getTime();
		LocalDate date = new LocalDate(time);
		chartName.setText(date.monthOfYear().getAsText());

		int badDaysCount = 0;
		List<DayORM> badDays = DayORM.getBadDaysByMonth(getActivity(), month,
				year);

		if (badDays != null && !badDays.isEmpty()) {
			badDaysCount = badDays.size();
		}

		int goodDaysCount = 0;
		List<DayORM> goodDays = DayORM.getGoodDaysByMonth(getActivity(), month,
				year);
		if (goodDays != null && !goodDays.isEmpty()) {
			goodDaysCount = goodDays.size();
		}

		if (badDaysCount == 0 && goodDaysCount == 0) {
			mainChartLayout.setVisibility(View.GONE);
			emptyView.setText(emptyView.getText() + " "
					+ getResources().getString(R.string.week_for_chart_act));
			layoutWithEmptyView.setVisibility(View.VISIBLE);

		} else {
			mainChartLayout.setVisibility(View.VISIBLE);
			layoutWithEmptyView.setVisibility(View.GONE);
		}

		mSeries.add("Bad" + (mSeries.getItemCount() + 1), badDaysCount);

		SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
		mRenderer.addSeriesRenderer(renderer);

		Log.v(Constants.TAG, "BAD" + badDaysCount);
		Log.v(Constants.TAG, "GOOD" + goodDaysCount);

		mSeries.add("Good " + (mSeries.getItemCount() + 1), goodDaysCount);

		SimpleSeriesRenderer renderer2 = new SimpleSeriesRenderer();
		renderer2
				.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
		mRenderer.addSeriesRenderer(renderer2);

		mChartView.repaint();

		super.onResume();
	}

	@Override
	public void onPause() {
		mSeries.clear();
		mRenderer.removeAllRenderers();
		super.onPause();
	}

	public static Fragment newInstance() {
		return new MonthFragment();
	}

}
