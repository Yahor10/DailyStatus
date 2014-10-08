package by.android.dailystatus.widget.customlistview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import by.android.dailystatus.R;
import by.android.dailystatus.model.GroupEvent;

public class AlphabetIndexerView extends LinearLayout implements
		OnTouchListener {

	private int HEIGHT_OF_SECTION, MAX_HEIGHT;
	private int numberOfSections, currentSection = -1;
	private float textSize = 14;
	private TextView currentTextView = null;
	private SectionIndexer sectionListener;

	public void setIndexerListener(SectionIndexer listener) {
		this.sectionListener = listener;
		updateAlphabet();
	}

	public AlphabetIndexerView(Context context) {
		super(context);
		init();
	}

	public AlphabetIndexerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AlphabetIndexerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init();
	}

	private void init() {
		this.setOrientation(VERTICAL);
		this.setGravity(Gravity.CENTER);
		this.setBackgroundResource(R.drawable.indexer_bg);
		this.setOnTouchListener(this);
		// this.setAlpha(0.5f);
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public void updateAlphabet() {
		if (this.sectionListener != null) {
			this.removeAllViews();
			Object[] alphabet = sectionListener.getSections();
			numberOfSections = alphabet.length;
			for (Object letter : alphabet) {
				TextView tv = new TextView(getContext());
				tv.setText(((GroupEvent) letter).nameGroup);
				tv.setTextColor(Color.WHITE);
				tv.setPadding(1, 0, 1, 0);
				tv.setTextSize(this.textSize);
				this.addView(tv, new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				((LinearLayout.LayoutParams) tv.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int curSect;
		float y;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			MAX_HEIGHT = this.getMeasuredHeight();
			HEIGHT_OF_SECTION = MAX_HEIGHT / numberOfSections;
			// this.setAlpha(1f);

			y = event.getY();
			curSect = -1;
			if (y >= 0 && y < MAX_HEIGHT) {
				curSect = (int) y / HEIGHT_OF_SECTION;
			}
			if (currentSection != curSect) {
				currentSection = curSect;
				sectionChanged(currentSection);
				if (currentSection < 0)
					return true;
				TextView tv = (TextView) this.getChildAt(currentSection);
				highlightNextVeiw(tv);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("AlphabetIndexerView", String.format(
					"Point on screen: %f, %f", event.getX(), event.getY()));
			y = event.getY();
			curSect = -1;
			if (y >= 0 && y <= MAX_HEIGHT) {
				curSect = (int) y / HEIGHT_OF_SECTION;
			}
			if (currentSection != curSect) {
				currentSection = curSect;
				sectionChanged(currentSection);
				if (currentSection < 0)
					return true;
				TextView tv = (TextView) this.getChildAt(currentSection);
				highlightNextVeiw(tv);
			}

			break;
		case MotionEvent.ACTION_UP:
			if (currentTextView != null)
				currentTextView.setTextColor(Color.WHITE);
			currentSection = -1;
			// this.setAlpha(0.5f);
			break;
		}
		return true;
	}

	private void highlightNextVeiw(TextView tv) {
		if (currentTextView != null) {
			currentTextView.setTextColor(Color.WHITE);
		}
		if (tv == null) {
			currentTextView = null;
			return;
		}
		tv.setTextColor(Color.YELLOW);
		currentTextView = tv;
	}

	private void sectionChanged(int section) {
		if (!(section < 0)) {
			if (sectionListener != null) {
				sectionListener.getPositionForSection(section);
			}
		}
	}
}
