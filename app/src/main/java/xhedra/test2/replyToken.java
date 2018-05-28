package xhedra.test2;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Whiplash on 5/29/2018.
 */

public class replyToken extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Log.d("token log", FirebaseInstanceId.getInstance().getToken());
    }
}
