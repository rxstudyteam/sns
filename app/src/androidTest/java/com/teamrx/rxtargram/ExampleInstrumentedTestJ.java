package com.teamrx.rxtargram;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamrx.rxtargram.model.ProfileModel;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.test.InstrumentationRegistry;

import static com.teamrx.rxtargram.repository.RemoteAppDataSource.USER_COLLECTION;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 * <p>
 * See [testing documentation](http://d.android.com/tools/testing).
 */
public class ExampleInstrumentedTestJ {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.teamrx.rxtargram", appContext.getPackageName());
    }
    @Test
    public void sdf() {
        String user_id = null;
        Executors.newSingleThreadExecutor().submit(new Callable<ProfileModel>() {
            @Override
            public ProfileModel call() throws Exception {
                Task<DocumentSnapshot> task = FirebaseFirestore.getInstance()
                        .collection(USER_COLLECTION).document(user_id)
                        .get();
                DocumentSnapshot document = Tasks.await(task, 5, TimeUnit.SECONDS);
                if (document.exists())
                    return document.toObject(ProfileModel.class);
                return null;
            }
        });
    }
    @Test
    public void sdfssss() {
        Activity context = null;
        byte[] imageUrl = new byte[0];
        ImageView imageView = null;
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(new BitmapImageViewTarget(imageView) {
                });
    }
}
//    @BindingAdapter(value = {"imageUrl"})
//    public static void setImage(ImageView imageView, String url) {
//        Log.e(url);
//    }


