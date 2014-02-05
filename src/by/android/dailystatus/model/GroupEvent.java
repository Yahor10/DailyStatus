package by.android.dailystatus.model;

import java.util.ArrayList;

import by.android.dailystatus.orm.model.EventORM;


public class GroupEvent {
	public ArrayList<EventORM> events;
	public String nameGroup;
	public int numberGroup;
	
	public GroupEvent(int numberGroup, String nameGroup){
		this.numberGroup = numberGroup;
		this.nameGroup = nameGroup;
		events = new ArrayList<EventORM>();
	}
}
