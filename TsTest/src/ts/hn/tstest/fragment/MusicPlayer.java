package ts.hn.tstest.fragment;

import java.util.ArrayList;
import java.util.List;

import ts.hn.tstest.Constants;
import ts.hn.tstest.FilterLog;
import ts.hn.tstest.MsConst;
import ts.hn.tstest.R;
import ts.hn.tstest.ServiceMusic;
import ts.hn.tstest.db.TsSqlite;
import ts.hn.tstest.dto.DataDbDto;
import ts.hn.tstest.utils.TimerUtil;
import ts.hn.tstest.utils.TsVolume;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MusicPlayer extends Fragment implements OnClickListener, SurfaceTextureListener {

    private static final String TAG = "MusicPlayer";
    FilterLog log = new FilterLog(TAG);

    // waiting dialog
    private ProgressDialog dialog;
    // sqlite mgr
    private TsSqlite sqlite;
    // thread load data
    private TaskLoading task;

    // comunicate between fragment and service
    private ServiceMusic tsService;
    private boolean isBound = false;

    private Messenger messengerService;
    private Messenger messengerClients;

    // list
    private List<DataDbDto> listVideos = new ArrayList<DataDbDto>();
    private List<DataDbDto> listFolder = new ArrayList<DataDbDto>();
    private List<DataDbDto> listFile = new ArrayList<DataDbDto>();

    // status
    private int status = MsConst.STATUS_PAUSE;

    // layout
    // the view displays video.
    private TextureView textureView;
    private Surface surface;

    private SeekBar seekbar;

    // handle to control UI0
    private Handler mHandler = new Handler();

    // media control
    private TextView txtName, txtSize, txtCurrent, txtDuration;
    private ImageView btnFolder, btnPrev, btnPlay, btnNext, btnStop;
    private ImageView btnMute, btnMaxVolume;
    private SeekBar seekbarVolume;
    private View viewSeekbar, viewControl, viewList, viewControlChild;

    // layout the list media
    private ImageView btnSwapToControl;
    private TextView txtAmount, txtPositionSong;

    // view swap
    private View swapFolder, swapFile;

    private final String defaultText = "00:00:00";
    /** flag to check whether video is being played in full screen */
    private boolean isFullScreen = false;
    private TsVolume tsVolume;
    
    //bumdle data
    private int contentid;
    private String selectedFolder, selectedFile;

    public static MusicPlayer getInstance(int arg) {
        MusicPlayer f = new MusicPlayer();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.music_player_fragment, container, false);
        MusicPlayer.this.getActivity().bindService(ServiceMusic.getIntentService(getActivity()), mConnection,
                Context.BIND_AUTO_CREATE);
        initData();
        initLayout(rootView);
        initListener();
        updateLayout(null, null, 0, 0);
        updateStatus(status);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        Message msg = new Message();
        msg.what = MsConst.TS_UNREGISTER;
        msg.replyTo = messengerClients;

        try {
            messengerService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (isBound) {
            getActivity().unbindService(mConnection);

        }
        isBound = false;
        super.onDestroyView();
    }

    @SuppressLint("HandlerLeak")
    private class ClientInCommingMsg extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String name;
            int current;
            int duration;
            Bundle bundle;
            switch (msg.what) {
            case MsConst.TS_NEXT:
                break;
            // get status from service
            case MsConst.TS_MEDIA_INFO:
                bundle = msg.getData();
                name = bundle.getString(MsConst.KEY_MEDIA_NAME);
                current = bundle.getInt(MsConst.KEY_MEDIA_CURRENT);
                duration = bundle.getInt(MsConst.KEY_MEDIA_DURATION);
                selectedFolder = bundle.getString(MsConst.KEY_FOLDER);
                selectedFile = bundle.getString(MsConst.KEY_MEDIA_NAME);
                
                updateLayout(name, null, current, duration);
                mHandler.removeCallbacks(updateUI);
                mHandler.post(updateUI);

                log.d("log>>>" + "TS_MEDIA_INFO:" + name);
                break;

            // get status
            case MsConst.TS_MEDIA_STATUS:
                status = msg.arg1;
                updateStatus(status);
                mHandler.removeCallbacks(updateUI);
                mHandler.post(updateUI);
                break;
            case MsConst.TS_CURRENT:
                bundle = msg.getData();
                current = bundle.getInt(MsConst.KEY_MEDIA_CURRENT);
                duration = bundle.getInt(MsConst.KEY_MEDIA_DURATION);
                updateLayout(null, null, current, duration);

            default:
                break;
            }
            super.handleMessage(msg);
        }
    }

    private void initData() {
        sqlite = TsSqlite.getInstance();
        task = new TaskLoading();
        task.execute(Constants.AUDIO);
        messengerClients = new Messenger(new ClientInCommingMsg());
        tsVolume = TsVolume.getInstance();

    }

    private void initLayout(View rootView) {
        textureView = (TextureView) rootView.findViewById(R.id.music_textureview);
        seekbar = (SeekBar) rootView.findViewWithTag("seekbar");
        txtName = (TextView) rootView.findViewWithTag("name");
        txtSize = (TextView) rootView.findViewWithTag("size");
        txtCurrent = (TextView) rootView.findViewWithTag("current");
        txtDuration = (TextView) rootView.findViewWithTag("duration");

        btnFolder = (ImageView) rootView.findViewWithTag("folder");
        btnPrev = (ImageView) rootView.findViewWithTag("prev");
        btnPlay = (ImageView) rootView.findViewWithTag("play");
        btnNext = (ImageView) rootView.findViewWithTag("next");
        btnStop = (ImageView) rootView.findViewWithTag("stop");
        btnMute = (ImageView) rootView.findViewWithTag("mute");
        btnMaxVolume = (ImageView) rootView.findViewWithTag("max_volume");

        seekbarVolume = (SeekBar) rootView.findViewWithTag("seek_volume");
        seekbarVolume.setMax(tsVolume.getMax());
        seekbarVolume.setProgress(tsVolume.getCurrent());
       
       
    }

    private void initListener() {

        if (textureView == null) {
            Log.e("", ">>> error initLayout first !!!!");
            return;
        }
        textureView.setSurfaceTextureListener(this);
        textureView.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(seekbarListener);

        btnFolder.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnMute.setOnClickListener(this);
        btnMaxVolume.setOnClickListener(this);
        seekbarVolume.setOnSeekBarChangeListener(volumeListener);

    }

    /**
     * update layout of name, artist, seek bar, status, duration
     */

    private void updateStatus(int status) {
        switch (status) {
        case MsConst.STATUS_PLAY:
            break;

        default:
            break;
        }
    }

    private void updateLayout(String name, String artist, int current, int duration) {
        if (!TextUtils.isEmpty(name)) {
            txtName.setText(name);
        }

        if (!TextUtils.isEmpty(artist)) {
            txtSize.setText(artist);
        }

        txtCurrent.setText(TimerUtil.milliSecondsToTimer(current));
        txtDuration.setText(TimerUtil.milliSecondsToTimer(duration));
        int progress = TimerUtil.getProgressPercentage(current, duration);
        seekbar.setProgress(progress);

        switch (status) {
        case MsConst.STATUS_PLAY:

            break;

        default:
            break;
        }
    }

    /**
     * thread update current of song
     */

    private Runnable updateUI = new Runnable() {

        @Override
        public void run() {
            if (status == MsConst.STATUS_PLAY) {
                Message msg = Message.obtain(null, MsConst.TS_CURRENT);
                try {
                    messengerService.send(msg);
                    mHandler.postDelayed(this, 1000);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * service connection
     */

    ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            log.d("log>>>" + "onServiceConnected");
            isBound = true;
            messengerService = new Messenger(service);

            // register client messenger to service
            Message msg = new Message();
            msg.what = MsConst.TS_REGISTER;
            msg.replyTo = messengerClients;

            try {
                messengerService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
            messengerService = null;
        }

    };

    private class TaskLoading extends AsyncTask<Integer, Void, Void> {
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, null);
            sqlite.install(cursor, Constants.AUDIO);
            listVideos.clear();
            listVideos.addAll(sqlite.getListData(Constants.AUDIO));
            log.d("log>>>" + "doInBackground listVideos:" + listVideos.size());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
            try {
                Message msg = Message.obtain(null, MsConst.TS_MEDIA_POSITION, 4, 0);
                messengerService.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == textureView) {

        }
        if (v == btnFolder) {
            
            listener.onMusicFolderClick(Constants.AUDIO, selectedFolder, selectedFile);
        }

        if (v == btnPrev) {
            sendCmdToService(MsConst.TS_PREV);
        }

        if (v == btnPlay) {
            if (status == MsConst.STATUS_PAUSE) {
                sendCmdToService(MsConst.TS_PLAY_POSITION);
            }

            if (status == MsConst.STATUS_PLAY) {
                sendCmdToService(MsConst.TS_PAUSE);
            }
        }

        if (v == btnNext) {
            sendCmdToService(MsConst.TS_NEXT);
        }

        if (v == btnMute) {
            tsVolume.setVolume(0);
            seekbarVolume.setProgress(0);
        }

        if (v == btnMaxVolume) {

        }

        if (v == btnStop) {
            sendCmdToService(MsConst.TS_STOP);
        }

        if (v == btnSwapToControl) {
        }

        if (v == swapFolder) {
        }

        if (v == swapFile) {
        }
    }

    // TODO seekbar listener

    private OnSeekBarChangeListener seekbarListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            sendCmdToService(MsConst.TS_MOVE_TO_POSITION, progress);
        }
    };

    private OnSeekBarChangeListener volumeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            tsVolume.setVolume(progress);
        }
    };

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
        // TODO Auto-generated method stub

    }

    private void sendCmdToService(int request) {
        Message msg = Message.obtain(null, request);
        try {
            messengerService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    private void sendCmdToService(int request, int arg1) {
        Message msg = Message.obtain(null, request, arg1, 0);
        try {
            messengerService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    private IMusicPlayer listener;

    public interface IMusicPlayer {
        void onMusicFolderClick(int contentid, String selectedFolder, String selectedFile);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof IMusicPlayer) {
            listener = (IMusicPlayer) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        
    }

}
