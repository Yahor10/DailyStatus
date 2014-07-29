package by.android.dailystatus.newpopup;

import by.android.dailystatus.orm.model.EventORM;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class MenuItem {

    private int itemId;
    private String title;
    private int viewId;
    private Drawable icon;
    private Intent intent;
    private EventORM eventORM;

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    public int getItemId() {
        return itemId;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public int getViewId() {
		return viewId;
	}
	public void setViewId(int viewId) {
		this.viewId = viewId;
	}
	public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public Drawable getIcon() {
        return icon;
    }
    public void setIntent(Intent intent) {
        this.intent = intent;
    }
    public Intent getIntent() {
        return intent;
    }
	public EventORM getEventORM() {
		return eventORM;
	}
	public void setEventORM(EventORM eventORM) {
		this.eventORM = eventORM;
	}
}