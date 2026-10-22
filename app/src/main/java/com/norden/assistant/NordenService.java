package com.norden.assistant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NordenService extends Service {
    static final String CHANNEL = "norden_channel";
    static final String ACTION_STOP = "norden.STOP";
    static final String ACTION_SPEAKING = "norden.SPEAKING";
    static final String ACTION_TEXT = "norden.TEXT";

    private static final String[] WAKE_WORDS = {
            "hello norden", "hallo norden", "हेलो नोडेन", "हैलो नोडेन", "नोडेन", "norden"
    };

    private static final Map<String, String> ALIASES = new HashMap<>();
    static {
        ALIASES.put("व्हाट्सएप", "whatsapp");
        ALIASES.put("इंस्टाग्राम", "instagram");
        ALIASES.put("यूट्यूब", "youtube");
        ALIASES.put("फेसबुक", "facebook");
        ALIASES.put("क्रोम", "chrome");
        ALIASES.put("कैमरा", "camera");
        ALIASES.put("सेटिंग्स", "settings");
        ALIASES.put("कैलकुलेटर", "calculator");
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private SpeechRecognizer recognizer;
    private boolean started = false;
    private boolean speaking = false;
    private boolean running = true;

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int r = tts.setLanguage(new Locale("hi", "IN"));
                if (r < 0) tts.setLanguage(Locale.US);
                ttsReady = true;
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override public void onStart(String id) {}
                    @Override public void onDone(String id) { finishSpeaking(); }
                    @Override public void onError(String id) { finishSpeaking(); }
                });
            }
        });
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopNorden();
            return START_NOT_STICKY;
        }
        ServiceCompat.startForeground(this, 1, buildNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
        if (!started) {
            started = true;
            listen();
        }
        return START_STICKY;
    }

    private void stopNorden() {
        running = false;
        handler.removeCallbacksAndMessages(null);
        if (recognizer != null) recognizer.cancel();
        stopSelf();
    }

    private void listen() {
        if (!running || speaking) return;
        handler.post(() -> {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
            i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
            try {
                recognizer.startListening(i);
            } catch (Exception e) {
                handler.postDelayed(this::listen, 1000);
            }
        });
    }

    private final RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onResults(Bundle results) {
            ArrayList<String> m = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = (m != null && !m.isEmpty()) ? m.get(0).toLowerCase(Locale.ROOT).trim() : "";
            handleCommand(text);
            listen();
        }
        @Override public void onError(int error) {
            handler.postDelayed(NordenService.this::listen, 800);
        }
        @Override public void onReadyForSpeech(Bundle params) {}
        @Override public void onBeginningOfSpeech() {}
        @Override public void onRmsChanged(float rmsdB) {}
        @Override public void onBufferReceived(byte[] buffer) {}
        @Override public void onEndOfSpeech() {}
        @Override public void onPartialResults(Bundle partialResults) {}
        @Override public void onEvent(int eventType, Bundle params) {}
    };
}
