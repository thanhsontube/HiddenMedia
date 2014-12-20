package ts.hn.tstest.movie;

import ts.hn.tstest.R;
import android.app.Fragment;
import android.os.Bundle;

public class MainActivity extends AbsFragmentActivity {
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    @Override
    protected Fragment onCreateMainFragment(Bundle savedInstanceState) {
        return new MainFragment();
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.ll_main;
    }

}
