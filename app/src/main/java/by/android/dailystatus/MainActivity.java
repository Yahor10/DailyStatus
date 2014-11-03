package by.android.dailystatus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import by.android.dailystatus.alarm.EveryDayTimeAlarm;
import by.android.dailystatus.application.DailyStatusApplication;
import by.android.dailystatus.dialog.AddDayEvent;
import by.android.dailystatus.dialog.ImageChoiseDialog;
import by.android.dailystatus.fragment.DayModel;
import by.android.dailystatus.orm.model.DayORM;
import by.android.dailystatus.orm.model.EventORM;
import by.android.dailystatus.preference.PreferenceUtils;
import by.android.dailystatus.widget.animations.AnimationViewPagerFragmentZoom;
import by.android.dailystatus.widget.calendar.CalendarView;
import by.android.dailystatus.widget.container.EventLayout;
import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapWrapper;

import static by.android.dailystatus.application.Constants.TAG;

public class MainActivity extends ActionBarActivity implements
		OnMenuItemClickListener, OnPageChangeListener {

	private static final int DAY_OF_WEEK_LABEL_IDS[] = {R.id.day1, R.id.day2,
			R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day0};

	public static final int RESULT_TAKE_IMAGE = 0;
	public static final int RESULT_LOAD_IMAGE = 1;
	public static final int RESULT_LOG_OUT = 2;
	public static final int RESULT_GET_STANDART_EVENT = 3;

	private static final String DAY_MILLS = "DAY_MILLS";
	private Uri takePictureUri;

	private Animator mCurrentAnimator;
	private int mShortAnimationDuration;

	private TextView currentDay;

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

	public static Intent buildIntent(Context context) {
		return new Intent(context, MainActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (!PreferenceUtils
				.getFlagApplicationWasLaunched(getApplicationContext())) {
			PreferenceUtils
					.setFlagApplicationWasLaunched(getApplicationContext());
			setRepeatingAlarm(getApplicationContext());

		}

		inflater = getLayoutInflater();
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#FFFFFFFF")));// "#0e78c9"

		mShortAnimationDuration = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		if (savedInstanceState == null) {
			now = DateTime.now();
		} else {
			long dayMills = savedInstanceState.getLong(DAY_MILLS);
			now = new DateTime(dayMills);
		}
		mCache = DailyStatusApplication.getApplication(this).getImageCache();

		initPageModel();
		initDayLaybels();

		adapter = new DayPageAdapter();
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setPageTransformer(true, new AnimationViewPagerFragmentZoom());
		viewPager.setAdapter(adapter);
		// we dont want any smoothscroll. This enables us to switch the page
		// without the user notifiying this
		viewPager.setCurrentItem(PAGE_MIDDLE, false);

		viewPager.setOnPageChangeListener(this);
	}

	public static void setRepeatingAlarm(Context context) {

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, EveryDayTimeAlarm.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);

		am.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 10800000, 10800000, pendingIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		SubMenu subChoosePhoto = menu.addSubMenu(R.string.menu_add_element)
				.setIcon(getResources().getDrawable(R.drawable.ic_add));

		subChoosePhoto.add(0, 1, Menu.NONE, R.string.add_day_picture)
				.setOnMenuItemClickListener(this);
		subChoosePhoto.add(0, 2, Menu.NONE, R.string.add_standart_event)
				.setOnMenuItemClickListener(this);
		subChoosePhoto.add(0, 3, Menu.NONE, getString(R.string.add_event_from_calendar))
				.setOnMenuItemClickListener(this);
		subChoosePhoto.add(0, 4, Menu.NONE, R.string.add_day_event)
				.setOnMenuItemClickListener(this);
		subChoosePhoto.getItem()
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		SubMenu subMyProfile = menu.addSubMenu("Dropdown list").setIcon(
				getResources().getDrawable(R.drawable.ic_override_menu));

		subMyProfile.add(0, 5, Menu.NONE, R.string.charts)
				.setIcon(R.drawable.ic_menu_graphics)
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 6, Menu.NONE, R.string.calendar)
				.setIcon(R.drawable.ic_menu_calendar)
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 7, Menu.NONE, R.string.profile)
				.setIcon(R.drawable.ic_profile)
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 8, Menu.NONE, R.string.events)
				.setIcon(R.drawable.ic_add_event)
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 9, Menu.NONE, R.string.settings)
				.setIcon(R.drawable.ic_menu_settings)
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
			case 2:
				Intent intentEvents = new Intent(MainActivity.this,
						EventsListStandartActivity.class);
				startActivityForResult(intentEvents, RESULT_GET_STANDART_EVENT);
				break;
			case 3:
				Intent intentAddFromCalendar = new Intent(MainActivity.this,
						AddEventFromCalendarActivity.class);
				startActivityForResult(intentAddFromCalendar, RESULT_GET_STANDART_EVENT);
				break;
			case 4:
				DialogDayEvent();
				break;
			case 5:
				int dayOfWeek = now.getDayOfYear();
				int monthOfYear = now.getMonthOfYear();
				int year = now.getYear();
				startActivity(ChartsActivity.buintIntent(this, dayOfWeek,
						monthOfYear, year));
				break;
			case 6:
				startActivity(CalendarView.buintIntent(this));
				break;
			case 7:
				Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
				startActivityForResult(intent, RESULT_LOG_OUT);
				break;
			case 8:
				int dayOfWeek1 = now.getDayOfYear();
				int monthOfYear1 = now.getMonthOfYear();
				int year1 = now.getYear();
				startActivity(EventActivity.buintIntent(this, dayOfWeek1,
						monthOfYear1, year1));
				break;
			case 9:
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
			final DayModel leftPage = dayPageModel[PAGE_LEFT];
			final DayModel middlePage = dayPageModel[PAGE_MIDDLE];
			final DayModel rightPage = dayPageModel[PAGE_RIGHT];

			final int oldLeftIndex = leftPage.getIndex();
			final int oldMiddleIndex = middlePage.getIndex();
			final int oldRightIndex = rightPage.getIndex();

			// user swiped to right direction --> left page
			if (mSelectedPageIndex == PAGE_LEFT) {

				// moving each page content one page to the right

				now = now.minusDays(1);

				leftPage.setIndex(oldLeftIndex - 1, now.minusDays(1));
				middlePage.setIndex(oldLeftIndex, now);
				rightPage.setIndex(oldMiddleIndex, now.plusDays(1));

				updateDateStep();

				setContent(PAGE_RIGHT);
				setContent(PAGE_MIDDLE);
				setContent(PAGE_LEFT);

				// user swiped to left direction --> right page
			} else if (mSelectedPageIndex == PAGE_RIGHT) {

				now = now.plusDays(1);

				leftPage.setIndex(oldMiddleIndex, now.minusDays(1));
				middlePage.setIndex(oldRightIndex, now);
				rightPage.setIndex(oldRightIndex + 1, now.plusDays(1));

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
		final DayModel leftPage = dayPageModel[position];
		leftPage.goodDay.clearAnimation();
		leftPage.badDay.clearAnimation();

	}

	private Bitmap decodeFile(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE_WIGHT = 300;
			final int REQUIRED_SIZE_HEIGHT = 300;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE_WIGHT
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE_HEIGHT)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void saveImage(Uri selectedImage) throws Exception {
		String[] filePathColumn = {MediaStore.Images.Media.DATA};
		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();

		if (picturePath == null) {
			throw new PictureNotExistsException();
		}

		File newdir = new File(picturePath);
		// cursor.close();

		Bitmap bitmap = decodeFile(newdir);

		Log.v(TAG, "PICTURE PATH" + picturePath + "DAY"
				+ now.dayOfWeek().getAsShortText());
		CacheableBitmapWrapper newValue = new CacheableBitmapWrapper(bitmap);
		mCache.put(picturePath, newValue);

		String currentUser = PreferenceUtils.getCurrentUser(this);

		DayORM day = new DayORM(currentUser, now.getDayOfYear(),
				now.getMonthOfYear(), now.getYear());

		day.pictureURL = picturePath;
		DayORM.insertOrUpdateDayPicture(this, day);

		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_TAKE_IMAGE && resultCode == RESULT_OK) {


			String picturePath = PreferenceUtils
					.getImageFromCameraURL(getApplicationContext());
			File newdir = new File(picturePath);
			Bitmap bitmap = decodeFile(newdir);

			CacheableBitmapWrapper newValue = new CacheableBitmapWrapper(bitmap);
			mCache.put(picturePath, newValue);

			String currentUser = PreferenceUtils.getCurrentUser(this);

			DayORM day = new DayORM(currentUser, now.getDayOfYear(),
					now.getMonthOfYear(), now.getYear());

			day.pictureURL = picturePath;
			DayORM.insertOrUpdateDayPicture(this, day);

			adapter.notifyDataSetChanged();

		}
		if ((requestCode == RESULT_LOAD_IMAGE || requestCode == RESULT_TAKE_IMAGE)
				&& resultCode == RESULT_OK && null != data) {

			try {
				saveImage(data.getData());
			} catch (PictureNotExistsException e) {
				Toast.makeText(this, "Picture does not exist on the device", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (Exception e) {
				Toast.makeText(this, "Cannot open picture", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}

		if (requestCode == RESULT_LOG_OUT && resultCode == RESULT_OK) {

			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
			finish();

		}
		if (requestCode == RESULT_GET_STANDART_EVENT && resultCode == RESULT_OK) {
			if (data != null) {
				String title = data.getStringExtra(EventsListStandartActivity.MESSAGE_KEY);

				DateTime now = getNow();

				String currentUser = PreferenceUtils.getCurrentUser(this);
				int day = now.getDayOfYear();
				int month = now.getMonthOfYear();
				int year = now.getYear();
				long date = now.getMillis();
				EventORM event = new EventORM(currentUser, day, month, year, date, title);
				event.new_item = true;

				EventORM.insertEvent(this, event);
				updateContent();
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putLong(DAY_MILLS, now.getMillis());
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		long dayMills = savedInstanceState.getLong(DAY_MILLS);
		now = new DateTime(dayMills);
	}

	private void initDayLaybels() {
		int dayOfWeek = now.getDayOfWeek() - 1;

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
			label.setBackgroundColor(getResources().getColor(R.color.red_border));
			if (dayOfWeek == day) {
				int margin = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) label.getLayoutParams();
				params.topMargin = margin;
				params.bottomMargin = margin;
				params.leftMargin = margin;
				params.rightMargin = margin;
				dayStep = day;
			}
		}
	}

	private void initPageModel() {
		for (int i = 0; i < dayPageModel.length; i++) {
			dayPageModel[i] = new DayModel(i - 1, getApplicationContext(), now);
		}
	}

	public void updateContent() {
		setContent(PAGE_MIDDLE);
	}

	@SuppressLint("NewApi")
	private void setContent(int index) {
		final DayModel model = dayPageModel[index];
		String dayText = model.getDayText();
		model.dayText.setText(dayText);

		DateTime date = model.getDate();
		int dayOfYear = date.getDayOfYear();
		int year = date.getYear();

		DayORM day = DayORM.getDay(this, dayOfYear, year);
		if (day != null) {
			if (day.pictureURL != null) {
				final CacheableBitmapWrapper cacheableBitmapWrapper = mCache
						.get(day.pictureURL);
				if (cacheableBitmapWrapper != null
						&& cacheableBitmapWrapper.hasValidBitmap()) {
					model.dayImage.setImageBitmap(cacheableBitmapWrapper
							.getBitmap());
				} else {
					String picturePath = day.pictureURL;
					File newdir = new File(picturePath);
					Log.v(TAG,
							"PICTURE PATH " + picturePath + " DAY "
									+ now.getDayOfYear());

					Bitmap bitmap = decodeFile(newdir);
					if (bitmap != null) {
						CacheableBitmapWrapper newValue = new CacheableBitmapWrapper(
								bitmap);
						mCache.put(picturePath, newValue);
						model.dayImage.setImageBitmap(bitmap);
					} else {
						model.dayImage.setImageResource(R.drawable.photo3);
					}
				}
			} else {
				model.dayImage.setImageResource(R.drawable.photo3);
			}

			model.eventLayout.removeAllViews();

			int status = day.status;
			switch (status) {
				case 1:
					ScaleAnimation animation_good = new ScaleAnimation(0, 1, 0, 1,
							ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					animation_good.setDuration(500);
					animation_good.setInterpolator(new OvershootInterpolator());
					model.goodDay.findViewById(R.id.good_day_text).startAnimation(animation_good);
					break;
				case -1:
					ScaleAnimation animation_bad = new ScaleAnimation(0, 1, 0, 1,
							ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					animation_bad.setDuration(500);
					animation_bad.setInterpolator(new OvershootInterpolator());
					model.badDay.findViewById(R.id.bad_day_text).startAnimation(animation_bad);
					break;
				default:
					break;
			}
		} else {
			model.dayImage.setImageResource(R.drawable.photo3);
			model.eventLayout.removeAllViews();
		}

		List<EventORM> eventsByDay = EventORM.getEventsByDay(
				getApplicationContext(), dayOfYear, year);
		if (eventsByDay != null && !eventsByDay.isEmpty()) {
			for (EventORM eventORM : eventsByDay) {
				model.eventLayout.addEventView(this, eventORM);
			}
		} else {
			model.eventLayout.removeAllViews();
		}

	}

	private void DialogDayEvent() {
		AddDayEvent dialog = new AddDayEvent();
		dialog.show(getSupportFragmentManager(), "");
	}

	private void DialogChosePhoto() {
		DialogFragment dialog = new ImageChoiseDialog(this);
		dialog.show(getSupportFragmentManager(), "");
	}

	private void updateDateStep() {
		int margin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
		View prevDay = findViewById(DAY_OF_WEEK_LABEL_IDS[dayStep]);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) prevDay.getLayoutParams();
		params.topMargin = params.bottomMargin = params.leftMargin = params.rightMargin = 0;
		prevDay.setLayoutParams(params);

		View view = findViewById(DAY_OF_WEEK_LABEL_IDS[now.getDayOfWeek() - 1]);
		params = (LinearLayout.LayoutParams) view.getLayoutParams();
		params.topMargin = params.bottomMargin = params.leftMargin = params.rightMargin = margin;
		view.setLayoutParams(params);

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

			currentDay = (TextView) inflate.findViewById(R.id.currentDay);

			ImageView dayImage = (ImageView) inflate
					.findViewById(R.id.dayImage);
			View goodDay = inflate.findViewById(R.id.good_container);
			View badDay = inflate.findViewById(R.id.bad_container);
			EventLayout eventLayout = (EventLayout) inflate
					.findViewById(R.id.eventLayout);

			currentPage.dayText = currentDay;
			currentPage.dayImage = dayImage;
			currentPage.goodDay = goodDay;
			currentPage.badDay = badDay;
			currentPage.eventLayout = eventLayout;

			currentDay.setText(currentPage.getDayText());
			container.addView(inflate);

			updateDateStep();
			setContent(position);

			inflate.findViewById(R.id.good_container).setOnClickListener(this);
			inflate.findViewById(R.id.bad_container).setOnClickListener(this);
			inflate.findViewById(R.id.back_day).setOnClickListener(this);
			inflate.findViewById(R.id.next_day).setOnClickListener(this);
			inflate.findViewById(R.id.addDayEvent).setOnClickListener(this);

			inflate.findViewById(R.id.good_container).setTag(
					inflate.findViewById(R.id.bad_day_text));
			inflate.findViewById(R.id.bad_container).setTag(
					inflate.findViewById(R.id.good_day_text));

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
				case R.id.good_container:

					String currentUser = PreferenceUtils
							.getCurrentUser(getApplicationContext());

					DayORM day = DayORM.getDay(MainActivity.this,
							now.getDayOfYear(), now.getYear());
					if (day == null)
						day = new DayORM(currentUser, now.getDayOfYear(),
								now.getMonthOfYear(), now.getYear());

					day.status = 1;

					DayORM.insertOrUpdateDay(getApplicationContext(), day);
					setContent(PAGE_MIDDLE);
					break;
				case R.id.bad_container:
					currentUser = PreferenceUtils
							.getCurrentUser(getApplicationContext());
					day = DayORM.getDay(MainActivity.this, now.getDayOfYear(),
							now.getYear());

					if (day == null)
						day = new DayORM(currentUser, now.getDayOfYear(),
								now.getMonthOfYear(), now.getYear());

					day.status = -1;

					DayORM.insertOrUpdateDay(getApplicationContext(), day);
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

	class PictureNotExistsException extends Exception {

	}
}