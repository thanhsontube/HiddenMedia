package ts.hn.tstest.a;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import ts.hn.tstest.FilterLog;
import android.os.Messenger;

public class CommMgr implements ICommMgr {

    private static final String TAG = "CommMgr";

    FilterLog log = new FilterLog(TAG);

    public static final int KEY_VIDEO = 1;
    public static final int KEY_AUDIO = 2;
    public static final int KEY_PHOTO = 3;

    private ICallback callbackVideo, callbackAudio, callbackPhoto;

    private HashMap<Messenger, Queue<TsData>> hashMap;
    private HashMap<Integer, Queue<TsData>> hashMap2;

    private TsThread videoThread, audioThread, photoThread;

    public CommMgr() {
        hashMap = new HashMap<Messenger, Queue<TsData>>();
        hashMap2 = new HashMap<Integer, Queue<TsData>>();
        videoThread = new VideoThread();
        audioThread = new AudioThread();
        photoThread = new PhotoThread();
    }

    @Override
    public void addData(Messenger messenger, TsData data) {

        if (!hashMap.containsKey(messenger)) {
            hashMap.put(messenger, new LinkedList<TsData>());
        }
        hashMap.get(messenger).add(data);

        if (!videoThread.isAlive()) {
            videoThread.active();
        }

        if (!audioThread.isAlive()) {
            audioThread.active();
        }

        if (!photoThread.isAlive()) {
            photoThread.active();
        }

    }

    @Override
    public void setCallbackVideo(ICallback callback) {
        this.callbackVideo = callback;

    }

    @Override
    public void setCallbackAudio(ICallback callback) {
        this.callbackAudio = callback;

    }

    @Override
    public void setCallbackPhoto(ICallback callback) {
        this.callbackPhoto = callback;

    }

    private class VideoThread extends TsThread {

        @Override
        protected void handleThread() {
            try {
                TsData data = hashMap2.get(KEY_VIDEO).poll();
                if (data == null) {
                    deactive();
                }
                log.d("log>>>" + "encrypt video");
                if (callbackVideo != null) {
                    callbackVideo.onProcess(50);
                    callbackVideo.onFinished();
                }

                Thread.sleep(10000);

            } catch (Exception e) {
            }

        }

    }

    private class AudioThread extends TsThread {

        @Override
        protected void handleThread() {

        }

    }

    private class PhotoThread extends TsThread {

        @Override
        protected void handleThread() {

        }

    }

    @Override
    public void addData(int key, TsData data) {
        if (!hashMap2.containsKey(key)) {
            hashMap2.put(key, new LinkedList<TsData>());
        }
        hashMap2.get(key).add(data);

        if (!videoThread.isAlive()) {
            videoThread.active();
        }

        if (!audioThread.isAlive()) {
            audioThread.active();
        }

        if (!photoThread.isAlive()) {
            photoThread.active();
        }

    }

}
