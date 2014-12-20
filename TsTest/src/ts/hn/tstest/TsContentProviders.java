package ts.hn.tstest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ts.hn.tstest.db.TsSqlite;
import ts.hn.tstest.dto.DataDbDto;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class TsContentProviders {
    private static final String TAG = "TsContentProviders";
    static FilterLog log = new FilterLog(TAG);

    public static List<DataDbDto> getAudio(Context context) {
        List<DataDbDto> list = new ArrayList<DataDbDto>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            log.d("log>>>" + "size:" + cursor.getCount());
            int i = 0;
            StringBuilder builder = null;
            do {
                // builder = new StringBuilder();
                // for (String string : columns) {
                // builder.append(string);
                // builder.append(":");
                // builder.append(cursor.getString(cursor.getColumnIndex(string)));
                // builder.append(";");
                // }
                // log.d("log>>>" + i++ + " --------------------");
                // log.d("log>>>" + builder.toString());

                String id = cursor.getString(cursor.getColumnIndex("_id"));
                String path = cursor.getString(cursor.getColumnIndex("_data"));
                int type = Constants.AUDIO;
                String name = cursor.getString(cursor.getColumnIndex("title"));
                String folder = new File(path).getParent() + File.separator;
                DataDbDto dto = new DataDbDto(id, path, type, name);
                dto.folder = folder;
                list.add(dto);

            } while (cursor.moveToNext());
        }

        return list;
    }
    
    public static List<DataDbDto> getAudio(Context context, Cursor cursor) {
        List<DataDbDto> list = new ArrayList<DataDbDto>();
        if (cursor != null && cursor.moveToFirst()) {
            log.d("log>>>" + "size:" + cursor.getCount());
            int i = 0;
            StringBuilder builder = null;
            do {
                // builder = new StringBuilder();
                // for (String string : columns) {
                // builder.append(string);
                // builder.append(":");
                // builder.append(cursor.getString(cursor.getColumnIndex(string)));
                // builder.append(";");
                // }
                // log.d("log>>>" + i++ + " --------------------");
                // log.d("log>>>" + builder.toString());

                String id = cursor.getString(cursor.getColumnIndex("_id"));
                String path = cursor.getString(cursor.getColumnIndex("_data"));
                int type = Constants.AUDIO;
                String name = cursor.getString(cursor.getColumnIndex("title"));
                String folder = new File(path).getParent() + File.separator;
                DataDbDto dto = new DataDbDto(id, path, type, name);
                dto.folder = folder;
                list.add(dto);

            } while (cursor.moveToNext());
        }

        return list;
    }

    public static List<DataDbDto> getVideo(Context context) {
        List<DataDbDto> list = new ArrayList<DataDbDto>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            log.d("log>>>" + "size:" + cursor.getCount());
            int i = 0;
            StringBuilder builder = null;
            do {
                // builder = new StringBuilder();
                // for (String string : columns) {
                // builder.append(string);
                // builder.append(":");
                // builder.append(cursor.getString(cursor.getColumnIndex(string)));
                // builder.append(";");
                // }
                // log.d("log>>>" + i++ + " --------------------");
                // log.d("log>>>" + builder.toString());

                String id = cursor.getString(cursor.getColumnIndex("_id"));
                String path = cursor.getString(cursor.getColumnIndex("_data"));
                int type = Constants.VIDEO;
                String name = cursor.getString(cursor.getColumnIndex("title"));
                String folder = new File(path).getParent() + File.separator;
                DataDbDto dto = new DataDbDto(id, path, type, name);
                dto.folder = folder;
                list.add(dto);

            } while (cursor.moveToNext());
        }
        return list;
    }

}
