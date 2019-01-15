package android.etc;

import android.app.Activity;
import android.log.Log;
import smart.util.GalleryLoader;

@SuppressWarnings("unused")
public class EastarEggJ {
    private static Activity activity;
    public EastarEggJ(Activity activity) {
        this.activity = activity;
    }

    public static void GALLERY_LOADER() {
        GalleryLoader.builder(activity)
                .setCrop(true, 100, 100)
                .setSource(GalleryLoader.Source.GALLERY)
                .setOnGalleryLoadedListener(uri -> Log.e(uri))
                .setOnCancelListener(() -> Log.e("error"))
                .load();
    }
}
