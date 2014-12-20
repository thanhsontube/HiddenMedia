package ts.hn.tstest.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ts.hn.tstest.Constants;
import ts.hn.tstest.FilterLog;
import ts.hn.tstest.R;
import ts.hn.tstest.TsContentProviders;
import ts.hn.tstest.adapter.VideoListFileAdapter;
import ts.hn.tstest.db.TsSqlite;
import ts.hn.tstest.dto.DataDbDto;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MoviePlayer extends Fragment implements SurfaceTextureListener, OnClickListener {
    private static final String TAG = "MoviePlayer";
    FilterLog log = new FilterLog(TAG);
    // the view displays video.
    private TextureView textureView;
    private Surface surface;
    private MediaPlayer player;

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
    private List<DataDbDto> listVideos = new ArrayList<DataDbDto>();
    private List<DataDbDto> listFolder = new ArrayList<DataDbDto>();
    private List<DataDbDto> listFile = new ArrayList<DataDbDto>();
    private int currentFile = 0;
    private AudioManager audioManager;
    public static final int VOLUME_FIRST_SETUP = 30; // %

    // luu width, height cua textureview luc khoi tao.
    private int width, height;

    // flag luu trang thai folder or control
    private boolean isShowList = false;

    private VideoListFileAdapter adapterFolder;
    private ListView listviewFolder;
    private View viewOfFolder;

    private VideoListFileAdapter adapterFile;
    private ListView listviewFile;
    private View viewOfFile;
    
    private String selectedFolder;
    private String selectedFile;

    private TsSqlite sqlite;

    public static MoviePlayer getInstance(int arg) {
        MoviePlayer f = new MoviePlayer();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        IntentFilter filter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
        getActivity().registerReceiver(eventReceiver, filter);
        sqlite = TsSqlite.getInstance();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(eventReceiver);
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    BroadcastReceiver eventReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            log.d("log>>>" + "eventReceiver");
            seekbarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_fragment, container, false);
        initData();
        initLayout(rootView);
        initListener();
        txtAmount.setText(listVideos.size() + "Videos");
        updateLayout();
        return rootView;
    }

    private void initData() {
        sqlite.install(TsContentProviders.getVideo(getActivity()), Constants.VIDEO);
        listVideos.clear();
        listVideos.addAll(sqlite.getListData(Constants.VIDEO));

        listFolder.clear();
        listFolder.addAll(sqlite.getListFolder(Constants.VIDEO));
        
        listFile.clear();
        log.d("log>>>" + "listVideos:" + listVideos.size() + ";listFolder:" + listFolder.size());
    }

    private void initLayout(View rootView) {
        textureView = (TextureView) rootView.findViewById(R.id.movie_textureview);
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
        seekbarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        seekbar.setProgress(currentVolume);

        viewSeekbar = rootView.findViewById(R.id.video_seekbar_ll);
        viewControl = rootView.findViewById(R.id.video_control_ll);
        viewList = rootView.findViewById(R.id.video_ll_listfile);
        viewControlChild = rootView.findViewById(R.id.video_ll_control_child);

        listviewFolder = (ListView) rootView.findViewById(R.id.ts_listview_folder);
        if (!listFolder.isEmpty()) {
            selectedFolder = listFolder.get(0).folder;
        }
        
        if (!listVideos.isEmpty()) {
            selectedFile = listVideos.get(0).name;
        }
        adapterFolder = new VideoListFileAdapter(getActivity(), listFolder, Constants.FOLDER);
        listviewFolder.setAdapter(adapterFolder);

        listviewFile = (ListView) rootView.findViewById(R.id.ts_listview_file);
        adapterFile = new VideoListFileAdapter(getActivity(), listFile, Constants.FILE);
        listviewFile.setAdapter(adapterFile);

        btnSwapToControl = (ImageView) rootView.findViewById(R.id.ts_btn_swap_to_control);
        txtAmount = (TextView) rootView.findViewById(R.id.ts_txt_amount_song);
        txtPositionSong = (TextView) rootView.findViewById(R.id.ts_txt_position_song);

        swapFolder = rootView.findViewById(R.id.ts_btn_folder);
        swapFile = rootView.findViewById(R.id.ts_btn_file);

        viewOfFolder = rootView.findViewById(R.id.ts_ll_listview_folder);
        viewOfFile = rootView.findViewById(R.id.ts_ll_listview_file);
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
        listviewFolder.setOnItemClickListener(onListviewFolderItemCLick);
        listviewFile.setOnItemClickListener(onListviewFileItemClick);
        btnSwapToControl.setOnClickListener(this);

        swapFolder.setOnClickListener(this);
        swapFile.setOnClickListener(this);

    }

    private void initPlayer() {

        if (listVideos == null || listVideos.isEmpty()) {
            Log.e("", ">>> listVideos have nothing");
            return;
        }
        player = MediaPlayer.create(getActivity(), Uri.parse(listVideos.get(currentFile).path));
        player.setSurface(surface);
        player.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextVideo();
                playVideo();
            }
        });

        player.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                playNextVideo();
                playVideo();
                return false;
            }
        });

    }

    /**
     * Runnable to update seek bar and time every second
     */
    private Runnable updateUI = new Runnable() {

        @Override
        public void run() {
            if (player != null && player.isPlaying()) {
                updateTime(player.getCurrentPosition());
                mHandler.postDelayed(updateUI, 1000);
            }

        }
    };

    private void updateTime(int position) {
        int percent = 0;
        if (position > 0 && player != null) {
            percent = getProgressPercentage(position, player.getDuration());
            txtCurrent.setText(milliSecondsToTimer(position));
            txtDuration.setText(milliSecondsToTimer(player.getDuration()));
        } else {
            txtCurrent.setText(defaultText);
            txtDuration.setText(defaultText);
        }
        seekbar.setProgress(percent);
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
            if (player == null || !fromUser) {
                return;
            }
            int currentPosition = progressToTimer(progress, player.getDuration());
            player.seekTo(currentPosition);
            updateTime(player.getCurrentPosition());

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
            if (player == null || !fromUser) {
                return;
            }
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

        }
    };

    // TODO interface SurfaceTExtture

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        log.d("log>>>" + "onSurfaceTextureAvailable");
        surface = new Surface(arg0);
        displayFileName();
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
            player = null;
        }
        resetVideo();
        textureView.setOpaque(false);
        playVideo();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1, int arg2) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {

    }

    /**
     * Function to get Progress percentage
     * 
     * @param currentDuration
     *            : current time interval of song in milliseconds
     * @param totalDuration
     *            : total duration of song in milliseconds
     * @return progress percentage in int
     * */
    private int getProgressPercentage(final long currentDuration, final long totalDuration) {
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        Double percentage = (((double) currentSeconds) / totalSeconds) * 100;
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * 
     * @param progress
     *            : progress percentage in int
     * @param totalDuration
     *            : total duration of song in milliseconds
     * @return current duration in milliseconds
     * */
    private int progressToTimer(final int progress, final int totalDuration) {
        int currentDuration = 0;
        int duration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * duration);
        return currentDuration * 1000;
    }

    /**
     * milliSecondsToTimer(int milliseconds) converts milliseconds to time in string format
     * 
     * @param milliseconds
     * @return time string
     */
    public static String milliSecondsToTimer(final int milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        String strMins = "";
        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        // Prepending 0 to mins if it is one digit
        if (minutes < 10) {
            strMins = "0" + minutes;
        } else {
            strMins = "" + minutes;
        }
        finalTimerString = finalTimerString + strMins + ":" + secondsString;

        return finalTimerString;
    }

    @Override
    public void onClick(View v) {
        if (v == textureView) {
            if (!isFullScreen) {
                showFullScreen();
            } else {
                shownormalscreen();
            }
        }
        if (v == btnFolder) {
            if (!isShowList) {
                viewList.setVisibility(View.VISIBLE);
                viewControlChild.setVisibility(View.GONE);
            } else {
                viewList.setVisibility(View.GONE);
                viewControlChild.setVisibility(View.VISIBLE);
            }

            isShowList = !isShowList;

        }

        if (v == btnPrev) {
            playPrevVideo();
        }

        if (v == btnPlay) {
            if (player != null) {
                if (player.isPlaying()) {
                    pauseVideo();
                } else {
                    playVideo();
                }

            }
        }

        if (v == btnNext) {
            playNextVideo();
        }

        if (v == btnMute) {

            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVolume.setProgress(0);
        }

        if (v == btnMaxVolume) {

        }

        if (v == btnStop) {
            resetVideo();
        }

        if (v == btnSwapToControl) {
            if (!isShowList) {
                viewList.setVisibility(View.VISIBLE);
                viewControlChild.setVisibility(View.GONE);
            } else {
                viewList.setVisibility(View.GONE);
                viewControlChild.setVisibility(View.VISIBLE);
            }

            isShowList = !isShowList;
        }

        if (v == swapFolder) {
            setVisibleFolderList(true);
            listFolder.clear();
            listFolder.addAll(sqlite.getListFolder(Constants.VIDEO));
            updateSelectedListview();
        }

        if (v == swapFile) {
            setVisibleFolderList(false);
            listFile.clear();
            listFile.addAll(sqlite.getListData(Constants.VIDEO));
            updateSelectedListview();
//            adapterFile.notifyDataSetChanged();
        }
    }

    private void setVisibleFolderList(boolean isVisible) {
        if (isVisible) {
            viewOfFolder.setVisibility(View.VISIBLE);
            viewOfFile.setVisibility(View.GONE);
        } else {
            viewOfFolder.setVisibility(View.GONE);
            viewOfFile.setVisibility(View.VISIBLE);
        }
    }

    private void showFullScreen() {
        isFullScreen = true;
        getActivity().getActionBar().hide();
        viewSeekbar.setVisibility(View.GONE);
        viewControl.setVisibility(View.GONE);
        isFullScreen = true;
        textureView.setX(0);
        textureView.setY(0);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textureView.getLayoutParams();
        width = layoutParams.width;
        height = layoutParams.height;
        log.d("log>>>" + "width:" + width + ";height:" + height);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.height = displayMetrics.heightPixels;
        log.d("log>>>" + "displayMetrics.widthPixels:" + displayMetrics.widthPixels + ";displayMetrics.heightPixels:" + displayMetrics.heightPixels);
        textureView.setLayoutParams(layoutParams);
        textureView.bringToFront();
    }

    private void shownormalscreen() {
        isFullScreen = false;
        getActivity().getActionBar().show();
        viewSeekbar.setVisibility(View.VISIBLE);
        viewControl.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textureView.getLayoutParams();
//        Display display = getActivity().getWindowManager().getDefaultDisplay();
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        display.getMetrics(displayMetrics);
        layoutParams.width = 0;
        layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.weight = 1.0f;
        textureView.setLayoutParams(layoutParams);
        textureView.bringToFront();
    }

    private void playNextVideo() {
        if (listVideos.isEmpty()) {
            return;
        }
        currentFile++;
        if (currentFile == listVideos.size()) {
            currentFile = 0;
        }
        displayFileName();
        resetVideo();

    }

    private void playPrevVideo() {
        if (listVideos.isEmpty()) {
            return;
        }
        currentFile--;
        if (currentFile == -1) {
            currentFile = 0;
        }
        displayFileName();
        resetVideo();
    }

    private void resetVideo() {
        try {

            if (listVideos.size() == 0) {
                return;
            }

            if (player == null) {
                initPlayer();
            } else {
                player.reset();
                player.setDataSource(listVideos.get(currentFile).path);
                player.setSurface(surface);
                player.prepare();
            }
            mHandler.removeCallbacks(updateUI);
            player.start();
            player.seekTo(0);
            Thread.sleep(100);
            player.pause();
        } catch (Exception e) {
            log.e("log>>>" + "resetVideo:" + e.toString());
        }
        
        
        updateTime(0);
        updateLayout();
        selectedFolder = sqlite.getFolderName(listVideos.get(currentFile).name, Constants.VIDEO);
        updateSelectedListview();
//        setSelected(currentFile, listFile);
//        adapterFolder.notifyDataSetChanged();
//        listviewFolder.smoothScrollToPosition(currentFile);

    }

    private void playVideo() {
        if (player == null || listVideos.isEmpty()) {
            return;
        }

        player.start();
        mHandler.post(updateUI);
    }

    /** To pause the current video file */
    private void pauseVideo() {
        try {
            if (player != null && player.isPlaying()) {
                player.pause();
            }
        } catch (IllegalStateException e) {
            log.e("log>>>" + "pauseVideo:" + e.toString());
        }
    }

    private void displayFileName() {
        if (listVideos.isEmpty()) {
            return;
        }
        txtName.setText(listVideos.get(currentFile).name);
        File file = new File(listVideos.get(currentFile).path);
        long length = file.length();
        length = length / (1024 * 1024);
        String size = "Size " + length + "MB";
        txtSize.setText(size);
    }

    private OnItemClickListener onListviewFileItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            currentFile = arg2;
            listVideos.clear();
            listVideos.addAll(listFile);
            
            selectedFile = listVideos.get(arg2).name;
            resetVideo();
            playVideo();

        }

    };

    private OnItemClickListener onListviewFolderItemCLick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//            currentFolder = arg2;
