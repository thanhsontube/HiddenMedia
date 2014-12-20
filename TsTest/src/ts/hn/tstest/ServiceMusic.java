package ts.hn.tstest;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ts.hn.tstest.db.TsSqlite;
import ts.hn.tstest.dto.DataDbDto;
import ts.hn.tstest.utils.TimerUtil;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class ServiceMusic extends Service {

    private static final String TAG = "ServiceMusic";
    private Messenger messenger;
    private Stack<Messenger> stackMessenger;
    private FilterLog log = new FilterLog(TAG);

    private MediaPlayer player;
    private List<DataDbDto> list = new ArrayList<DataDbDto>();
    private TsSqlite sqlite;

    // current position
    private int currentPosition = 0;

    /** repeat off functionality indicator */
    private boolean isRepeatOff = false;

    /** repeat track functionality indicator */
    private boolean isRepeatTrack = false;

    /** repeat folder functionality indicator */
    private boolean isRepeatFolder = false;

    /** random folder functionality indicator */
    private boolean isRandomFolder = false;

    /** Came back from list files activity */
    private boolean fromAllListFiles = false;

    /** Came back from list folder activity */
    private boolean fromFolderScreen = false;

    /** random all functionality indicator */
    private boolean isRandomAll = false;

    @SuppressLint("HandlerLeak")
    private class IncommingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            // AFTER MUSIC PLAYER FINISH ASYNCTASK
            case MsConst.TS_MEDIA_POSITION:
                list.clear();
                list.addAll(sqlite.getListData(Constants.AUDIO));
                currentPosition = msg.arg1;
                playSong(currentPosition);
                pauseSong();
                break;
            case MsConst.TS_REGISTER:
                stackMessenger.add(msg.replyTo);
                break;
            case MsConst.TS_UNREGISTER:
                stackMessenger.remove(msg.replyTo);
                break;

            case MsConst.TS_PLAY_POSITION:
                playSong();
                break;
            case MsConst.TS_PAUSE:
                pauseSong();
                break;
            case MsConst.TS_STOP:
//                stopSong();
                if(player != null && player.isPlaying()) {
                    pauseSong();
                    player.seekTo(0);
                }
                break;
            case MsConst.TS_RESUME:
                resumeSong();
                break;
            case MsConst.TS_CURRENT:
                sendCurrentToClient(currentPosition);
                break;
            case MsConst.TS_NEXT:
                playNextSong();
                break;
            case MsConst.TS_PREV:
                playPrevSong();
                break;
                //when user use seekbar
            case MsConst.TS_MOVE_TO_POSITION:
                int position = msg.arg1;
                moveToPostion(position);
                break;

            default:
                break;
            }
            // super.handleMessage(msg);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sqlite = TsSqlite.getInstance();
        messenger = new Messenger(new IncommingHandler());
        stackMessenger = new Stack<Messenger>();

        player = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    public static Intent getIntentService(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ServiceMusic.class);
        return intent;
    }

    private void playSong(final int index) {
        try {
            // log.d("log>>>" + "path:" + resource.getListMusic().get(index).path);
            player.reset();
            player.setDataSource(list.get(index).path);
            player.setOnErrorListener(new OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    log.e("log>>>" + "onError");
                    currentPosition++;
                    if (currentPosition == list.size()) {
                        currentPosition = 0;
                    }
                    playSong(currentPosition);
                    return false;
                }
            });
            player.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer arg0) {
                    currentPosition++;
                    if (currentPosition == list.size()) {
                        currentPosition = 0;
                    }
                    playSong(currentPosition);
                }
            });
            player.prepare();
            player.start();
            sendStatusToClient(currentPosition);
            sendInfoMediaToClient(currentPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void playSong() {
        player.start();
        sendStatusToClient(currentPosition);
    }

    private void pauseSong() {
        if (player.isPlaying()) {
            player.pause();
            sendStatusToClient(currentPosition);
        }
    }

    private void resumeSong() {
        player.start();
        sendStatusToClient(currentPosition);
    }

    private void stopSong() {
        if (player.isPlaying()) {
            player.stop();
            sendStatusToClient(currentPosition);
        }
    }

    private void playNextSong() {
        currentPosition++;
        if (currentPosition >= list.size()) {
            currentPosition = 0;
            playSong(currentPosition);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pauseSong();
        } else {
            playSong(currentPosition);
        }
    }

    private void playPrevSong() {
        currentPosition = currentPosition - 1;
        if (currentPosition < 0) {
            currentPosition = 0;
            playSong(currentPosition);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
            }
            pauseSong();
        } else {
            playSong(currentPosition);
        }
    }

    /**
     * send the info of audio to client
     * 
     * @param index
     *            is position of audio file in list
     *            
     */
    private void sendInfoMediaToClient(int index) {
        Bundle bundle = new Bundle();
        
        DataDbDto dto = list.get(index);
        bundle.putString(MsConst.KEY_MEDIA_NAME, list.get(index).name);
        bundle.putString(MsConst.KEY_MEDIA_ARTIST, list.get(index).artist);
        bundle.putInt(MsConst.KEY_MEDIA_DURATION, player.getDuration());
        bundle.putInt(MsConst.KEY_MEDIA_CURRENT, player.getCurrentPosition());
        bundle.putString(MsConst.KEY_FOLDER, list.get(index).folder);
        bundle.putString(MsConst.KEY_PATH, list.get(index).path);
        for (Messenger messenger : stackMessenger) {
            Message msg = Message.obtain(null, MsConst.TS_MEDIA_INFO, index, 0);
            msg.setData(bundle);
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCurrentToClient(int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(MsConst.KEY_MEDIA_DURATION, player.getDuration());
        bundle.putInt(MsConst.KEY_MEDIA_CURRENT, player.getCurrentPosition());
        for (Messenger messenger : stackMessenger) {
            Message msg = Message.obtain(null, MsConst.TS_CURRENT, index, 0);
            msg.setData(bundle);
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendStatusToClient(int index) {

        for (Messenger messenger : stackMessenger) {
            Message msg = Message.obtain(null, MsConst.TS_MEDIA_STATUS, index, 0);
            try {
                int status = 0;
                if (player.isPlaying()) {

                    status = MsConst.STATUS_PLAY;

                } else {
                    status = MsConst.STATUS_PAUSE;

                }
                msg.arg1 = status;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        
    }
    private void moveToPostion(int position) {
        if(player == null) {
            return;
        }
        int gotohere = TimerUtil.progressToTimer(position, player.getDuration());
        player.seekTo(gotohere);
        sendCurrentToClient(currentPosition);
    }

}
