package by.android.dailystatus.dialog;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;
import by.android.dailystatus.MainActivity;
import by.android.dailystatus.application.Constants;
import by.android.dailystatus.widget.calendar.Utils;

public class ImageChoiseDialog extends DialogFragment {


	public ImageChoiseDialog() {
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final CharSequence[] items = { "Camera", "Gallery" };
		final FragmentActivity activity = getActivity();

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Please, choose or take photo");

		AlertDialog alertDialog = builder.setItems(items,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) {
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(Utils.getTempFile(activity)));
							activity.startActivityForResult(intent, MainActivity.RESULT_TAKE_IMAGE);
						} else if (item == 1) {
							Intent i = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							activity.startActivityForResult(i, MainActivity.RESULT_LOAD_IMAGE);
						}
					}
				}).create();

		return alertDialog;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(by.android.dailystatus.application.Constants.TAG,
				"FRAGMENT ON ACTIVITY RESULT");
		super.onActivityResult(requestCode, resultCode, data);
	}

}