//            setSelected(currentFolder, listFolder);
            setVisibleFolderList(false);
            selectedFolder = listFolder.get(arg2).folder;
            
            listFile.clear();
            listFile.addAll(sqlite.getListFolder(selectedFolder, Constants.VIDEO));
            
            updateSelectedListview();
//            adapterFile.notifyDataSetChanged();

        }

    };
    
    private void updateSelectedListview() {
        DataDbDto dataDbDto;
        int smoothFolder = 0;
        int smoothFile = 0;
        for (int i = 0; i < listFolder.size(); i++) {
            dataDbDto = listFolder.get(i);
            if (dataDbDto.folder.equalsIgnoreCase(selectedFolder)) {
                dataDbDto.setSelected(true);
                smoothFolder = i;
            } else {
                dataDbDto.setSelected(false);
            }
        }
        adapterFolder.notifyDataSetChanged();
        listviewFolder.smoothScrollToPosition(smoothFolder);
        
        selectedFile = listVideos.get(currentFile).name;
        
        for (int i = 0; i < listFile.size(); i++) {
            dataDbDto = listFile.get(i);
            if (dataDbDto.name.equalsIgnoreCase(selectedFile)) {
                dataDbDto.setSelected(true);
                smoothFile = i;
            } else {
                dataDbDto.setSelected(false);
            }
        }
        adapterFile.notifyDataSetChanged();
        listviewFile.smoothScrollToPosition(smoothFile);
        updateLayout();
    }

    private void updateLayout() {
        txtPositionSong.setText(currentFile + 1 + "/" + listVideos.size());
        displayFileName();
    }

}
