package kr.hs.emirim.ssm.smartschoolmirror;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class PocketSphinx implements RecognitionListener {

    public interface Listener {
        void onSpeechRecognizerReady();

        void onActivationPhraseDetected(String KEYPHRASE);

        void onTimeout();

    }

    private static final String TAG = PocketSphinx.class.getSimpleName();

    public static final String ACTIVATION_KEYPHRASE = "hi mirror";
    public static final String ACTIVATION_KEYPHRASE_time = "what time is it";
    public static final String ACTIVATION_KEYPHRASE_date = "what date is it today";
    public static final String ACTIVATION_KEYPHRASE_food = "what school food";
    public static final String ACTIVATION_KEYPHRASE_schedule = "what schedule";
    public static final String ACTIVATION_KEYPHRASE_weather = "how is the weather";
    public static final String ACTIVATION_KEYPHRASE_mirror = "show mirror";

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String WAKEUP_SEARCH = "wakeup";
    private static final String ACTION_SEARCH = "action";

    private final Listener listener;

    private SpeechRecognizer recognizer;

    public PocketSphinx(Context context, Listener listener) {
        this.listener = listener;
        runRecognizerSetup(context);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.e(TAG, "말하기 시작");
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(WAKEUP_SEARCH)) {
            Log.e(TAG, "말하기 끝");
            recognizer.stop();
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }

        String text = hypothesis.getHypstr();
        if (text.equals(ACTIVATION_KEYPHRASE) || text.contains("time") || text.contains("date") || text.contains("today") ||
                text.contains("weather") || text.contains("food") || text.contains("schedule") || text.contains("mirror")) {
            recognizer.stop();
        } else {
            Log.e(TAG, "부분 음성 결과: " + text);
        }
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }

        String text = hypothesis.getHypstr();
        Log.e(TAG, "음성 결과: " + text);

        if (text.equals(ACTIVATION_KEYPHRASE)) {
            listener.onActivationPhraseDetected(ACTIVATION_KEYPHRASE);
        }
        else if (text.contains("time")) {
            listener.onActivationPhraseDetected(ACTIVATION_KEYPHRASE_time);
        }
        else if (text.contains("today")) {
            listener.onActivationPhraseDetected(ACTIVATION_KEYPHRASE_date);
        }
        else if (text.contains("food")) {
            listener.onActivationPhraseDetected(ACTIVATION_KEYPHRASE_food);
        }
        else if (text.contains("schedule")) {
            listener.onActivationPhraseDetected(ACTIVATION_KEYPHRASE_schedule);
        }
        else if (text.contains("weather")) {
            listener.onActivationPhraseDetected(ACTIVATION_KEYPHRASE_weather);
        }
        else if (text.contains("mirror")) {
            listener.onActivationPhraseDetected(ACTIVATION_KEYPHRASE_mirror);
        }else{
            listener.onActivationPhraseDetected("다시 말씀해주세요.");

        }

    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, "On error", e);
    }

    @Override
    public void onTimeout() {
        Log.e(TAG, "Timeout!");
        recognizer.stop();
        listener.onTimeout();
    }

    public void startListeningToActivationPhrase() {
        Log.e(TAG, "\"ok mirror\"라고 말하세요");
        recognizer.startListening(WAKEUP_SEARCH);
    }

    public void startListeningToAction() {
        Log.e(TAG, "Start listening for some actions with a 10secs timeout");
        recognizer.startListening(ACTION_SEARCH, 10000);
    }

    public void onDestroy() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    private void runRecognizerSetup(final Context context) {
        Log.e(TAG, "Recognizer setup");

        // Recognizer initialization is a time-consuming and it involves IO, so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(context);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Log.e(TAG, "Failed to initialize recognizer: " + result);
                } else {
                    listener.onSpeechRecognizerReady();
                }
            }
        }.execute();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .getRecognizer();
        recognizer.addListener(this);

        // Custom recognizer
        recognizer.addKeyphraseSearch(WAKEUP_SEARCH, ACTIVATION_KEYPHRASE);
       // recognizer.addKeyphraseSearch(ACTION_SEARCH, ACTIVATION_KEYPHRASE_schedule);
      //  File languageModel = new File(assetsDir, "weather.dmp");

        recognizer.addNgramSearch(ACTION_SEARCH, new File(assetsDir, "ssmpredefined.lm.bin"));
    }
}
