package by.android.dailystatus.fragment;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import by.android.dailystatus.ChartsActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.orm.model.DayORM;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class YearFragment extends BaseChartsFragment {

	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();

	private GraphicalView mChartView;
	private View inflate;

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
		mChartView = ChartFactory.getPieChartView(getActivity(), mSeries,
				mRenderer);

		LinearLayout mainChartLayout = (LinearLayout) inflate
				.findViewById(R.id.main_chart_layout);
		RelativeLayout layoutWithEmptyView = (RelativeLayout) inflate
				.findViewById(R.id.view_for_empty_data);
		TextView emptyView = (TextView) inflate.findViewById(R.id.txt_empty);

		int year = getActivity().getIntent()
				.getIntExtra(ChartsActivity.YEAR, 0);

		chartName.setText("" + year);
		int badDaysCount = 0;
		List<DayORM> badDays = DayORM
				.getBadDaysByYear(applicationContext, year);
		if (badDays != null && !badDays.isEmpty()) {
			badDaysCount = badDays.size();
		}

		int goodDaysCount = 0;
		List<DayORM> goodDays = DayORM.getGoodDaysByYear(applicationContext,
				year);
		if (goodDays != null && !goodDays.isEmpty()) {
			goodDaysCount = goodDays.size();
		}

		if (badDaysCount == 0 && goodDaysCount == 0) {
			mainChartLayout.setVisibility(View.GONE);
			emptyView.setText(emptyView.getText() + " "
					+ getResources().getString(R.string.week_for_chart_act));

		} else {
			layoutWithEmptyView.setVisibility(View.GONE);
		}

		layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		mSeries.add("Bad", badDaysCount);

		SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
		mRenderer.addSeriesRenderer(renderer);

		mSeries.add("Good ", goodDaysCount);

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
		return new YearFragment();
	}

}
