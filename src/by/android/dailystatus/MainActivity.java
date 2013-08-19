package by.android.dailystatus;

import static by.android.dailystatus.application.Constants.TAG;

import java.io.File;
import java.util.List;

import org.joda.time.DateTime;

import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapWrapper;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import by.android.dailystatus.alarm.AlarmActivity;
import by.android.dailystatus.application.DailyStatusApplication;
import by.android.dailystatus.dialog.AddDayEvent;
import by.android.dailystatus.dialog.ImageChoiseDialog;
import by.android.dailystatus.fragment.DayModel;
import by.android.dailystatus.orm.model.DayORM;
import by.android.dailystatus.orm.model.EventORM;
import by.android.dailystatus.preference.PreferenceUtils;
import by.android.dailystatus.widget.calendar.CalendarView;
import by.android.dailystatus.widget.calendar.Utils;
import by.android.dailystatus.widget.container.EventLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;

public class MainActivity extends SherlockFragmentActivity implements
		OnMenuItemClickListener, OnPageChangeListener {

	private static final int DAY_OF_WEEK_LABEL_IDS[] = { R.id.day1, R.id.day2,
			R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day0 };

	public static final int RESULT_TAKE_IMAGE = 0;
	public static final int RESULT_LOAD_IMAGE = 1;

	private Uri takePictureUri;

	private Animator mCurrentAnimator;
	private int mShortAnimationDuration;

	private TextView currentDay;
	private ImageView dayImage;
	private EventLayout eventLayout;

	private Button badDay;
	private Button goodDay;

	private int dayStep = 0;

	private DateTime now;

	private LayoutInflater inflater;
	private DayModel[] dayPageModel = new DayModel[3];

	private static final int PAGE_LEFT = 0;
	private static final int PAGE_MIDDLE = 1;
	private static final int PAGE_RIGHT = 2;

	private int mSelectedPageIndex = 1;

	private ViewPager viewPager;

	private DayPageAdapter adapter;

	private BitmapLruCache mCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "Main activity created");
		setContentView(R.layout.activity_main);

		inflater = getLayoutInflater();
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));

		mShortAnimationDuration = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		now = DateTime.now();
		mCache = DailyStatusApplication.getApplication(this).getImageCache();

		initPageModel();
		initDayLaybels();

		adapter = new DayPageAdapter();
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(adapter);
		// we dont want any smoothscroll. This enables us to switch the page
		// without the user notifiying this
		viewPager.setCurrentItem(PAGE_MIDDLE, false);

		viewPager.setOnPageChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		SubMenu subChoosePhoto = menu.addSubMenu("").setIcon(
				getResources().getDrawable(R.drawable.ic_menu_add));

		subChoosePhoto.add(0, 1, Menu.NONE, "Add Day Picture")
				.setOnMenuItemClickListener(this);
		subChoosePhoto.add(0, 3, Menu.NONE, "Add Day Event")
				.setOnMenuItemClickListener(this);
		subChoosePhoto.getItem()
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		SubMenu subMyProfile = menu.addSubMenu("").setIcon(
				getResources().getDrawable(
						R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark));

		subMyProfile.add(0, 4, Menu.NONE, "Charts")
				.setIcon(R.drawable.ic_menu_chart)
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 5, Menu.NONE, "Month")
				.setIcon(R.drawable.ic_menu_calendar)
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 6, Menu.NONE, "Profile")
				.setIcon(R.drawable.ic_menu_profile)
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 7, Menu.NONE, "Alarm").setOnMenuItemClickListener(
				this);
		subMyProfile.add(0, 8, Menu.NONE, "Settings")
				.setIcon(R.drawable.ic_settings)
				.setOnMenuItemClickListener(this);
		subMyProfile.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			DialogChosePhoto();
			break;
		case 3:
			DialogDayEvent();
			break;
		case 4:
			int dayOfWeek = now.getDayOfYear();
			int monthOfYear = now.getMonthOfYear();
			int year = now.getYear();
			startActivity(ChartsActivity.buintIntent(this, dayOfWeek,
					monthOfYear, year));
			break;
		case 5:
			startActivity(CalendarView.buintIntent(this));
			break;
		case 6:
			Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
			startActivity(intent);
			break;
		case 7:
			Intent intentAlarm = new Intent(MainActivity.this,
					AlarmActivity.class);
			startActivity(intentAlarm);
			break;
		case 8:
			Intent intantSettings = new Intent(MainActivity.this,
					SettingsActivity.class);
			startActivity(intantSettings);
			break;
		default:
		}
		return true;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (state == ViewPager.SCROLL_STATE_IDLE) {
			Log.v(TAG, "onPageScrollStateChanged");
			final DayModel leftPage = dayPageModel[PAGE_LEFT];
			final DayModel middlePage = dayPageModel[PAGE_MIDDLE];
			final DayModel rightPage = dayPageModel[PAGE_RIGHT];

			final int oldLeftIndex = leftPage.getIndex();
			final int oldMiddleIndex = middlePage.getIndex();
			final int oldRightIndex = rightPage.getIndex();

			// user swiped to right direction --> left page
			if (mSelectedPageIndex == PAGE_LEFT) {

				// moving each page content one page to the right
				leftPage.setIndex(oldLeftIndex - 1);
				middlePage.setIndex(oldLeftIndex);
				rightPage.setIndex(oldMiddleIndex);

				now = now.minusDays(1);
				updateDateStep();

				setContent(PAGE_RIGHT);
				setContent(PAGE_MIDDLE);
				setContent(PAGE_LEFT);

				// user swiped to left direction --> right page
			} else if (mSelectedPageIndex == PAGE_RIGHT) {

				leftPage.setIndex(oldMiddleIndex);
				middlePage.setIndex(oldRightIndex);
				rightPage.setIndex(oldRightIndex + 1);

				now = now.plusDays(1);
				updateDateStep();

				setContent(PAGE_LEFT);
				setContent(PAGE_MIDDLE);
				setContent(PAGE_RIGHT);

			}
			viewPager.setCurrentItem(PAGE_MIDDLE, false);
		}

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int position) {
		mSelectedPageIndex = position;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_TAKE_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			File file = Utils.getTempFile(this);
			takePictureUri = Uri.fromFile(file);
			Log.v(TAG, "TEMP FILE");
			// Bitmap bitmap =
			// BitmapFactory.decodeStream(takePictureUri.toString());
			// notify
		}
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

			Log.v(TAG,
					"PICTURE PATH" + picturePath + "DAY" + now.getDayOfYear());
			CacheableBitmapWrapper newValue = new CacheableBitmapWrapper(bitmap);
			mCache.put(picturePath, newValue);

			Log.v(TAG, "LRU CACHE SIZE" + mCache.size());

			String currentUser = PreferenceUtils.getCurrentUser(this);

			DayORM day = new DayORM(currentUser, now.getDayOfYear(),
					now.getMonthOfYear(), now.getYear());

			day.pictureURL = picturePath;
			DayORM.insertOrUpdateDay(this, day);
			adapter.notifyDataSetChanged();

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initDayLaybels() {
		int dayOfWeek = now.getDayOfWeek() - 1;
		// Get first day of week based on locale and populate the day
		// headers
		final int sundayColor = getResources().getColor(
				R.color.sunday_text_color);
		final int saturdayColor = getResources().getColor(
				R.color.saturday_text_color);

		DateTime weekStart = now.dayOfWeek().withMinimumValue();

		String[] weekDays = new String[7];

		for (int i = 0; i < weekDays.length; i++) {
			DateTime plusDays = weekStart.plusDays(i);
			String asShortText = plusDays.dayOfWeek().getAsShortText();
			weekDays[i] = asShortText;
		}

		for (int day = 0; day < weekDays.length; day++) {
			final String dayString = weekDays[day];
			final TextView label = (TextView) findViewById(DAY_OF_WEEK_LABEL_IDS[day]);
			label.setText(dayString);
			if (dayOfWeek == day) {
				label.setBackgroundColor(Color.BLUE);
				dayStep = day;
			}
			if (day == 6) {
				label.setTextColor(sundayColor);
			} else if (day == 5) {
				label.setTextColor(saturdayColor);
			}
		}
	}

	private void initPageModel() {
		for (int i = 0; i < dayPageModel.length; i++) {
			// initing the pagemodel with indexes of -1, 0 and 1
			dayPageModel[i] = new DayModel(i - 1);
		}
	}

	public void updateContent() {
		setContent(PAGE_MIDDLE);
	}

	private void setContent(int index) {
		final DayModel model = dayPageModel[index];
		String dayText = model.getDayText();
		model.dayText.setText(dayText);

		int violetColor = getResources().getColor(R.color.violet);
		model.goodDay.setBackgroundColor(violetColor);
		model.badDay.setBackgroundColor(violetColor);

		DateTime date = model.getDate();
		DayORM day = DayORM.getDay(this, date.getDayOfYear(), date.getYear());
		if (day != null) {
			if (day.pictureURL != null) {
				final CacheableBitmapWrapper cacheableBitmapWrapper = mCache
						.get(day.pictureURL);
				if (cacheableBitmapWrapper != null
						&& cacheableBitmapWrapper.hasValidBitmap()) {
					model.dayImage.setImageBitmap(cacheableBitmapWrapper
							.getBitmap());
				}
			}

			Log.i(TAG, "DAY" + day);
			int status = day.status;
			int color = 0;
			switch (status) {
			case 1:
				color = getResources().getColor(android.R.color.white);
				model.goodDay.setBackgroundColor(color);
				break;
			case -1:
				color = getResources().getColor(android.R.color.black);
				model.badDay.setBackgroundColor(color);
				break;
			default:
				break;
			}
		} else {
			model.dayImage.setImageResource(R.drawable.ic_launcher);
			model.goodDay.setBackgroundColor(violetColor);
			model.badDay.setBackgroundColor(violetColor);
		}

		int dayOfYear = now.getDayOfYear();
		int year = now.getYear();

//		List<EventORM> eventsByDay = EventORM.getEventsByDay(
//				getApplicationContext(), dayOfYear, year);
//		if (eventsByDay != null && !eventsByDay.isEmpty()) {
//			for (EventORM eventORM : eventsByDay) {
//				Log.i(TAG,"eventORM" + eventORM);
//				eventLayout.addEventView(getApplicationContext(),
//						eventORM.description);
//			}
//		} else {
//			eventLayout.removeAllViews();
//		}
	}

	private void DialogDayEvent() {
		AddDayEvent dialog = new AddDayEvent(this);
		dialog.show(getSupportFragmentManager(), "");
	}

	private void DialogChosePhoto() {
		DialogFragment dialog = new ImageChoiseDialog();
		dialog.show(getSupportFragmentManager(), "");
	}

	private void updateDateStep() {

		View prevDay = findViewById(DAY_OF_WEEK_LABEL_IDS[dayStep]);
		prevDay.setBackgroundColor(Color.parseColor("#67CBEE"));
		View view = findViewById(DAY_OF_WEEK_LABEL_IDS[now.getDayOfWeek() - 1]);
		view.setBackgroundColor(Color.BLUE);

		dayStep = now.getDayOfWeek() - 1;
	}

	private void zoomImageFromThumb(final View thumbView, Bitmap bitmap) {
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}

		final ImageView expandedImageView = (ImageView) findViewById(R.id.dayImage);
		expandedImageView.setImageBitmap(bitmap);

		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		thumbView.getGlobalVisibleRect(startBounds);
		findViewById(R.id.day_layout).getGlobalVisibleRect(finalBounds,
				globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);

		float startScale;
		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
				.width() / startBounds.height()) {
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		thumbView.setAlpha(0f);
		expandedImageView.setVisibility(View.VISIBLE);

		expandedImageView.setPivotX(0f);
		expandedImageView.setPivotY(0f);

		AnimatorSet set = new AnimatorSet();
		set.play(
				ObjectAnimator.ofFloat(expandedImageView, View.X,
						startBounds.left, finalBounds.left))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
						startBounds.top, finalBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
						startScale, 1f))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
						startScale, 1f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;

		final float startScaleFinal = startScale;
		expandedImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurrentAnimator != null) {
					mCurrentAnimator.cancel();
				}

				AnimatorSet set = new AnimatorSet();
				set.play(
						ObjectAnimator.ofFloat(expandedImageView, View.X,
								startBounds.left))
						.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
								startBounds.top))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_X, startScaleFinal))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_Y, startScaleFinal));
				set.setDuration(mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}
				});
				set.start();
				mCurrentAnimator = set;
			}
		});
	}

	// public void onSaveInstanceState(Bundle bundle) {
	// super.onSaveInstanceState(bundle);
	// if(takePictureUri != null) {
	// bundle.putString("takePictureUri", takePictureUri.getPath());
	// }
	// bundle.putString("picturePath", picturePath);
	// bundle.putBoolean("takePhoto", takePhoto);
	// }

	private class DayPageAdapter extends PagerAdapter implements
			OnClickListener {

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			// we only need three pages
			return 3;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View inflate = inflater.inflate(R.layout.day_fragment, null);
			DayModel currentPage = dayPageModel[position];
			Log.v(TAG, "instantiateItem");
			currentDay = (TextView) inflate.findViewById(R.id.currentDay);
			dayImage = (ImageView) inflate.findViewById(R.id.dayImage);

			goodDay = (Button) inflate.findViewById(R.id.good_day);
			badDay = (Button) inflate.findViewById(R.id.bad_day);
			eventLayout = (EventLayout) inflate.findViewById(R.id.eventLayout);

			eventLayout.addEventView(getApplicationContext(),
					"T1");
			eventLayout.addEventView(getApplicationContext(),
					"T2");
			eventLayout.addEventView(getApplicationContext(),
					"T3");

			
			currentPage.dayText = currentDay;
			currentPage.dayImage = dayImage;
			currentPage.goodDay = goodDay;
			currentPage.badDay = badDay;

			currentDay.setText(currentPage.getDayText());
			container.addView(inflate);

			updateDateStep();

			inflate.findViewById(R.id.good_day).setOnClickListener(this);
			inflate.findViewById(R.id.bad_day).setOnClickListener(this);
			inflate.findViewById(R.id.back_day).setOnClickListener(this);
			inflate.findViewById(R.id.next_day).setOnClickListener(this);
			inflate.findViewById(R.id.addDayEvent).setOnClickListener(this);

			dayImage.setOnClickListener(this);

			return inflate;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.good_day:
				String currentUser = PreferenceUtils
						.getCurrentUser(getApplicationContext());

				DayORM day = new DayORM(currentUser, now.getDayOfYear(),
						now.getMonthOfYear(), now.getYear());
				day.status = 1;

				DayORM.insertOrUpdateDay(getApplicationContext(), day);
				Toast.makeText(getApplicationContext(), "GOOD DAY",
						Toast.LENGTH_SHORT).show();
				setContent(PAGE_MIDDLE);
				break;
			case R.id.bad_day:
				currentUser = PreferenceUtils
						.getCurrentUser(getApplicationContext());

				day = new DayORM(currentUser, now.getDayOfYear(),
						now.getMonthOfYear(), now.getYear());
				day.status = -1;

				DayORM.insertOrUpdateDay(getApplicationContext(), day);
				Toast.makeText(getApplicationContext(), "BAD DAY",
						Toast.LENGTH_SHORT).show();
				setContent(PAGE_MIDDLE);
				break;
			case R.id.dayImage:
				// BitmapDrawable bitmapDrawable = (BitmapDrawable) dayImage
				// .getDrawable();
				// Bitmap bitmap = bitmapDrawable.getBitmap();
				// zoomImageFromThumb(dayImage, bitmap);
			case R.id.back_day:
				viewPager.setCurrentItem(PAGE_LEFT);
				break;
			case R.id.next_day:
				viewPager.setCurrentItem(PAGE_RIGHT);
				break;
			case R.id.addDayEvent:
				DialogDayEvent();
				break;
			default:
				break;
			}
		}
	}

	public DateTime getNow() {
		return now;
	}
}