package chrisdurning.something;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by chrisdurning on 13/09/2017.
 */

public class SmsReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent)
    {
        Bundle myBundle = intent.getExtras();
        SmsMessage[] messages = null;
        String strMessage = "";

        if (myBundle != null)
        {
            Object [] pdus = (Object[]) myBundle.get("pdus");

            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = myBundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                }
                else {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                strMessage += messages[i].getMessageBody();
            }

            Log.e("SMS", strMessage);
            Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
            Log.i("SMS", strMessage);
            Log.i("SMS", "huhu");
        }
    }
}
