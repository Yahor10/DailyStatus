package by.android.dailystatus.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import by.android.dailystatus.R;
import by.android.dailystatus.SettingsActivity;

import com.actionbarsherlock.view.Window;

public class TwitterDialog extends Dialog {

	private static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);

	private FrameLayout mContent;
	private LinearLayout webViewContainer;
	private WebView mWebView;
	private ImageView mCrossImage;
	private ProgressDialog mSpinner;
	private String mUrl;
	private SettingsActivity parent;

	public TwitterDialog(Context context, String url) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		parent = (SettingsActivity) context;

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mUrl = url;

		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");

		setContentView(R.layout.twitter_web_dialog);

		mContent = (FrameLayout) findViewById(R.id.twitter_web_view_content);
		// можете сами поиграться с LayoutParams, чтобы добиться нужного размера
		// диалога.
		// Display display = getWindow().getWindowManager().getDefaultDisplay();
		// int width = display.getWidth();
		// int height = display.getHeight();
		// LayoutParams lp = new LayoutParams(width * 7 / 8, height * 11 / 14);
		// mContent.setLayoutParams(lp);
		// mContent.setPadding(width / 8, height / 14, 0, 0);
		mContent.setPadding(15, 15, 15, 15);

		webViewContainer = (LinearLayout) findViewById(R.id.twitter_web_view_container);
		mCrossImage = (ImageView) findViewById(R.id.twitter_close_web_dialog);

		mCrossImage.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				TwitterDialog.this.dismiss();
				if (parent.progressDialog != null
						&& parent.progressDialog.isShowing()) {
					parent.progressDialog.dismiss();
				}
			}
		});
		int margin = mCrossImage.getDrawable().getIntrinsicWidth() / 2;
		mCrossImage.setVisibility(View.INVISIBLE);
		setUpWebView(margin);
	}
	
	
	@Override
	protected void onStop() {
		if (parent.progressDialog != null && parent.progressDialog.isShowing()) {
			parent.progressDialog.dismiss();
		}
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (parent.progressDialog != null && parent.progressDialog.isShowing()) {
			parent.progressDialog.dismiss();
		}
	}

	private void setUpWebView(int margin) {
		mWebView = new WebView(parent);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new TwitterDialog.TwWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(FILL);

		webViewContainer.setPadding(margin, margin, margin, margin);
		webViewContainer.addView(mWebView);
	}
	

	private class TwWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			boolean isDenied = false;
			try {
				Uri uri = Uri.parse(url);
				String param = uri.getQuery();
				String name = param.split("=")[0];
				if ("denied".equals(name)) {
					isDenied = true;
				}
			} catch (Exception e) {
			}

			TwitterDialog.this.dismiss();
			if (!isDenied) {
				getContext().startActivity(
						new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			} else {
				if (parent.progressDialog != null
						&& parent.progressDialog.isShowing()) {
					parent.progressDialog.dismiss();
				}
			}
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mSpinner.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mSpinner.dismiss();
			/*
			 * Once webview is fully loaded, set the mContent background to be
			 * transparent and make visible the 'x' image.
			 */
			mContent.setBackgroundColor(Color.TRANSPARENT);
			mWebView.setVisibility(View.VISIBLE);
			mCrossImage.setVisibility(View.VISIBLE);
		}
	}

}
