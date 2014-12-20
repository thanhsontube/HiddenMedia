package ts.hn.tstest.adapter;

import java.util.List;

import ts.hn.tstest.dto.DataDbDto;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListFolderAdapter extends ArrayAdapter<DataDbDto>{
    private List<DataDbDto> list;
    private Context context;

    public ListFolderAdapter(Context context, List<DataDbDto> list) {
        super(context,0, list);
        this.list = list;
        this.context = context;
        
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Holder holder = null;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
        }
        return v;
    }
    static class Holder {
        public TextView txtName;
        public TextView txtPath;
    }
}
