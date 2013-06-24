package by.android.dailystatus;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import by.android.dailystatus.dialog.ImageChoiseDialog;
import by.android.dailystatus.widget.calendar.Utils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;

import static by.android.dailystatus.application.Constants.TAG;

public class MainActivity extends SherlockFragmentActivity implements
		OnClickListener, OnMenuItemClickListener {

	private static final int DAY_OF_WEEK_LABEL_IDS[] = { R.id.day0, R.id.day1,
			R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6 };

	private Animator mCurrentAnimator;
	private int mShortAnimationDuration;

	private TextView currentDay;
	private ImageView dayImage;

	private int dayStep = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "Main activity created");
		setContentView(R.layout.activity_main);

		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));

		dayImage = (ImageView) findViewById(R.id.dayImage);
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

		// Get first day of week based on locale and populate the day headers
		final int startDay = Utils.getFirstDayOfWeek();
		final int sundayColor = getResources().getColor(
				R.color.sunday_text_color);
		final int saturdayColor = getResources().getColor(
				R.color.saturday_text_color);

		DateFormatSymbols symbols = new DateFormatSymbols(new Locale("EN"));
		String[] dayNames = symbols.getShortWeekdays();
		for (int i = 0; i < dayNames.length - 1; i++) {
			dayNames[i] = dayNames[i + 1];
		}

		DateTime now = DateTime.now();
		int dayOfWeek = now.getDayOfWeek();

		for (int day = 0; day < 7; day++) {
			final String dayString = dayNames[day];
			final TextView label = (TextView) findViewById(DAY_OF_WEEK_LABEL_IDS[day]);
			label.setText(dayString);
			if (dayOfWeek == day) {
				label.setBackgroundColor(Color.BLUE);
			}
			if (Utils.isSunday(day, startDay)) {
				label.setTextColor(sundayColor);
			} else if (Utils.isSaturday(day, startDay)) {
				label.setTextColor(saturdayColor);
			}
		}

		currentDay = (TextView) findViewById(R.id.currentDay);

		DateTime.Property pDoW = now.dayOfWeek();
		String day = pDoW.getAsText();

		Property pMoY = now.monthOfYear();
		String month = pMoY.getAsText();
		int dayOfMonth = now.getDayOfMonth();

		StringBuffer dataBuilder = new StringBuffer();
		dataBuilder.append(day);
		dataBuilder.append(" ");
		dataBuilder.append(dayOfMonth);
		dataBuilder.append(". ");
		dataBuilder.append(month);

		currentDay.setText(dataBuilder.toString());

		findViewById(R.id.good_day).setOnClickListener(this);
		findViewById(R.id.bad_day).setOnClickListener(this);

		dayImage.setOnClickListener(this);

		mShortAnimationDuration = getResources().getInteger(
				android.R.integer.config_shortAnimTime);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		menu.add("Save").setIcon(R.drawable.ic_compose)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		SubMenu subMenu = menu.addSubMenu("").setIcon(
				getResources().getDrawable(
						R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark));

		subMenu.add(0, 1, Menu.NONE, "Add Day Picture")
				.setOnMenuItemClickListener(this);
		subMenu.add(0, 2, Menu.NONE, "Pick Day Color")
				.setOnMenuItemClickListener(this);
		subMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
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
		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.isChecked())
			item.setChecked(false);
		else
			item.setChecked(true);
		return true;
	}

	public void alertChoosePhoto() {
		DialogFragment dialog = new ImageChoiseDialog();
		dialog.show(getSupportFragmentManager(), "");
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			alertChoosePhoto();
			return true;
		case 2:
			// ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
			// colorPickerDialog.initialize(R.string.dialog_title, new int[] {
			// Color.CYAN, Color.LTGRAY, Color.BLACK, Color.BLUE, Color.GREEN,
			// Color.MAGENTA, Color.RED, Color.GRAY }, Color.YELLOW, 3, 2);
			// colorPickerDialog.showPaletteView();
			return true;
		default:
			return false;
		}
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

}
