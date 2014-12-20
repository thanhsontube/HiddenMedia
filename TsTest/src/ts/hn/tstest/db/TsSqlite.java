package ts.hn.tstest.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ts.hn.tstest.Constants;
import ts.hn.tstest.dto.DataDbDto;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TsSqlite {
    private TsDataHelper helper;
    private SQLiteDatabase database;
    static TsSqlite instance = null;

    public static void createInstance(Context context) {
        instance = new TsSqlite(context);
    }

    public static TsSqlite getInstance() {
        return instance;
    }

    private TsSqlite(Context context) {
        try {
            helper = new TsDataHelper(context);
            database = helper.getWritableDatabase();
        } catch (Exception e) {
            database = helper.getReadableDatabase();
        }
    }

    public boolean install(List<DataDbDto> list, int type) {
        boolean isSuccess = true;
        ContentValues values;
        database.beginTransaction();

        try {
            String selection = Constants.MEDIA_TYPE + "= ?";
            String[] selectionArgs = new String[] { String.valueOf(type) };
            database.delete(Constants.TABLE_NAME, selection, selectionArgs);
            for (DataDbDto dto : list) {
                values = new ContentValues();
                values.put(Constants.FILE_NAME, dto.name);
                values.put(Constants.FOLDER_PATH, dto.folder);
                values.put(Constants.MEDIA_TYPE, type);
                values.put(Constants.PATH, dto.path);
                database.insert(Constants.TABLE_NAME, null, values);

            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        } finally {
            database.endTransaction();
        }
        return isSuccess;
    }

    /**
     * delete specific db
     * 
     * @param type
     * @return
     */
    private void delete(int type) {
        String selection = Constants.MEDIA_TYPE + "= ?";
        String[] selectionArgs = new String[] { String.valueOf(type) };
        database.delete(Constants.TABLE_NAME, selection, selectionArgs);
    }

    public List<DataDbDto> getListData(int type) {
        List<DataDbDto> list = new ArrayList<DataDbDto>();
        String selection = Constants.MEDIA_TYPE + "= ?";
        String[] selectionArgs = new String[] { String.valueOf(type) };
        Cursor cursor = database.query(Constants.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex(Constants.PATH));
                String name = cursor.getString(cursor.getColumnIndex(Constants.FILE_NAME));
                String folder = new File(path).getParent() + File.separator;
                DataDbDto dto = new DataDbDto("", path, type, name);
                dto.folder = folder;
                list.add(dto);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public List<DataDbDto> getListFolder(int type) {
        List<DataDbDto> list = new ArrayList<DataDbDto>();
        try {

            String selection = Constants.MEDIA_TYPE + "= ?";
            String[] selectionArgs = new String[] { String.valueOf(type) };
            Cursor cursor = database.query(true, Constants.TABLE_NAME, new String[] { Constants.FOLDER_PATH },
                    selection, selectionArgs, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String folder = cursor.getString(cursor.getColumnIndex(Constants.FOLDER_PATH));
                    DataDbDto dto = new DataDbDto();
                    dto.folder = folder;
                    dto.type = type;
                    list.add(dto);
                } while (cursor.moveToNext());
            }
            return list;
        } catch (Exception e) {
            Log.e("", ">>>ERROR getListFolder:" + e.toString());
            return list;
        }
    }

    public List<DataDbDto> getListFolder(String folderName, int type) {
        List<DataDbDto> list = new ArrayList<DataDbDto>();
        try {

            String selection = Constants.MEDIA_TYPE + "= ? AND " + Constants.FOLDER_PATH + " =?";
            String[] selectionArgs = new String[] { String.valueOf(type), folderName };
            Cursor cursor = database
                    .query(Constants.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String path = cursor.getString(cursor.getColumnIndex(Constants.PATH));
                    String name = cursor.getString(cursor.getColumnIndex(Constants.FILE_NAME));
                    String folder = new File(path).getParent() + File.separator;
                    DataDbDto dto = new DataDbDto("", path, type, name);
                    dto.folder = folder;
                    list.add(dto);
                } while (cursor.moveToNext());
            }
            return list;
        } catch (Exception e) {
            Log.e("", ">>>ERROR getListFolder:" + e.toString());
            return list;
        }
    }

    public String getFolderName(String name, int type) {
        String folder = null;
        try {

            String selection = Constants.MEDIA_TYPE + "= ? AND " + Constants.FILE_NAME + " =?";
            String[] selectionArgs = new String[] { String.valueOf(type), name };
            Cursor cursor = database
                    .query(Constants.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    folder = cursor.getString(cursor.getColumnIndex(Constants.FOLDER_PATH));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("", ">>>ERROR getListFolder:" + e.toString());
        }
        return folder;
    }

    /**
     * install db from content provider to my database
     */
    public boolean install(Cursor cursor, int type) {
        boolean isSuccess = true;
        ContentValues values;
        database.beginTransaction();

        try {
            delete(type);
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String path = cursor.getString(cursor.getColumnIndex("_data"));
                    String name = cursor.getString(cursor.getColumnIndex("title"));
                    String folder = new File(path).getParent() + File.separator;

                    values = new ContentValues();
                    values.put(Constants.FILE_NAME, name);
                    values.put(Constants.FOLDER_PATH, folder);
                    values.put(Constants.MEDIA_TYPE, type);
                    values.put(Constants.PATH, path);
                    database.insert(Constants.TABLE_NAME, null, values);

                } while (cursor.moveToNext());
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        } finally {
            database.endTransaction();
            cursor.close();
        }
        return isSuccess;
    }

}
