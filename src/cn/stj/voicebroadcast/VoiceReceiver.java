package cn.stj.voicebroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class VoiceReceiver extends BroadcastReceiver {

	private static final String TAG = VoiceReceiver.class.getSimpleName();
	private static final boolean DEBUG = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (DEBUG)
			Log.d(TAG, "onReceive------>>");
		if (intent != null) {
			Intent voiceIntent = new Intent(context, VoiceService.class);
			String speakText = intent.getStringExtra(VoiceService.SPEAKTEXT);
			voiceIntent.putExtra(VoiceService.SPEAKTEXT, speakText);
			context.startService(voiceIntent);
		}

	}

}
