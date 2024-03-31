package com.as.fpphone.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class RingtoneHelper {
  static Ringtone ringtone;
    private static Vibrator vibrator;

    public static void playRingtone(Context context){
        if (ringtone == null) {
            ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        }
        if (ringtone != null && !ringtone.isPlaying()) {
            ringtone.play();
            //Vibrate if device is not in silent mode
            if(!isDeviceInSilentMode(context)) {
                vibrate(context, new long[]{100, 200, 300, 400, 500});
            }
        }
    }


    public static void stopRinging(){
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            stopVibration();
        }
    }

    public static void vibrate(Context context, long[] pattern){
        if(vibrator == null){
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        if(vibrator!=null && vibrator.hasVibrator()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern,0));
            }
            else {
                vibrator.vibrate(pattern,0);
            }
        }
    }

    public static void stopVibration() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.cancel();
        }
    }

    private static boolean isDeviceInSilentMode(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int mode = audioManager.getRingerMode();
            if(mode == AudioManager.RINGER_MODE_SILENT){
                return true; // silent
            }
        }
        return false; // not silent
    }
}
