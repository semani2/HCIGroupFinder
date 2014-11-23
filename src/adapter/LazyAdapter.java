package adapter;

import java.util.ArrayList;
import java.util.HashMap;

import prajnan.hci.studygroupfinder.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
	
	private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
     
 
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
    }
 
    public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row_notification, null);
 
        TextView message = (TextView)vi.findViewById(R.id.message); // title
        TextView sender = (TextView)vi.findViewById(R.id.sender); // artist name
        TextView notificationId = (TextView) vi.findViewById(R.id.notificationId);
        TextView status = (TextView) vi.findViewById(R.id.status);
 
        HashMap<String, String> notification = new HashMap<String, String>();
        notification = data.get(position);
 
        // Setting all values in listview
        message.setText(notification.get("message"));
        sender.setText(notification.get("sender"));
        notificationId.setText(notification.get("notificationId"));
        status.setText(notification.get("status"));
        String notificationStatus = notification.get("status");
        if(notificationStatus.equals("read"))
        {
        	message.setTextAppearance(activity, Typeface.NORMAL);
        }
       
        return vi;
    }
}


