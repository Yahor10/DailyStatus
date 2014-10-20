package by.android.dailystatus.widget.container;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

import by.android.dailystatus.MainActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.ViewEventActivity;
import by.android.dailystatus.application.Constants;
import by.android.dailystatus.dialog.AddDayEvent;
import by.android.dailystatus.newpopup.MenuItem;
import by.android.dailystatus.newpopup.PopupMenu;
import by.android.dailystatus.newpopup.PopupMenu.OnItemSelectedListener;
import by.android.dailystatus.orm.model.EventORM;

public class EventLayout extends LinearLayout implements OnItemSelectedListener {

    private static final int MENU_DELETE = 3;
    private static final int MENU_EDIT = 2;
    private static final int MENU_VIEW = 1;
    private int mEventId = 0;
    MainActivity mainActivity;

    public EventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // TODO check id count
    public void addEventView(final MainActivity mainActivity, EventORM eventORM) {
        this.mainActivity = mainActivity;
        android.view.ViewGroup.LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout layoutEvent = new LinearLayout(
                mainActivity.getApplicationContext());
        layoutEvent.setTag(eventORM);
        layoutEvent.setId(mEventId);
        mEventId++;

        layoutEvent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int viewId = v.getId();

                EventORM eventORM = (EventORM) v.getTag();
                Log.v(Constants.TAG, "eventORM:" + eventORM.id);
                PopupMenu menu = new PopupMenu(mainActivity
                        .getApplicationContext());
                menu.setHeaderTitle(eventORM.description);
                // Set Listener
                menu.setOnItemSelectedListener(EventLayout.this);
                Resources resources = getResources();

                menu.add(MENU_VIEW, R.string.view, viewId, eventORM).setIcon(
                        resources.getDrawable(R.drawable.ic_view));
                menu.add(MENU_EDIT, R.string.edit, viewId, eventORM).setIcon(
                        resources.getDrawable(R.drawable.ic_edit));
                menu.add(MENU_DELETE, R.string.delete, viewId, eventORM)
                        .setIcon(resources.getDrawable(android.R.drawable.ic_delete));
                menu.show(v);

            }
        });

        layoutEvent.setGravity(Gravity.LEFT | Gravity.CENTER);
        layoutEvent.setLayoutParams(layoutParams);

        int imageSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        ImageView image = new ImageView(mainActivity.getApplicationContext());
        LayoutParams imageParams = new LayoutParams(imageSize, imageSize);
        imageParams.leftMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        image.setImageResource(R.drawable.ic_event);
        image.setLayoutParams(imageParams);

        LinearLayout.LayoutParams layoutParamsText = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParamsText.setMargins(10, 0, 0, 0);
        TextView textView = new TextView(mainActivity.getApplicationContext());
        textView.setLayoutParams(layoutParamsText);
        textView.setTextSize(20);
        textView.setText(eventORM.description);

        layoutEvent.addView(image, 0);
        layoutEvent.addView(textView, 1);
        layoutEvent.setBackgroundResource(R.drawable.background_white);
        layoutEvent.setClickable(true);

        addView(layoutEvent, 0);

        if (eventORM.new_item) {
            Random random = new Random();
            int ID_ANIMATION = R.anim.event_list_item_animation;

            switch (random.nextInt(6)) {
                case 0:
                    ID_ANIMATION = R.anim.event_list_item_animation;
                    break;
                case 1:
                    ID_ANIMATION = R.anim.event_list_left_right;
                    break;
                case 2:
                    ID_ANIMATION = R.anim.grow_from_bottomleft_to_topright;
                    break;
                case 3:
                    ID_ANIMATION = R.anim.grow_from_bottom;
                    break;
                case 4:
                    ID_ANIMATION = R.anim.grow_from_top;
                    break;
                case 5:
                    ID_ANIMATION = R.anim.button_clic_animation_zoom_small;
                    break;

                default:
                    break;
            }

            Animation animationEvent = AnimationUtils.loadAnimation(
                    mainActivity.getApplicationContext(), ID_ANIMATION);
            animationEvent.setDuration(500);
            layoutEvent.startAnimation(animationEvent);
            animationEvent = null;
            Log.d("NEW_EVENT", "NEW_EVENT");
            EventORM.deleteEventByName(mainActivity, eventORM.description,
                    eventORM.day);
            eventORM.new_item = false;
            EventORM.insertEvent(mainActivity, eventORM);
        }
    }

    @Override
    public void onItemSelected(MenuItem item) {
        Context context = getContext();

        switch (item.getItemId()) {
            case MENU_VIEW:
                if (context == null) {
                    return;
                }
                EventORM eventORMView = item.getEventORM();
                Intent buildIntent = ViewEventActivity.buildIntent(context,
                        eventORMView.date, eventORMView.description);
                context.startActivity(buildIntent);
                break;
            case MENU_EDIT:
                EventORM eventEdit = item.getEventORM();
                AddDayEvent dialog = new AddDayEvent(mainActivity,
                        eventEdit.description, true);
                dialog.show(mainActivity.getSupportFragmentManager(), "");
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
                removeViewAt(index);
                removeView(findViewById);

                break;
            default:
                break;
        }
    }

}
