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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GroupActivity extends Activity {
	
	SessionManager session;
	Firebase groupFirebase,userFirebase;
	Map<String, Object> groupDetails = new HashMap<String,Object>();
	Map<String, String> userDetails = new HashMap<String,String>();
	String uid, groupId, emailId, createdBy, members;
	Boolean isOwner = false;
	TextView groupNameText, placeText, dateText, courseText, timeText, membersText;
	Button addOrJoinButton, leaveOrDeleteButton;
	List<String> membersList= new ArrayList<String>();
	List<String> groupsList = new ArrayList<String>();

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
		uid = userDetails.get("uid");
		
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
		        courseText.setText("Course: "+(String)groupDetails.get("course"));
		        timeText.setText("Time: "+(String)groupDetails.get("time"));
		        dateText.setText("Date: "+(String)groupDetails.get("date"));
		        placeText.setText("Meeting place: "+(String) groupDetails.get("place"));
		        createdBy = (String)groupDetails.get("createdby");
		        if(createdBy.equals(emailId))
		        {
		        	isOwner = true;
		        }
		        membersList = (List<String>)groupDetails.get("members");
		        for (int k=1;k<membersList.size();k++)
		        {
		        	
		        	String s = membersList.get(k);
		            members += s + " ,";
		        }
		        membersText.setText("Group Members: "+members);
		        
		        //Next check for user ownership and set buttons and finctinalities
		        if(isOwner){
		        	//User is owner of group
		        	//Can add members to the group
		        	addOrJoinButton.setText("Add member");
		        	addOrJoinButton.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// Is owner so take him to add member functionality
							Intent addMember = new Intent(GroupActivity.this,CreateGroup2.class);
							addMember.putExtra("groupId", groupId);
							startActivity(addMember);
							
						}
					});
		        	
		        	// Leave or Delete Button
		        	
		        	
		        }
		        else
		        {
		        	
		        	//Check if user is already a member or not
		        	Log.d("Members of the group",String.valueOf(membersList.contains(emailId)));
		        	if(membersList.contains(emailId)){
		        		// User is already member so disable the button
		        		addOrJoinButton.setEnabled(false);
					}
		        	else
		        	{
		        		// Member is not owner and not a member of the group so show the join button
		        		// TO join the group
		        		addOrJoinButton.setText("Join Group");
		        		final Firebase groupMembers = new Firebase("https://study-group-finder.firebaseio.com/groups/"+groupId+"/members");
		        		final Firebase userGroups = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid+"/Groups");
		        		addOrJoinButton.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// Add user to members group of thr group and also add the group id to the user's group list
								membersList.add(emailId);
								groupMembers.setValue(membersList);
								
								//Add group ID to users groups list
								userGroups.addListenerForSingleValueEvent(new ValueEventListener() {
								    @Override
								    public void onDataChange(DataSnapshot snapshot) {
								        // Get user's groups list 
								    	//Append the group iD and push it back
								    	groupsList = (List<String>)snapshot.getValue();
								    	groupsList.add(groupId);
								    	userGroups.setValue(groupsList);
								    }
								    @Override
								    public void onCancelled(FirebaseError firebaseError) {
								    }
								});
							}
						});
		        	}
		        	
		        	// Leave or Delete Button
		        	
		        }
		        
		    }
		    @Override
		    public void onCancelled(FirebaseError firebaseError) {
		    }
		});
		
		//Check if current user is the owner of the group
	
		
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
