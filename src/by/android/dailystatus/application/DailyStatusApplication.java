package by.android.dailystatus.application;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.app.Application;
import android.content.Context;

public class DailyStatusApplication extends Application {

	private BitmapLruCache mImageCache;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		if (mImageCache != null) {
			mImageCache.trimMemory();
		}
		super.onLowMemory();
	}

	public BitmapLruCache getImageCache() {
		if (null == mImageCache) {
			mImageCache = new BitmapLruCache(this,
					Constants.IMAGE_CACHE_HEAP_PERCENTAGE);
		}
		return mImageCache;
	}

	public static DailyStatusApplication getApplication(Context context) {
		return (DailyStatusApplication) context.getApplicationContext();
	}
}
