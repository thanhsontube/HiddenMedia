package ts.hn.tstest.fragment;

import ts.hn.tstest.FilterLog;
import ts.hn.tstest.R;
import ts.hn.tstest.fragment.MusicPlayer.IMusicPlayer;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

public class MusicPlayerActivity extends FragmentActivity implements IMusicPlayer{
    private static final String TAG = "MusicPlayerActivity";
    FilterLog log = new FilterLog(TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.music_player_activity);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MusicPlayer f = MusicPlayer.getInstance(1);
        ft.add(R.id.music_ll_main, f);
        ft.commit();
    }

    @Override
    public void onMusicFolderClick(int contentid, String selectedFolder, String selectedFile) {
        log.d("log>>>" + "onMusicFolderClick selectedFolder:" + selectedFolder + ";selectedFile:" + selectedFile);
        PhotoPlayer f = PhotoPlayer.createInstance(contentid, selectedFolder, selectedFile);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.music_ll_main, f);
        ft.commit();
    }

}
