package by.android.dailystatus.fragment;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import by.android.dailystatus.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class YearFragment extends BaseChartsFragment {

	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();

	private GraphicalView mChartView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View inflate = inflater.inflate(R.layout.xy_chart, null);
		LinearLayout layout = (LinearLayout) inflate.findViewById(R.id.chart);
		mRenderer.removeAllRenderers();
		mChartView = ChartFactory.getPieChartView(getActivity(), mSeries,
				mRenderer);

		layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		mSeries.add("Bad" + (mSeries.getItemCount() + 1), 123);

		SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
		mRenderer.addSeriesRenderer(renderer);

		mSeries.add("Good " + (mSeries.getItemCount() + 1), 200);

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

		return inflate;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);

		super.onViewCreated(view, savedInstanceState);
	}

	public static Fragment newInstance() {
		return new YearFragment();
	}

}
