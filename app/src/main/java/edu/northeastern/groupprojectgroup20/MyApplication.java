package edu.northeastern.groupprojectgroup20;

import android.app.Application;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import edu.northeastern.groupprojectgroup20.healthconnect.HealthDataFetchWorker;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d("MyApplication", "onCreate: ");
//        PeriodicWorkRequest healthDataFetchRequest =
//                new PeriodicWorkRequest.Builder(HealthDataFetchWorker.class, 1, TimeUnit.HOURS)
//                .build();
////        WorkManager.getInstance(this).enqueue(healthDataFetchRequest);
//
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//                "healthDataFetchWork",
//                ExistingPeriodicWorkPolicy.KEEP,
//                healthDataFetchRequest
//        );
//
//        Log.d("MyApplication", "onCreate: work start");
    }
}
