package com.blt.mockgps.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.blt.mockgps.Application.MyApp;
import com.blt.mockgps.Database.Entitiy.PeriodEntitiy;
import com.blt.mockgps.Database.Entitiy.PositionsEntitiy;
import com.blt.mockgps.Main.MainActivity;
import com.blt.mockgps.Main.MockLocationClass;
import com.blt.mockgps.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class BackGroundTaskService extends Service {

    private static Timer timer = new Timer();
    private MockLocationClass mockLocationClass;
    private static int pos = -1;
    private static int ChangeCount = 0;
    private static int TryCount = 0;
    private static int RountCount = 0;
    private static long TimeSpend = 0;
    private static long mPeriod ;
    private static Handler mHandler = new Handler();
    private static Box<PeriodEntitiy> PeriodBox;
    private static Box<PositionsEntitiy> positionBox;
    private static PeriodEntitiy periodEntitiy;
    private static List<PositionsEntitiy> list_poses;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void getData(){
        mockLocationClass = new MockLocationClass(getApplicationContext());
        BoxStore boxStore = ((MyApp) getApplication()).getBoxStore();
        positionBox = boxStore.boxFor(PositionsEntitiy.class);
        PeriodBox = boxStore.boxFor(PeriodEntitiy.class);
        periodEntitiy = PeriodBox.get(1);

        if(periodEntitiy==null)
            periodEntitiy= new PeriodEntitiy(0,5);

        mPeriod = periodEntitiy.Period * 1000;

        list_poses = positionBox.getAll();
        ToggleCommand();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getData();
        return START_STICKY;
    }

    private void ToggleCommand(){
        if(list_poses==null || list_poses.size()<1){
           try{Toast.makeText(getApplicationContext(),R.string.YouNeedAddSomeLocationBeforStartAp,Toast.LENGTH_LONG).show();}catch (Exception e){e.printStackTrace();}
            return;
        }

        TimerTask updateLocation = new UpdateLocationTask();
        timer.scheduleAtFixedRate(updateLocation,200,1*1000);
        start();
    }

    private void start(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("Timer","pos Change Command");
                if(pos >= (list_poses.size()-1)) {
                    RountCount++;
                    restStartFromBeging(R.string.YouRichTheEndOfTheLis);
                    pos = 0;
                }else
                    pos++;

                ChangeCount++;
                TimeSpend += mPeriod;

                mHandler.postDelayed(this, mPeriod);
            }
        });
    }

    private void restStartFromBeging(int MessageId){
        try{Toast.makeText(getApplicationContext(),MessageId,Toast.LENGTH_LONG).show();}catch (Exception e){e.printStackTrace();}
        showNotification(getString(MessageId));
    }

    private void showNotification(String eventtext) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(eventtext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(0, n);
    }




    class UpdateLocationTask extends TimerTask {

        @Override
        public void run() {
            Log.i("Timer","Location Change Command");
            if(list_poses==null || list_poses.size()<pos)
                return;

            PositionsEntitiy positionsEntitiy = list_poses.get(pos);


            mockLocationClass.setMock(positionsEntitiy.getLat(),positionsEntitiy.getLng(),20);
            TryCount++;

            String strMessage =" \n"+
                    "ChangeCount ="+ChangeCount +"\n" +
                    "TryCount ="+TryCount +"\n" +
                    "RountCount ="+RountCount +"\n" +
                    "TimeSpend ="+(TimeSpend / 1000)+" Second";
            Log.i("Timer",strMessage);
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {

        timer.cancel();
        LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locMgr.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            try{Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();}catch (Exception ex){ex.printStackTrace();}
            e.printStackTrace();
        }

        super.onDestroy();
    }

}
