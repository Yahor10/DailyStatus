package by.android.dailystatus;

import static by.android.dailystatus.application.Constants.TAG;

import org.joda.time.DateTime;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;
import by.android.dailystatus.dialog.ImageChoiseDialog;
import by.android.dailystatus.fragment.DayModel;
import by.android.dailystatus.profile.ProfileActivity;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;

public class MainActivity extends SherlockFragmentActivity implements
		OnMenuItemClickListener, OnPageChangeListener {

	private static final int DAY_OF_WEEK_LABEL_IDS[] = { R.id.day1, R.id.day2,
			R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day0 };

	private Animator mCurrentAnimator;
	private int mShortAnimationDuration;

	private TextView currentDay;
	private ImageView dayImage;

	private int dayStep = 0;

	private DateTime now;

	private LayoutInflater inflater;
	private DayModel[] dayPageModel = new DayModel[3];

	private static final int PAGE_LEFT = 0;
	private static final int PAGE_MIDDLE = 1;
	private static final int PAGE_RIGHT = 2;

	private int mSelectedPageIndex = 1;

	private ViewPager viewPager;

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
		
		initPageModel();
		initDayLaybels();

		DayPageAdapter adapter = new DayPageAdapter();
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
		menu.add("Save").setIcon(R.drawable.ic_compose)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		SubMenu subChoosePhoto = menu.addSubMenu("").setIcon(
				getResources().getDrawable(
						R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark));

		subChoosePhoto.add(0, 1, Menu.NONE, "Add Day Picture")
				.setOnMenuItemClickListener(this);
		subChoosePhoto.add(0, 2, Menu.NONE, "Pick Day Color")
				.setOnMenuItemClickListener(this);
		subChoosePhoto.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		SubMenu subMyProfile = menu.addSubMenu("").setIcon(
				getResources().getDrawable(
						R.drawable.abs__spinner_ab_default_holo_dark));

		subMyProfile.add(0, 3, Menu.NONE, "Charts")
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 4, Menu.NONE, "Month")
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 5, Menu.NONE, "Profile")
				.setOnMenuItemClickListener(this);
		subMyProfile.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.isChecked())
			item.setChecked(false);
		else
			item.setChecked(true);
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			DialogChosePhoto();
			return true;
		case 2:
			// ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
			// colorPickerDialog.initialize(R.string.dialog_title, new int[] {
			// Color.CYAN, Color.LTGRAY, Color.BLACK, Color.BLUE, Color.GREEN,
			// Color.MAGENTA, Color.RED, Color.GRAY }, Color.YELLOW, 3, 2);
			// colorPickerDialog.showPaletteView();
			return true;
		case 5:
			Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
			startActivity(intent);
			return true;
		default:
			return false;
		}
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
				leftPage.setIndex(oldLeftIndex - 1);
				middlePage.setIndex(oldLeftIndex);
				rightPage.setIndex(oldMiddleIndex);

				setContent(PAGE_RIGHT);
				setContent(PAGE_MIDDLE);
				setContent(PAGE_LEFT);

				now = now.minusDays(1);
				updateDateStep();

				// user swiped to left direction --> right page
			} else if (mSelectedPageIndex == PAGE_RIGHT) {

				leftPage.setIndex(oldMiddleIndex);
				middlePage.setIndex(oldRightIndex);
				rightPage.setIndex(oldRightIndex + 1);

				setContent(PAGE_LEFT);
				setContent(PAGE_MIDDLE);
				setContent(PAGE_RIGHT);

				now = now.plusDays(1);
				updateDateStep();
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

	private void setContent(int index) {
		final DayModel model = dayPageModel[index];
		model.dayText.setText(model.getDayText());
	}

	private void DialogChosePhoto() {
		DialogFragment dialog = new ImageChoiseDialog();
		dialog.show(getSupportFragmentManager(), "");
	}

	private void updateDateStep() {

		View prevDay = findViewById(DAY_OF_WEEK_LABEL_IDS[dayStep]);
		prevDay.setBackgroundColor(Color.parseColor("#FFDDDDDD"));

		View view = findViewById(DAY_OF_WEEK_LABEL_IDS[now.getDayOfWeek() - 1]);
		view.setBackgroundColor(Color.BLUE);

		dayStep = now.getDayOfWeek() - 1;

		Log.v(TAG, "CURRENT DAY" + now.getDayOfWeek());

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
			currentDay = (TextView) inflate.findViewById(R.id.currentDay);
			currentPage.dayText = currentDay;
			currentDay.setText(currentPage.getDayText());
			container.addView(inflate);

			dayImage = (ImageView) inflate.findViewById(R.id.dayImage);

			// if (savedInstanceState != null) {
			// this.takePictureUri =
			// Uri.parse(savedInstanceState.getString("takePictureUri"));
			// this.picturePath = savedInstanceState.getString("picturePath");
			// this.takePhoto = savedInstanceState.getBoolean("takePhoto");
			// }
			// if(takePhoto == true & takePictureUri != null) {
			// dayImage.setImageURI(takePictureUri);
			// }
			// else if(takePhoto == false & picturePath != null){
			// dayImage.setImageURI(Uri.parse(picturePath));
			// }

			updateDateStep();

			inflate.findViewById(R.id.good_day).setOnClickListener(this);
			inflate.findViewById(R.id.bad_day).setOnClickListener(this);
			inflate.findViewById(R.id.back_day).setOnClickListener(this);
			inflate.findViewById(R.id.next_day).setOnClickListener(this);

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
				findViewById(R.id.day_layout).setBackgroundColor(Color.BLUE);
				break;
			case R.id.bad_day:
				findViewById(R.id.day_layout).setBackgroundColor(Color.BLACK);
				break;
			case R.id.dayImage:
				BitmapDrawable bitmapDrawable = (BitmapDrawable) dayImage
						.getDrawable();
				Bitmap bitmap = bitmapDrawable.getBitmap();
				zoomImageFromThumb(dayImage, bitmap);
			case R.id.back_day:
				viewPager.setCurrentItem(PAGE_LEFT);
				break;
			case R.id.next_day:
				viewPager.setCurrentItem(PAGE_RIGHT);
			default:
				break;
			}
		}
	}

}
