package kr.hs.emirim.ssm.smartschoolmirror;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

public class TtsSpeaker implements TextToSpeech.OnInitListener {

    interface Listener {
        void onTtsInitialized();

        void onTtsSpoken();
    }

    private static final String TAG = TtsSpeaker.class.getSimpleName();
    private static final String UTTERANCE_ID = BuildConfig.APPLICATION_ID + ".UTTERANCE_ID";

    private Listener listener;
    private boolean isInitialized = false;
    private TextToSpeech ttsEngine;

    public TtsSpeaker(Context context, Listener listener) {
        this.listener = listener;
        ttsEngine = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            ttsEngine.setLanguage(Locale.KOREAN);
            if (status == TextToSpeech.SUCCESS) {
                Log.e(TAG, "TTS 기능 초기화 성공");
                ttsEngine.setPitch(1.2f);//높이
                ttsEngine.setSpeechRate(1.0f);//속도
            } else {
                Log.e(TAG, "TTS 초기화 실패 : " + status);
                ttsEngine = null;
            }
            ttsEngine.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.i(TAG, "onStart");
                }

                @Override
                public void onDone(String utteranceId) {
                    Log.i(TAG, "onDone");
                    listener.onTtsSpoken();
                }

                @Override
                public void onError(String utteranceId, int errorCode) {
                    Log.w(TAG, "onError (" + utteranceId + ")" + ". Error code: " + errorCode);
                }

                @Override
                public void onError(String utteranceId) {
                    Log.w(TAG, "onError");
                }
            });

            ttsEngine.setPitch(1f);
            ttsEngine.setSpeechRate(1f);

            isInitialized = true;
            Log.i(TAG, "TTS initialized successfully");
            listener.onTtsInitialized();
        } else {
            Log.w(TAG, "Could not open TTS Engine (onInit status=" + status + "). Ignoring text to speech");
            ttsEngine = null;
        }
    }

    public void say(String message) {
        if (!isInitialized || ttsEngine == null) {
            Log.w(TAG, "TTS is not initialized yet, be patient");
            return;
        }

        ttsEngine.speak(message, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID);
    }

    public void onDestroy() {
        if (ttsEngine != null) {
            ttsEngine.stop();
            ttsEngine.shutdown();
        }
    }
}
