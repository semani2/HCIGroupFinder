package prajnan.hci.studygroupfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import session.SessionManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class GroupActivity extends Activity {
	
	SessionManager session;
	Firebase groupFirebase,userFirebase;
	Map<String, Object> groupDetails = new HashMap<String,Object>();
	Map<String, String> userDetails = new HashMap<String,String>();
	String uid, groupId, emailId, createdBy, members;
	Boolean isOwner;
	TextView groupNameText, placeText, dateText, courseText, timeText, membersText;
	Button addOrJoinButton, leaveOrDeleteButton;
	List<String> membersList= new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
		Firebase.setAndroidContext(this);
		
		
		
		// Initalize session
		session = new SessionManager(this);
		
		// Get Group Id from Intent
		Intent intent = getIntent();
		groupId = intent.getStringExtra("groupId");
		
		//Setting up UI ELements
		groupNameText = (TextView) findViewById(R.id.groupNameText);
		placeText = (TextView) findViewById(R.id.placeText);
		dateText = (TextView) findViewById(R.id.dateText);
		timeText = (TextView) findViewById(R.id.timeText);
		membersText = (TextView) findViewById(R.id.membersText);
		courseText = (TextView) findViewById(R.id.courseText);
		addOrJoinButton = (Button) findViewById(R.id.addOrJoinButton);
		leaveOrDeleteButton = (Button) findViewById(R.id.leaveOrDeleteButton);
		
		//Check if current user is owner of group
		//Lets get the emailid of the current user
		userDetails = session.getUserDetails();
		emailId = userDetails.get("email");
		
		groupFirebase = new Firebase("https://study-group-finder.firebaseio.com/groups/"+groupId);
		
		groupFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		        groupDetails = (HashMap<String, Object>)snapshot.getValue();
		        Log.d("GroupDetails",groupDetails.keySet().toString());
		        
		        Iterator it = groupDetails.entrySet().iterator();
		        while(it.hasNext()){
		    		Map.Entry pairs = (Map.Entry) it.next();
		    		Log.d("GroupHashMap",pairs.getKey()+"="+pairs.getValue());
		        }
		        groupNameText.setText((String)groupDetails.get("name"));
		        courseText.setText((String)groupDetails.get("course"));
		        timeText.setText((String)groupDetails.get("time"));
		        dateText.setText((String)groupDetails.get("date"));
		        placeText.setText((String) groupDetails.get("place"));
		        
		        membersList = (List<String>)groupDetails.get("members");
		        for (String s : membersList)
		        {
		        	
		        	if(s==null)
		        		continue;
		        	else
		            members += s + " ,";
		        }
		        membersText.setText(members);
		        
		    }
		    @Override
		    public void onCancelled(FirebaseError firebaseError) {
		    }
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
