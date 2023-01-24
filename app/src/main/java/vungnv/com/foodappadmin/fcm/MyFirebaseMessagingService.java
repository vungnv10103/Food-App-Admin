package vungnv.com.foodappadmin.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import vungnv.com.foodappadmin.MainActivity;
import vungnv.com.foodappadmin.constant.Constant;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService implements Constant {
    private static final String SERVER_KEY = "AAAAtObMYUE:APA91bGELZcoHbSiWwdje7v9V0_QgMWyDVj201R0nuUfqVM3OUFbDVq5K8ZeZ0DTpZ7m2UVwBLUUf7gVzhnUT5hD7PBWZvlZ1JLuF8_HsGGbTmdaXqzkiSkB9NUC_Bb06IRZr-MKPHgO";
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        if (notification == null) {
            return;
        }
        String title = notification.getTitle();
        String message = notification.getBody();
        sendNotification(title, message);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        // Send the notification
//        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
//        sendNotification1(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        super.onMessageReceived(remoteMessage);


    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
//                .setSmallIcon(R.drawable.icon_notification_client)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }

    }

//    private void sendNotification1(final String title, final String body1) {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, FCM_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d(TAG, "Notification sent successfully: " + response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "Failed to send notification: " + error.getMessage());
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "key=" + SERVER_KEY);
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                String body = "{\"to\": \"registration_token\", \"notification\": {\"title\": \"" + title + "\", \"body\": \"" + body1 + "\"}}";
//                return body.getBytes();
//            }
//        };
//        requestQueue.add(stringRequest);
//    }

}
