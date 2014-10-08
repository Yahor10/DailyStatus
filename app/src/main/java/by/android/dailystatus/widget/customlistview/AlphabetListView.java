package by.android.dailystatus.widget.customlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import by.android.dailystatus.R;

public class AlphabetListView extends FrameLayout {

	private ListView listView;
	private AlphabetIndexerView indexer;

	public AlphabetListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public AlphabetListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public AlphabetListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.indexed_listview, this, true);

		listView = (ListView) v.findViewById(R.id.alphabet_listview);
		indexer = (AlphabetIndexerView) v.findViewById(R.id.alphabet_indexer);
	}

	public ListView getListView() {
		return this.listView;
	}

	public AlphabetIndexerView getIndexer() {
		return this.indexer;
	}
}
