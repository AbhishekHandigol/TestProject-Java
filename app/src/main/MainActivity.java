package com.aware.androidupdatetemplateapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import android.app.DownloadManager;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

//Notifications need a notification channel, notification builder and a notification manager.
public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_1_ID = "channel1"; //create notification channel
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) { //page is initialized
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //keeps track of the requests in a queue
        queue = Volley.newRequestQueue(this);
    }

    public void checkForUpdates(View view) {
        final TextView textView = (TextView) findViewById(R.id.text); //reference to the form
        //sending an http request to the server -  encoding the url /updates
        String localhost = "http://10.0.2.2:5000/updates";
        JsonObjectRequest stringRequest = //sends a get request
                new JsonObjectRequest(Request.Method.GET, localhost, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) { //json response back with app updates
                                if (response != null) {
                                    JSONObject link = null;
                                    try {
                                        link = response.getJSONObject("link");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String notiflink = null;
                                    try {
                                        notiflink = link.getString("link");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String appID = null;
                                    try {
                                        appID = response.getJSONObject("appID").getString("appID");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //parses the JSON object response and gets the appID of the app that needs
                                    //to be updated and the installation link
                                    textView.setText(response.toString()); //telling the user a message
                                    //displays the notification as an output
                                    displayNotifications(notiflink, appID);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textView.setText("That didn't work!");
                    }
                });
        queue.add(stringRequest);
        Handler handler = new Handler();
        Runnable runnable = new Runnable();
        checkForUpdates(view);
        //this facilitates the automation part of the project, where the app checks for updates every
        //five minutes without the user having to click "check for updates".
        handler.postDelayed(runnable, 60000);
    }

    private void displayNotifications(String notiflink, String appID) {
        //displays the notification by initializing a builder, manager, channel
        //as well as other parts like the title and content (which includes the appID and the notification link)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setContentTitle("New Update").setContentText("A new update of app " + appID + "has arrived!" +
                        notiflink).setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    //implement functionality - once the user clicks on the notification, download the update
    //and let the user know the update has been installed.
    //my.test.app.UpdateDownloader updateDownloader = new my.test.app.UpdateDownloader();
    //registerReceiver(updateDownloader,
    // new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
}