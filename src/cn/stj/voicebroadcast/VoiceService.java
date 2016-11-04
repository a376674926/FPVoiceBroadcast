package cn.stj.voicebroadcast;

import java.util.HashMap;
import java.util.Locale;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

public class VoiceService extends Service implements
		TextToSpeech.OnInitListener {

	private static final String TAG = VoiceService.class.getSimpleName();
	public static final String SPEAKTEXT = "speaktext";
	private static final boolean DEBUG = false;
	private static final String IFLYTEK_ENGINE = "com.iflytek.tts";
	private static final String PARAM_UTTERANCE_ID = "param_utterance_id";
	private TextToSpeech mTts;
	private String mSpeakText;
	private boolean isTtsInit;
	private MyUtteranceProgressListener mListener = new MyUtteranceProgressListener();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mTts = new TextToSpeech(this, this, IFLYTEK_ENGINE);
		mTts.setOnUtteranceProgressListener(mListener);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			mSpeakText = intent.getStringExtra(SPEAKTEXT);
		}
		if (isTtsInit) {
			speakOut(mSpeakText);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			isTtsInit = true;
			int result = mTts.setLanguage(Locale.CHINESE);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				if (DEBUG) {
					Log.e("TTS", "This Language is not supported");
					stopSelf() ;
				}
			} else {
				if (DEBUG) {
					Log.d(TAG, "onInit");
				}
				speakOut(mSpeakText);
			}
		} else {
			if (DEBUG){
			    Log.e("TTS", "Initilization Failed!");
			}
			isTtsInit = false ;
			stopSelf() ;
		}

	}

	private void speakOut(String speakText) {
		if (DEBUG) {
			Log.d(TAG, "speakOut---->>speakText:" + speakText);
		}
		HashMap myHashAlarm = new HashMap();
		myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
				String.valueOf(AudioManager.STREAM_ALARM));
		myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
				PARAM_UTTERANCE_ID);
		mTts.speak(speakText, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
	}

	@Override
	public void onDestroy() {
		if (DEBUG) {
			Log.d(TAG, "onDestroy");
		}
		if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}
		mListener = null;
		super.onDestroy();
	}

	private class MyUtteranceProgressListener extends UtteranceProgressListener {

		@Override
		public void onStart(String utteranceId) {
		}

		/**
		 * Called when an utterance has successfully completed processing. All
		 * audio will have been played back by this point for audible output,
		 * and all output will have been written to disk for file synthesis
		 * requests.
		 */
		@Override
		public void onDone(String utteranceId) {
			if (DEBUG)
				Log.d(TAG, "UtteranceProgressListener-->>onDone");
			VoiceService.this.stopSelf();
		}

		@Override
		public void onError(String utteranceId) {
		}

	}

}
