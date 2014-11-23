package adapter;

import java.util.List;


import prajnan.hci.studygroupfinder.R;

import model.Group;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Group> groupItems;
   // ImageLoader imageLoader = AppController.getInstance().getImageLoader();
 
    public CustomListAdapter(Activity activity, List<Group> groupItems) {
        this.activity = activity;
        this.groupItems = groupItems;
    }
 
    @Override
    public int getCount() {
        return groupItems.size();
    }
 
    @Override
    public Object getItem(int location) {
        return groupItems.get(location);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);
 
        
        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.groupPic);
        TextView groupName = (TextView) convertView.findViewById(R.id.groupName);
        TextView place = (TextView) convertView.findViewById(R.id.place);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView course = (TextView) convertView.findViewById(R.id.course);
        TextView groupId = (TextView) convertView.findViewById(R.id.groupIdText);
 
        // getting movie data for the row
        Group m = groupItems.get(position);
 
        // thumbnail image
        Log.d("InAdapter",m.getGroupId()+m.getTitle()+m.getDate()+m.getPlace()+m.getCourse()+m.getGroupPic());
        thumbNail.setImageBitmap(decodeBase64(m.getGroupPic()));
         
        // title
        groupName.setText(m.getTitle());
         
        // rating
        place.setText("Place: " + String.valueOf(m.getPlace()));
         
       
        //Date
        date.setText(m.getDate());
         
        // release year
        course.setText(String.valueOf(m.getCourse()));
        
        //GroupId
        groupId.setText(m.getGroupId());
 
        return convertView;
    }
    
    public static Bitmap decodeBase64(String input) 
	{
	    byte[] decodedByte = Base64.decode(input, 0);
	    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
	}
 
}
