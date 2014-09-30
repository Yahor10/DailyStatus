package by.android.dailystatus.application;

import android.app.Application;
import android.content.Context;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import uk.co.senab.bitmapcache.BitmapLruCache;

// TODO create correct data base
//@ReportsCrashes( formKey = "",
//formUri = "https://sekt.cloudant.com/acra-doroga/_design/acra-storage/_update/report",
//reportType = org.acra.sender.HttpSender.Type.JSON,
//httpMethod = org.acra.sender.HttpSender.Method.PUT,
//formUriBasicAuthLogin="allionessinesirstedlefte",
//formUriBasicAuthPassword="Tbuv86xe4mgTJjLNDEGH1orf")
@ReportsCrashes(formKey = "", formUri = "https://sekt.cloudant.com/acra-doroga/_design/acra-storage/_update/report")
public class DailyStatusApplication extends Application {

    private BitmapLruCache mImageCache;

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
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
