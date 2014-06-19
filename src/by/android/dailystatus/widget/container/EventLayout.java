package by.android.dailystatus.widget.container;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import by.android.dailystatus.R;
import by.android.dailystatus.ViewEventActivity;
import by.android.dailystatus.application.Constants;
import by.android.dailystatus.newpopup.MenuItem;
import by.android.dailystatus.newpopup.PopupMenu;
import by.android.dailystatus.newpopup.PopupMenu.OnItemSelectedListener;
import by.android.dailystatus.orm.model.EventORM;

public class EventLayout extends LinearLayout implements OnItemSelectedListener {

	private static final int MENU_DELETE = 3;
	private static final int MENU_EDIT = 2;
	private static final int MENU_VIEW = 1;
	private int mEventId = 0;

	public EventLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// TODO check id count
	public void addEventView(final Context context, EventORM eventORM) {

		android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout layoutEvent = new LinearLayout(context);
		layoutEvent.setTag(eventORM);
		layoutEvent.setId(mEventId);
		mEventId++;

		layoutEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int viewId = v.getId();

				EventORM eventORM = (EventORM) v.getTag();
				Log.v(Constants.TAG, "eventORM:" + eventORM.id);
				PopupMenu menu = new PopupMenu(context);
				menu.setHeaderTitle(eventORM.description);
				// Set Listener
				menu.setOnItemSelectedListener(EventLayout.this);
				Resources resources = getResources();

				// TODO find resources
				menu.add(MENU_VIEW, R.string.view, viewId, eventORM).setIcon(
						resources.getDrawable(R.drawable.ic_edit));
				menu.add(MENU_EDIT, R.string.edit, viewId, eventORM).setIcon(
						resources.getDrawable(R.drawable.ic_edit));
				menu.add(MENU_DELETE, R.string.delete, viewId, eventORM)
						.setIcon(
								resources
										.getDrawable(android.R.drawable.ic_delete));
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
		separatorDown.setClickable(false);
		separatorDown.setBackgroundColor(Color.BLACK);

		addView(layoutEvent, 0);
		addView(separatorDown, 1);
	}

	@Override
	public void onItemSelected(MenuItem item) {

		Log.v(Constants.TAG, "MenuItem onItemSelected");
		Context context = getContext();

		switch (item.getItemId()) {
		case MENU_VIEW:
			if (context == null) {
				return;
			}
			context.startActivity(ViewEventActivity.buildIntent(context));
			break;

		case MENU_EDIT:
			// TODO edit;
			break;
		case MENU_DELETE:
			if (context == null) {
				return;
			}
			int viewId = item.getViewId();
			EventORM eventORM = item.getEventORM();
			EventORM.deleteEventByID(context, eventORM.id);
			View findViewById = findViewById(viewId);
			if (findViewById == null) {
				return;
			}

			int index = ((ViewGroup) findViewById.getParent())
					.indexOfChild(findViewById);
			removeViewAt(index + 1);
			removeView(findViewById);

			break;
		default:
			break;
		}
	}

}
