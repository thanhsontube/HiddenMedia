package ts.hn.tstest.db;

import ts.hn.tstest.Constants;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TsDataHelper extends SQLiteOpenHelper {

    /** Query to Creates table with appropriate column headings*/    
    private static final String CREATE_TABLE = "create table " 
                                  + Constants.TABLE_NAME + " ( " 
                                  + Constants.FOLDER_ID 
                                  + " integer primary key autoincrement not null, " 
                                  + Constants.FOLDER_PATH + " text,  "
                                  + Constants.PATH + " text,  "
                                  + Constants.MEDIA_TYPE + " int,  "
                                  + Constants.FILE_NAME + " text  " + ");";

    /** FilesDatabase constructor
     * @param context :Activity context
     */
    public TsDataHelper(final Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public final void onCreate(final SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS path");
        database.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

    }

}
