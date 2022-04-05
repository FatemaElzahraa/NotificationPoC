package com.example.notificationpoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.microsoft.windowsazure.messaging.notificationhubs.NotificationHub;
import com.google.firebase.messaging.*;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.Button;
import android.widget.ToggleButton;
import java.io.UnsupportedEncodingException;
import android.content.Context;
import java.util.HashSet;
import android.widget.Toast;
import org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import org.apache.http.HttpStatus;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.DialogInterface;

//import com.google.android.gms.tasks.OnSuccessListener;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private RegisterClient registerClient;
    private static final String BACKEND_ENDPOINT = "https://webapplication120220405103542.azurewebsites.net/";
    FirebaseMessaging fcm;
    String FCM_token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //NotificationHub.setListener(new CustomNotificationListener());
        //NotificationHub.start(this.getApplication(), "dozenpoc", "Endpoint=sb://dozenpoc.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=30cR3iX1JTFgTFeFWDhVzxSCpcAMgBVbZKmzGUm1xc0=");
        //NotificationHub.addTag("UI1");

        //mainActivity = this;
        //FirebaseService.createChannelAndHandleNotifications(getApplicationContext());
        helper.createChannelAndHandleNotifications(getApplicationContext());
        fcm = FirebaseMessaging.getInstance();
        registerClient = new RegisterClient(this, BACKEND_ENDPOINT);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Button sendPush = (Button) findViewById(R.id.sendbutton);
        sendPush.setEnabled(false);
    }

    protected void DialogNotify(String title, String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Send Notification button click handler. This method sends the push notification
     * message to each platform selected.
     *
     * @param v The view
     */
    public void sendNotificationButtonOnClick(View v)
            throws IOException {

        String nhMessageTag = ((EditText) findViewById(R.id.editTextNotificationMessageTag))
                .getText().toString();
        String nhMessage = ((EditText) findViewById(R.id.editTextNotificationMessage))
                .getText().toString();

        // JSON String
        nhMessage = "\"" + nhMessage + "\"";

        if (((ToggleButton)findViewById(R.id.toggleButtonWNS)).isChecked())
        {
            sendPush("wns", nhMessageTag, nhMessage);
        }
        if (((ToggleButton)findViewById(R.id.toggleButtonFCM)).isChecked())
        {
            sendPush("fcm", nhMessageTag, nhMessage);
        }
        if (((ToggleButton)findViewById(R.id.toggleButtonAPNS)).isChecked())
        {
            sendPush("apns", nhMessageTag, nhMessage);
        }
    }

    public void login(View view) throws UnsupportedEncodingException {
        this.registerClient.setAuthorizationHeader(getAuthorizationHeader());

        final Context context = this;
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w("Tag1", "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }

                                    // Get new FCM registration token
                                    FCM_token = task.getResult();
                                    Log.d("TAG2.2", "FCM Registration Token: " + FCM_token);

                                    // Log and toast
                                    //String msg = getString(R.string.msg_token_fmt, token);
                                    //Log.d("TAG2", msg);
                                    //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                    TimeUnit.SECONDS.sleep(1);
                    registerClient.register(FCM_token, new HashSet<String>());
                } catch (Exception e) {
                    DialogNotify("MainActivity - Failed to register", e.getMessage());
                    return e;
                }
                return null;
            }

            protected void onPostExecute(Object result) {
                Button sendPush = (Button) findViewById(R.id.sendbutton);
                sendPush.setEnabled(true);
                Toast.makeText(context, "Signed in and registered.",
                        Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

    private String getAuthorizationHeader() throws UnsupportedEncodingException {
        EditText username = (EditText) findViewById(R.id.usernameText);
        EditText password = (EditText) findViewById(R.id.passwordText);
        String basicAuthHeader = username.getText().toString()+":"+password.getText().toString();
        basicAuthHeader = Base64.encodeToString(basicAuthHeader.getBytes("UTF-8"), Base64.NO_WRAP);
        return basicAuthHeader;
    }

    /**
     * This method calls the ASP.NET WebAPI backend to send the notification message
     * to the platform notification service based on the pns parameter.
     *
     * @param pns     The platform notification service to send the notification message to. Must
     *                be one of the following ("wns", "fcm", "apns").
     * @param userTag The tag for the user who will receive the notification message. This string
     *                must not contain spaces or special characters.
     * @param message The notification message string. This string must include the double quotes
     *                to be used as JSON content.
     */
    public void sendPush(final String pns, final String userTag, final String message)
            throws ClientProtocolException, IOException {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {

                    String uri = BACKEND_ENDPOINT + "/api/notifications";
                    uri += "?pns=" + pns;
                    uri += "&to_tag=" + userTag;

                    HttpPost request = new HttpPost(uri);
                    request.addHeader("Authorization", "Basic "+ getAuthorizationHeader());
                    request.setEntity(new StringEntity(message));
                    request.addHeader("Content-Type", "application/json");

                    HttpResponse response = new DefaultHttpClient().execute(request);

                    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        DialogNotify("MainActivity - Error sending " + pns + " notification",
                                response.getStatusLine().toString());
                        throw new RuntimeException("Error sending notification");
                    }
                } catch (Exception e) {
                    DialogNotify("MainActivity - Failed to send " + pns + " notification ", e.getMessage());
                    return e;
                }

                return null;
            }
        }.execute(null, null, null);
    }
}