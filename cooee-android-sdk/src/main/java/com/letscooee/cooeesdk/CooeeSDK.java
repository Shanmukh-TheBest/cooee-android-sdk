package com.letscooee.cooeesdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import com.letscooee.async.SendEventAsyncNetworkClass;
import com.letscooee.campaign.ImagePopUpActivity;
import com.letscooee.campaign.VideoPopUpActivity;
import com.letscooee.init.PostLaunchActivity;
import com.letscooee.models.Campaign;
import com.letscooee.models.Event;
import com.letscooee.retrofit.APIClient;
import com.letscooee.retrofit.ServerAPIService;
import com.letscooee.utils.CooeeSDKConstants;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * @author Abhishek Taparia
 * The CooeeSdk class contains all the functions required by application to achieve the campaign tasks(Singleton Class)
 */
public class CooeeSDK {

    private Context context;
    public static CooeeSDK cooeeSDK = null;

    private CooeeSDK(Context context) {
        this.context = context;
        new PostLaunchActivity(context).appLaunch();
    }

    // create instance for CooeeSDK (Singleton Class)
    public static CooeeSDK getDefaultInstance(Context context) {
        if (cooeeSDK == null) {
            cooeeSDK = new CooeeSDK(context);
        }
        return cooeeSDK;
    }

    //Sends event to the server and returns with the campaign details
    public void sendEvent(String eventName, Map<String, String> eventProperties) {
        Event event = new Event(eventName, eventProperties);
        Campaign campaign = null;

        try {
            campaign = new SendEventAsyncNetworkClass(this.context).execute(event).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        createCampaign(campaign);
    }

    // Create campaign is received from event
    private void createCampaign(Campaign campaign) {
        if (campaign != null && campaign.getEventName() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("title", campaign.getEventName());
            bundle.putString("mediaURL", campaign.getContent().getMediaUrl());
            bundle.putString("transitionSide", campaign.getContent().getLayout().getDirection());
            bundle.putString("autoClose", campaign.getContent().getLayout().getCloseBehaviour().getAutoCloseTime() + "");

            switch (campaign.getEventName()) {
                case CooeeSDKConstants.IMAGE_CAMPAIGN: {
                    Intent intent = new Intent(this.context, ImagePopUpActivity.class);
                    intent.putExtras(bundle);
                    this.context.startActivity(intent);
                    break;
                }
                case CooeeSDKConstants.VIDEO_CAMPAIGN:
                    Intent intent = new Intent(this.context, VideoPopUpActivity.class);
                    intent.putExtras(bundle);
                    this.context.startActivity(intent);
                    break;
                case CooeeSDKConstants.SPLASH_CAMPAIGN: {
//                TODO: create Splash Campaign Layout class
                    break;
                }
                default: {
                    Log.e(CooeeSDKConstants.LOG_PREFIX + " error", "No familiar campaign");
                }
            }
        }
    }

    //send user data to the server
    public void updateUserData(Map<String, String> userData) throws Exception {
        updateUserProfile(userData, null);
    }

    //send user properties to the server
    public void updateUserProperties(Map<String, String> userProperties) throws Exception {
        updateUserProfile(null, userProperties);
    }

    //send user dat and properties to the server
    public void updateUserProfile(Map<String, String> userData, Map<String, String> userProperties) throws Exception {
        for (String key : userProperties.keySet()) {
            if (key.substring(0, 3).equalsIgnoreCase("ce ")) {
                throw new Exception("User Property cannot start with 'CE '");
            }
        }

        ServerAPIService apiService = APIClient.getServerAPIService();
        String header = this.context.getSharedPreferences(CooeeSDKConstants.SDK_TOKEN, Context.MODE_PRIVATE).getString(CooeeSDKConstants.SDK_TOKEN, "");

        Map<String, Object> userMap = new HashMap<>();
        if (userData != null) userMap.put("userData", userData);
        if (userProperties != null) userMap.put("userProperties", userProperties);

        Call<ResponseBody> call = apiService.updateProfile(header, userMap);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.i(CooeeSDKConstants.LOG_PREFIX + " status", response.code() + "");
            }

            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(CooeeSDKConstants.LOG_PREFIX + " Error", t.toString());
            }
        });
    }
}
