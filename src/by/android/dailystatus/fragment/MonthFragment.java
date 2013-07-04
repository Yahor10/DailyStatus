package by.android.dailystatus.fragment;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import by.android.dailystatus.R;

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
		mChartView = ChartFactory.getPieChartView(applicationContext, mSeries,
				mRenderer);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		layout.addView(mChartView, params);

		mSeries.add("Bad" + (mSeries.getItemCount() + 1), 5);

		SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
		mRenderer.addSeriesRenderer(renderer);

		mSeries.add("Good " + (mSeries.getItemCount() + 1), 17);

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
