package ts.hn.tstest.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class TsBaseFragment extends Fragment {

    public abstract int getViewId();

    public abstract void initData();

    public abstract void initLayout(View rootView);

    public abstract void initListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getViewId(), container, false);
        initData();
        initLayout(rootView);
        initListener();
        return rootView;
    }
}
