package ts.hn.tstest.adapter;

import java.util.List;

import ts.hn.tstest.Constants;
import ts.hn.tstest.R;
import ts.hn.tstest.dto.DataDbDto;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoListFileAdapter extends ArrayAdapter<DataDbDto> {

    private List<DataDbDto> list;
    private Context context;
    private int type;

    public VideoListFileAdapter(Context context, List<DataDbDto> list) {
        super(context, 0, list);
        this.list = list;
        this.context = context;
    }

    public VideoListFileAdapter(Context context, List<DataDbDto> list, int type) {
        super(context, 0, list);
        this.list = list;
        this.context = context;
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        Holder holder = null;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_video_list_file, parent, false);
            holder = new Holder();
            holder.icon = (ImageView) v.findViewWithTag("icon");
            holder.txtName = (TextView) v.findViewWithTag("name");
            holder.txtFolder = (TextView) v.findViewWithTag("folder");
            holder.viewBg = v.findViewWithTag("view_bg");
            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        DataDbDto dto = list.get(position);
        switch (type) {
        case Constants.FOLDER:

            holder.txtName.setText(dto.folder);
            holder.txtFolder.setText("");
            break;
        case Constants.FILE:
            holder.txtName.setText(dto.name);
            holder.txtFolder.setText(dto.path);
            break;

        default:
            break;
        }
        if (dto.isSelected()) {
            holder.viewBg.setBackgroundColor(Color.BLUE);
        } else {
            holder.viewBg.setBackgroundColor(Color.WHITE);
        }
        return v;
    }

    static class Holder {
        ImageView icon;
        TextView txtName, txtFolder;
        View viewBg;
    }
}
