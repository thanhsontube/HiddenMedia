package ts.hn.tstest.utils;

import android.content.Context;
import android.media.AudioManager;

public class TsVolume {
    static TsVolume instance = null;
    private Context context;
    private AudioManager audioManager;
    public static void createInstace(Context context) {
        instance = new TsVolume(context);
    }

    public TsVolume(Context context) {
        this.context = context;
        initialize();
    }

    public static TsVolume getInstance() {
        return instance;
    }
    
    private void initialize() {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }
    
    public int getMax() {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }
    
    public int getCurrent() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }
    
    public void setVolume (int volume) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }
}
