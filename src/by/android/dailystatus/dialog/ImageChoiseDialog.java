package by.android.dailystatus.dialog;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import by.android.dailystatus.MainActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.preference.PreferenceUtils;

public class ImageChoiseDialog extends DialogFragment implements
		OnClickListener {

	private MainActivity mainActivity;

	public ImageChoiseDialog(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final FragmentActivity activity = getActivity();

		final Dialog dialog = new Dialog(activity);
		// dialog.setTitle(R.string.title_dialog_add_event);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.add_image_dialog);

		dialog.findViewById(R.id.lay_camera).setOnClickListener(this);
		dialog.findViewById(R.id.lay_gallery).setOnClickListener(this);

		return dialog;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(by.android.dailystatus.application.Constants.TAG,
				"FRAGMENT ON ACTIVITY RESULT");
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.lay_camera:
			String path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
					+ "/picFolder/";
			File newdir = new File(path);
			newdir.mkdirs();

			String file = path + ((Long) System.currentTimeMillis()).toString()
					+ ".jpg";

			PreferenceUtils.setImageFromCameraURL(getActivity(), file);

			File newfile = new File(file);
			try {
				newfile.createNewFile();
			} catch (IOException e) {
			}

			Uri outputFileUri = Uri.fromFile(newfile);

			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

			mainActivity.startActivityForResult(cameraIntent,
					MainActivity.RESULT_TAKE_IMAGE);
			getDialog().dismiss();

			break;

		case R.id.lay_gallery:
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			mainActivity.startActivityForResult(i,
					MainActivity.RESULT_LOAD_IMAGE);
			getDialog().dismiss();
			break;

		default:
			break;
		}

	}

}
