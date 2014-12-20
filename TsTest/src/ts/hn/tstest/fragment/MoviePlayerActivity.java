package ts.hn.tstest.fragment;

import ts.hn.tstest.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;

public class MoviePlayerActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.movie_player_activity);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MoviePlayer fMovie = MoviePlayer.getInstance(1);
        ft.add(R.id.ll_main, fMovie);
        ft.commit();
        // startActivity(new Intent(this, EncryptActivity.class));
    }

   
}
