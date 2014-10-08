package by.android.dailystatus.model;

import java.util.ArrayList;

import by.android.dailystatus.orm.model.EventORM;

public class GroupEvent implements Cloneable {
	public ArrayList<EventORM> events;
	public String nameGroup;
	public int numberGroup;

	public GroupEvent(int numberGroup, String nameGroup) {
		this.numberGroup = numberGroup;
		this.nameGroup = nameGroup;
		events = new ArrayList<EventORM>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nameGroup == null) ? 0 : nameGroup.hashCode());
		result = prime * result + numberGroup;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupEvent other = (GroupEvent) obj;
		if (nameGroup == null) {
			if (other.nameGroup != null)
				return false;
		} else if (!nameGroup.equals(other.nameGroup))
			return false;
		if (numberGroup != other.numberGroup)
			return false;
		return true;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
