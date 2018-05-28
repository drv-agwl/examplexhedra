package xhedra.test2;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Whiplash on 5/29/2018.
 */

public class replyCatcher extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("Received: ", remoteMessage.getMessageId());
        String url = remoteMessage.getNotification().getBody();
        Intent intent = new Intent(getApplicationContext(), reply.class);
        intent.putExtra("URL", url);
        getApplicationContext().startActivity(intent);
    }
}
