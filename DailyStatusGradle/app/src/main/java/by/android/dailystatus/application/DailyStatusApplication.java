package by.android.dailystatus.application;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import uk.co.senab.bitmapcache.BitmapLruCache;

public class DailyStatusApplication extends Application {

    private BitmapLruCache mImageCache;

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);
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
