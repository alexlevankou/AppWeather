package by.alexlevankou.weatherapp.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;

import by.alexlevankou.weatherapp.Constants;

import static android.content.Context.ALARM_SERVICE;

public class BootReceiver extends BroadcastReceiver {



    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if(alarmManager == null) {
                    alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    Intent serviceIntent = new Intent(context, LocationService.class);
                    PendingIntent pIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + Constants.SECOND, 3 * Constants.HOUR, pIntent);
                }
            }
        }
    }
}
