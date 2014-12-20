package ts.hn.tstest.fragment;

import ts.hn.tstest.FilterLog;
import ts.hn.tstest.MsConst;
import ts.hn.tstest.R;
import ts.hn.tstest.base.TsBaseFragment;
import android.os.Bundle;
import android.view.View;

public class ListFolder extends TsBaseFragment {
    
    private static final String TAG = "ListFolder";

    FilterLog log = new FilterLog(TAG);

    /** media type id */
    private int intentId;
    /** previously selected folder index */
    private String selectedFolder;
    /** previously selected file index */
    private String selectedFile;

    public static ListFolder createInstance(int intentid, String selectedFolder, String selectedFile) {
        ListFolder f = new ListFolder();
        Bundle bundle = new Bundle();
        bundle.putInt(MsConst.KEY_INTENT_ID, intentid);
        bundle.putString(MsConst.KEY_SELECTED_FOLDER, selectedFolder);
        bundle.putString(MsConst.KEY_SELECTED_FILE, selectedFile);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentId = getArguments().getInt(MsConst.KEY_INTENT_ID);
        selectedFolder = getArguments().getString(MsConst.KEY_SELECTED_FOLDER);
        selectedFile = getArguments().getString(MsConst.KEY_SELECTED_FILE);
        log.d("log>>>" + "onCreate:" + selectedFolder + ";selectedFile:" + selectedFile);
        
    }

    @Override
    public int getViewId() {
        return R.layout.list_folder_fragment;
    }

    @Override
    public void initData() {
        // TODO Auto-generated method stub

    }

    @Override
    public void initLayout(View rootView) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initListener() {
        // TODO Auto-generated method stub

    }

}
