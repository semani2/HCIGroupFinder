package prajnan.hci.studygroupfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import session.SessionManager;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import adapter.LazyAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class NotificationFragment extends Fragment {
	
	 LazyAdapter adapter;
	 Firebase notificationsFirebase;
	 String uid, email;
	 SessionManager session;
	 Map<String, String> userDetails = new HashMap<String,String>();
	 ListView list;
	
	public NotificationFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		 View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
		 
		 list = (ListView) rootView.findViewById(R.id.list);
		 
		 //Set Firebase context
		 Firebase.setAndroidContext(getActivity());
		 
		 //Set up session manager
		 session = new SessionManager(getActivity());
		 
		 //Get user details from session
		 userDetails = session.getUserDetails();
		 uid = userDetails.get("uid");
		 email = userDetails.get("email");
		 
		 final ArrayList<HashMap<String, String>> notifications = new ArrayList<HashMap<String, String>>();
		 
		 notificationsFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid+"/notifications");
		 
		 //Adding a listener to the notifications firebase to keep listening for any notificatons
		 notificationsFirebase.orderByKey().addValueEventListener(new ValueEventListener() {
			    @Override
			    public void onDataChange(DataSnapshot snapshot) {
			        //System.out.println(snapshot.getValue());
			    	//Retreive data from the firebase and push it onto our adapter
			    	Map<String, String> notificationsMap = new HashMap<String,String>();
			    	
			    	notificationsMap = (HashMap<String,String>)snapshot.getValue();
			    	if(notificationsMap !=null){
			    	Log.d("Notifications Keys",notificationsMap.keySet().toString());
			    	
			    	// Iterate over each of the notification now
			    	Iterator it = notificationsMap.entrySet().iterator();
			    	while(it.hasNext()){
			    		HashMap<String,String> newNotification = new HashMap<String,String>();
			    		Map.Entry pairs = (Map.Entry) it.next();
			    		Log.d("NotificationsHashMap",pairs.getKey()+"="+pairs.getValue());
			    		Map<String, String> currentNotification = (HashMap<String, String>)pairs.getValue();
			    		Log.d("CurrentNotification",currentNotification.keySet().toString());
			    		newNotification.put("message", currentNotification.get("message"));
			    		newNotification.put("sender", currentNotification.get("sender"));
			    		newNotification.put("notificationId",pairs.getKey().toString());
			    		newNotification.put("status", currentNotification.get("status"));
			    		notifications.add(newNotification);
			    	}
			    	
			    	// Getting adapter by passing xml data ArrayList
			        adapter=new LazyAdapter(getActivity(), notifications);
			        list.setAdapter(adapter);
			    	}
			    	
			    	
			    	// Click event for single list row
			    	
			        list.setOnItemClickListener(new OnItemClickListener() {
			        	@Override
			            public void onItemClick(AdapterView<?> parent, View view,
			                    int position, long id) {
			            		TextView status = (TextView) view.findViewById(R.id.status);
			            		TextView notificationId = (TextView) view.findViewById(R.id.notificationId);
			            		String notificationStatus = status.getText().toString();
			            		Log.d("Status",notificationStatus);
			            		if(notificationStatus.equals("unread"))
			            		{
			            			Firebase notifFirebase = notificationsFirebase.child(notificationId.getText().toString()).child("status");
			            			notifications.clear();
			            			adapter.notifyDataSetChanged();
			            			notifFirebase.setValue("read");
			            		}
			            }
			        });
			    }
			    @Override
			    public void onCancelled(FirebaseError firebaseError) {
			        //System.out.println("The read failed: " + firebaseError.getMessage());
			    }
			});
		 return rootView;
	}

}
