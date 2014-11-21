package prajnan.hci.studygroupfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import session.SessionManager;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.content.Context;
import android.util.Log;

public class NotificationManager {

	// Context
    Context _context;
    
    //Notification Type
    public static String typeGroup = "GROUP";
    public static String typeUser = "USER";
    
    //Session
    SessionManager session;
    
    private String UID, EMAIL;
    
    Map<String,String> userDetails = new HashMap<String, String>();
    
    //Firebase
    Firebase ref1, ref2;
    
    
    public NotificationManager(Context context)
    {
    	_context = context;
    	Firebase.setAndroidContext(_context);
    	session = new SessionManager(_context);
    	userDetails = session.getUserDetails();
    	UID = userDetails.get("uid");
    	EMAIL = userDetails.get("email");
    }
    
    public void createNotification(String sender, String receiver, String message)
    {
    	final Map<String, String> notification = new HashMap<String, String>();
    	notification.put("sender", sender);
    	notification.put("receiver", receiver);
    	notification.put("message", message);
    	notification.put("status", "unread");
    	
    	String groupId;
    	groupId = receiver;
    	
    	ref1 = new Firebase("https://study-group-finder.firebaseio.com/groups/"+groupId+"/members");
    	
    	ref1.addListenerForSingleValueEvent(new ValueEventListener() {
    	    @Override
    	    public void onDataChange(DataSnapshot snapshot) {
    	        // Get list of members in group
    	    	
    	    	
    	    	final List<String> membersList = (List<String>) snapshot.getValue();
    	    	//Next push notification to each member fo the group
    			Firebase userRefFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/");
    			Log.d("AddToALLRef",userRefFirebase.toString());
    			userRefFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
    			    @Override
    			    public void onDataChange(DataSnapshot snapshot) {
    			        //System.out.println(snapshot.getValue());
    			    	Map<String, Object> users = new HashMap<String, Object>();
    			    	users = (HashMap<String, Object>)snapshot.getValue();
    			    	Log.d("Users",users.toString());
    			    	
    			    	// Next iterate over the users to get the UID of the users added to the group
    			    	Log.d("Key Set",users.keySet().toString());
    			    	Set keySet = users.keySet();
    			    	int numUsers = users.size();
    			    	Iterator it = users.entrySet().iterator();
    			    	List<String> uids = new ArrayList<String>();
    			    	
    			    	while(it.hasNext()){
    			    		Map.Entry pairs = (Map.Entry) it.next();
    			    		//Log.d("UsersHashMap",pairs.getKey()+"="+pairs.getValue());
    			    		Map<String, String> currentUser = (HashMap<String, String>)pairs.getValue();
    			    		//Log.d("CurrentUSerKeys",currentUser.keySet().toString());
    			    		Log.d("MembersList",membersList.toString());
    			    		for(int j=0;j<membersList.size();j++)
    			    		{
    			    			if(membersList.contains(currentUser.get("Email")))
    			    			{
    			    				Log.d("UserIsMember",currentUser.get("Email"));
    			    				Firebase userRef = new Firebase("https://study-group-finder.firebaseio.com/users/"+pairs.getKey()+"/notifications");
    			    				Firebase newNotif = userRef.push();
    			    				newNotif.setValue(notification);
    			    				break;
    			    			}
    			    		}
    			    		it.remove();
    			    	}
    	    }
    			    @Override
				    public void onCancelled(FirebaseError firebaseError) {
				        System.out.println("The read failed: " + firebaseError.getMessage());
				    }
				});
    	    }	
    	    @Override
    	    public void onCancelled(FirebaseError firebaseError) {
    	    }
    	});
    	
    	
    	
    	
    }
}
    
