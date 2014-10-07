package by.android.dailystatus.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import by.android.dailystatus.R;

public class LicenseDialog extends DialogFragment {

    String text;

    public void setText(String text) {
        this.text = text;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_license);
        TextView description = (TextView) dialog
                .findViewById(R.id.txt_dialog_version_description);
        if (text != null && !TextUtils.isEmpty(text)) {
            description.setText(text);
        }

        dialog.findViewById(R.id.but_dialog_version_ok).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();

                    }
                });

        return dialog;
    }

}
