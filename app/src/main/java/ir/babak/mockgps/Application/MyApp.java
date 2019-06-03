package ir.babak.mockgps.Application;

import android.app.Application;
import android.util.Log;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;
import ir.babak.mockgps.BuildConfig;
import ir.babak.mockgps.Database.Entitiy.MyObjectBox;

public class MyApp extends Application {

    private String TAG = "Application";
    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();

        initializeObjectBox();
    }

    private void initializeObjectBox(){
        boxStore = MyObjectBox.builder().androidContext(MyApp.this).build();

        if(BuildConfig.DEBUG){
            boolean started = new AndroidObjectBrowser(boxStore).start(this);
            Log.d(TAG,"ObjectBrowser is Startded: "+started);
        }

        Log.d(TAG,"Using ObjectBox "+ BoxStore.getVersion()
                + " ("+BoxStore.getVersionNative() +")");
    }

    public BoxStore getBoxStore(){
        return boxStore;
    }
}
