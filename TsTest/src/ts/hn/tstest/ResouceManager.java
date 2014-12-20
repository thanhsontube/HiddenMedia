package ts.hn.tstest;

import java.util.List;

import ts.hn.tstest.dto.DataDbDto;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.sax.StartElementListener;

public class ResouceManager {
    private static ResouceManager instance = null;
    private Context context;
    private SoundPool soundPool = null;
  //AudioManager 
    private AudioManager audioManager;
    
    private List<DataDbDto> listMusic = null;

    public List<DataDbDto> getListMusic() {
        return listMusic;
    }

    public void setListMusic(List<DataDbDto> listMusic) {
        this.listMusic = listMusic;
    }

    public static void createInstace(Context context) {
        instance = new ResouceManager(context);
    }

    public ResouceManager(Context context) {
        this.context = context;
        initialize();
    }

    public static ResouceManager getInstance() {
        return instance;
    }

    public Context getContext() {
        return context;
    }

    private void initialize() {
        createBeep();
        context.startService(ServiceMusic.getIntentService(context));
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    private void createBeep() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    }

    public void beep50() {
        if (soundPool == null) {
            return;
        }
        int iTmp = soundPool.load(context, R.raw.beep50, 1);
        soundPool.play(iTmp, 1, 1, 1, 0, 1f);
    }

    public void beep250() {
        if (soundPool == null) {
            return;
        }
        int iTmp = soundPool.load(context, R.raw.beep250, 1);
        soundPool.play(iTmp, 1, 1, 1, 0, 1f);
    }
}
