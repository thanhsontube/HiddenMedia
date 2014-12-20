package ts.hn.tstest.movie;

import ts.hn.tstest.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        initData();
        initLayout(rootView);
        initListener();
        return rootView;
    }
    
    private void initData() {}

    private void initLayout(View rootView) {}

    private void initListener() {}
}
