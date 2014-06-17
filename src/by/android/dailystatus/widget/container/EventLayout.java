package by.android.dailystatus.widget.container;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import by.android.dailystatus.R;
import by.android.dailystatus.application.Constants;
import by.android.dailystatus.newpopup.MenuItem;
import by.android.dailystatus.newpopup.PopupMenu;
import by.android.dailystatus.newpopup.PopupMenu.OnItemSelectedListener;
import by.android.dailystatus.orm.model.EventORM;

public class EventLayout extends LinearLayout implements OnItemSelectedListener {

	public EventLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void addEventView(final Context context, EventORM eventORM) {

		android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout layoutEvent = new LinearLayout(context);
		layoutEvent.setTag(eventORM);
		layoutEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EventORM eventORM = (EventORM) v.getTag();

				
				PopupMenu menu = new PopupMenu(context);
				menu.setHeaderTitle(eventORM.description);
				// Set Listener
				menu.setOnItemSelectedListener(EventLayout.this);

				// quickAction.setEvent(eventORM);
				// quickAction.show(v);
				// quickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);

				Resources resources = getResources();
				
				// TODO find resources
				menu.add(1, R.string.view,eventORM).setIcon(
						resources.getDrawable(R.drawable.ic_edit));
				menu.add(2, R.string.edit,eventORM).setIcon(
						resources.getDrawable(R.drawable.ic_edit));
				menu.add(3, R.string.delete,eventORM).setIcon(
						resources.getDrawable(android.R.drawable.ic_delete));
				menu.show(v);

			}
		});
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
		textView.setTextSize(20);
		// textView.setTextAppearance(context,
		// android.R.attr.textAppearanceMedium);
		textView.setText(eventORM.description);

		layoutEvent.addView(image, 0);
		layoutEvent.addView(textView, 1);
		layoutEvent
				.setBackgroundResource(android.R.drawable.list_selector_background);
		layoutEvent.setClickable(true);

		View separatorDown = new View(context);
		android.view.ViewGroup.LayoutParams layoutParamsSeparator = new LayoutParams(
				LayoutParams.MATCH_PARENT, 1);
		separatorDown.setLayoutParams(layoutParamsSeparator);
		separatorDown.setBackgroundColor(Color.BLACK);

		addView(layoutEvent, 0);
		addView(separatorDown, 1);
	}

	@Override
	public void onItemSelected(MenuItem item) {

		Log.v(Constants.TAG, "EVENT DESC" + item.getEventORM());
		switch (item.getItemId()) {
		case 1:
			break;

		default:
			break;
		}
	}

}
