package com.letscooee.init;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.letscooee.cooeesdk.CooeeSDK;
import com.letscooee.retrofit.APIClient;
import com.letscooee.retrofit.ServerAPIService;
import com.letscooee.utils.CooeeSDKConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.letscooee.utils.CooeeSDKConstants.LOG_PREFIX;

/**
 * @author Abhishek Taparia
 * AppController Class looks upon the lifecycle of the application, check if app is in foreground or background etc.
 */
public class AppController extends Application implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    private String lastScreen;

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        Log.d(LOG_PREFIX + "AppController", "Foreground");
        Date date = Calendar.getInstance().getTime();
        Map<String, String> eventProperties = new HashMap<>();
        eventProperties.put("time", date.toString());
        CooeeSDK.getDefaultInstance(null).sendEvent("isForeground", eventProperties);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Log.d(LOG_PREFIX + "AppController", "Background");
        Date date = Calendar.getInstance().getTime();
        Map<String, String> eventProperties = new HashMap<>();
        Map<String, String> userProperties = new HashMap<>();
        eventProperties.put("time", date.toString());
        CooeeSDK.getDefaultInstance(null).sendEvent("isBackground", eventProperties);

        userProperties.put("lastScreen", lastScreen);
        String header = getApplicationContext().getSharedPreferences(CooeeSDKConstants.SDK_TOKEN, Context.MODE_PRIVATE).getString(CooeeSDKConstants.SDK_TOKEN, "");
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userProperties", userProperties);
        ServerAPIService apiService = APIClient.getServerAPIService();
        apiService.updateProfile(header, userMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(LOG_PREFIX + " userProperties", response.code() + "");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(LOG_PREFIX + " bodyError2", t.toString());
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.d("ActivityStarted", activity.getLocalClassName());
        lastScreen = activity.getLocalClassName();
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.d("ActivityStopped", activity.getLocalClassName());

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.d("ActivityDestroyed", activity.getLocalClassName());
    }
}