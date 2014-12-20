package ts.hn.tstest.fragment;

import java.util.List;

import ts.hn.tstest.Constants;
import ts.hn.tstest.FilterLog;
import ts.hn.tstest.MsConst;
import ts.hn.tstest.R;
import ts.hn.tstest.base.TsBaseFragment;
import ts.hn.tstest.db.TsSqlite;
import ts.hn.tstest.dto.DataDbDto;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.provider.ContactsContract.RawContacts.Data;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PhotoPlayer extends TsBaseFragment {

    private static final String TAG = "PhotoPlayer";

    static FilterLog log = new FilterLog(TAG);

    /** media type id */
    private int intentId;
    /** previously selected folder index */
    private String selectedFolder;
    /** previously selected file index */
    private String selectedFile;
    
    private List<DataDbDto> listFolder;
    private List<DataDbDto> listFiles;
    private TsSqlite sqlite;
    
    private ListView lvFolder;

    public static PhotoPlayer createInstance(int intentid, String selectedFolder, String selectedFile) {
        PhotoPlayer f = new PhotoPlayer();
        Bundle bundle = new Bundle();
        bundle.putInt(MsConst.KEY_INTENT_ID, intentid);
        bundle.putString(MsConst.KEY_SELECTED_FOLDER, selectedFolder);
        bundle.putString(MsConst.KEY_SELECTED_FILE, selectedFile);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle bundle = getArguments();
        if(bundle != null) {
            intentId = bundle.getInt(MsConst.KEY_INTENT_ID, 0);
            selectedFolder =bundle.getString(MsConst.KEY_SELECTED_FOLDER);
            selectedFile = bundle.getString(MsConst.KEY_SELECTED_FILE);
        }

    }
    
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.folder_fragment, container, false);
//        log.d("log>>>" + "initLayout selectedFile:" + selectedFile);
//        return rootView;
//    }

    @Override
    public int getViewId() {
        return R.layout.folder_fragment;
    }

    @Override
    public void initData() {
        sqlite = TsSqlite.getInstance();
        listFolder = sqlite.getListFolder(Constants.AUDIO);

    }

    @Override
    public void initLayout(View rootView) {
        // FragmentManager fm = getFragmentManager();
        // FragmentTransaction ft = fm.beginTransaction();
        // ListFolder listFolder = ListFolder.createInstance(Constants.AUDIO, selectedFolder, selectedFile);
        // ft.add(R.id.folder_ll_main, listFolder);
        // ft.commit();

    }

    @Override
    public void initListener() {

    }

}
