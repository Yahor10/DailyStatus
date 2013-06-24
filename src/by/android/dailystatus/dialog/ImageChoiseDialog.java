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
import android.widget.ImageView;
import by.android.dailystatus.R;
import by.android.dailystatus.widget.calendar.Utils;

public class ImageChoiseDialog extends DialogFragment {
	
	private static final int RESULT_TAKE_IMAGE = 0;
	
	private static final int RESULT_LOAD_IMAGE = 1;

	
	private Uri takePictureUri;
	private String picturePath;
	private boolean takePhoto = false;
	
	private ImageView dayImage;
	
	
	public ImageChoiseDialog() {
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final CharSequence[] items = {"Camera", "Gallery"};
		final FragmentActivity activity = getActivity();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Please, choose or take photo");

		AlertDialog alertDialog = builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if(item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(Utils.getTempFile(activity)));
					startActivityForResult(intent, RESULT_TAKE_IMAGE);
				}
				else if (item == 1) {
					Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, RESULT_LOAD_IMAGE);
				}
			}
		}).create();
		
		dayImage = (ImageView) activity.findViewById(R.id.dayImage);
		return alertDialog;
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		 FragmentActivity activity = getActivity();
		if (requestCode == RESULT_TAKE_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
			File file = Utils.getTempFile(activity);
			takePictureUri = Uri.fromFile(file);
			dayImage.setImageURI(takePictureUri);
			takePhoto = true;
		}
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = activity.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
			cursor.close();
			dayImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			takePhoto = false;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
