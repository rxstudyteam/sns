package android.etc;

import android.app.Activity;
import android.log.Log;

import kotlin.Unit;
import smart.util.GalleryLoader;

@SuppressWarnings("unused")
public class EastarEggJ {
    private static Activity activity;
    public EastarEggJ(Activity activity) {
        this.activity = activity;
    }
    public static void GALLERY_LOADER() {
//        https://stackoverflow.com/questions/46082806/calling-a-kotlin-higher-order-function-from-java
        GalleryLoader.builder(activity)
            .setCrop(true, 100, 100)
            .setSource(GalleryLoader.Source.GALLERY)
            .setOnGalleryLoadedListener(uri -> {
                Log.e(uri);
                return Unit.INSTANCE;
            })
            .setOnCancelListener(() -> {
                Log.e("error");
                return Unit.INSTANCE;
            })
            .load();
    }
}
