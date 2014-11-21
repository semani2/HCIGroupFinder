package prajnan.hci.studygroupfinder;

import java.util.HashMap;
import java.util.Map;

import com.firebase.client.Firebase;

import android.content.Context;

public class NotificationManager {

	// Context
    Context _context;
    
    //Notification Type
    public static String typeGroup = "GROUP";
    public static String typeUser = "USER";
    
    
    
    public NotificationManager(Context context)
    {
    	_context = context;
    	Firebase.setAndroidContext(_context);
    	
    }
    
    public void createNotification(String sender, String receiver, String type, String message)
    {
    	Map<String, String> notification = new HashMap<String, String>();
    	notification.put("sender", sender);
    	notification.put("receiver", receiver);
    	notification.put("type",type);
    	notification.put("message", message);
    	
    	
    	
    	
    }
}
