package by.android.dailystatus.widget.container;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import by.android.dailystatus.R;

public class EventLayout extends LinearLayout {

	public EventLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void addEventView(Context context, String description) {

		android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout layoutEvent = new LinearLayout(context);
		layoutEvent.setGravity(Gravity.LEFT | Gravity.CENTER);
		layoutEvent.setLayoutParams(layoutParams);

		ImageView image = new ImageView(context);
		image.setImageResource(R.drawable.ic_star_full);
		image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		LinearLayout.LayoutParams layoutParamsText = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParamsText.setMargins(10, 0, 0, 0);
		TextView textView = new TextView(context);
		textView.setLayoutParams(layoutParamsText);
		textView.setText(description);
		
		layoutEvent.addView(image,0);
		layoutEvent.addView(textView,1);
		layoutEvent.setBackgroundResource(android.R.drawable.list_selector_background);
		layoutEvent.setClickable(true);

		View separatorDown = new View(context);
		android.view.ViewGroup.LayoutParams layoutParamsSeparator = new LayoutParams(
				LayoutParams.MATCH_PARENT, 1);
		separatorDown.setLayoutParams(layoutParamsSeparator);
		separatorDown.setBackgroundColor(Color.BLACK);

		addView(layoutEvent,0);
		addView(separatorDown, 1);
	}

}
