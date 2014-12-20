package ts.hn.tstest;

import ts.hn.tstest.db.TsSqlite;
import ts.hn.tstest.utils.TsVolume;
import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ResouceManager.createInstace(getApplicationContext());
        TsSqlite.createInstance(getApplicationContext());
        TsVolume.createInstace(getApplicationContext());
    }
}
