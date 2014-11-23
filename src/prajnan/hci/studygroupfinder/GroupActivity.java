package prajnan.hci.studygroupfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import session.SessionManager;
import android.app.Activity;
import android.app.ProgressDialog;
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
	NotificationManager notification;
	Firebase groupFirebase,userFirebase;
	Map<String, Object> groupDetails = new HashMap<String,Object>();
	Map<String, String> userDetails = new HashMap<String,String>();
	String uid, groupId, emailId, createdBy, members, groupName;
	Boolean isOwner = false;
	TextView groupNameText, placeText, dateText, courseText, timeText, membersText;
	Button addOrJoinButton, leaveOrDeleteButton;
	List<String> membersList= new ArrayList<String>();
	List<String> groupsList = new ArrayList<String>();
	List<String> userGroups = new ArrayList<String>();
	private ProgressDialog pDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
		Firebase.setAndroidContext(this);
		
		
		
		// Initalize session
		session = new SessionManager(this);
		
		notification = new NotificationManager(this);
		
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
		        groupName = (String)groupDetails.get("name");
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
		        Log.d("Member size",String.valueOf(membersList.size()));
		        members="";
		        for (int k=0;k<membersList.size();k++)
		        {
		        	
		        	String s = membersList.get(k);
		        	//Log.d("Print the string s",s);
		            members += s + " ,";
		        }
		        membersText.setText(members);
		        
		        //Next check for user ownership and set buttons and finctinalities
		        if(isOwner){
		        	//User is owner of group
		        	//Can add members to the group
		        	addOrJoinButton.setText("Add member");
		        	addOrJoinButton.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// Is owner so take him to add member functionality
							Intent addMember = new Intent(GroupActivity.this,AddMember.class);
							addMember.putExtra("groupId", groupId);
							addMember.putExtra("groupName",groupName);
							startActivity(addMember);
							
						}
					});
		        	
		        	// Leave or Delete Button
		        	leaveOrDeleteButton.setText("Delete Group");
		        	leaveOrDeleteButton.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Log.d("KEY_DELETE","IN DELETE GROUP");
							//pDialog = new ProgressDialog(getApplicationContext());
					        // Showing progress dialog before making http request
					        //pDialog.setMessage("Deleting group...");
					        //pDialog.show();
							//Delete group
							//First get list of all members
							//Delete the group id from their list of group memberships
							notification.createNotification(emailId, groupId, "The group "+groupName+" has been deleted.");
							Log.d("DeleteMembers",membersList.toString());
							for (String member : membersList){
								// For each member
								userFirebase = new Firebase("https://study-group-finder.firebaseio.com/users");
								userFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
								    @Override
								    public void onDataChange(DataSnapshot snapshot) {
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
								    		Log.d("UsersHashMap",pairs.getKey()+"="+pairs.getValue());
								    		Map<String, Object> currentUser = (HashMap<String, Object>)pairs.getValue();
								    		Log.d("CurrentUSerKeys",currentUser.keySet().toString());
								    		for(int j=0;j<membersList.size();j++)
								    		{
								    			// Check if the  user is present in the member emails list
								    			if(membersList.contains((String)currentUser.get("Email")))
								    			{
								    				//User is in this groups member list
								    				groupsList = (List<String>)currentUser.get("Groups");
								    				groupsList.remove(groupId);
								    				Firebase userGroupsList = new Firebase("https://study-group-finder.firebaseio.com/users/"+pairs.getKey().toString()+"/Groups");
								    				userGroupsList.setValue(groupsList);
								    			}
								    			
								    		}
								    		it.remove();
								    	}
								    	//Delete the group
								    	Firebase group = new Firebase("https://study-group-finder.firebaseio.com/groups/"+groupId);
								    	group.setValue(null);
								    	
								    	Intent goHome = new Intent(GroupActivity.this, HomeActivity.class);
								    	startActivity(goHome);
								    	finish();
								    	
								    	
								    }
								    @Override
								    public void onCancelled(FirebaseError firebaseError) {
								    }
								});
							}
							
						}
					});
		        	
		        }
		        else
		        {
		        	
		        	//Check if user is already a member or not
		        	Log.d("Members of the group",String.valueOf(membersList.contains(emailId)));
		        	if(membersList.contains(emailId)){
		        		// User is already member so disable the button
		        		addOrJoinButton.setText("Join Group");
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
								    	if(groupsList == null)
								    	{
								    		groupsList = new ArrayList<String>();
								    	}
								    	groupsList.add(groupId);
								    	userGroups.setValue(groupsList);
								    	notification.createNotification(emailId, groupId, emailId+" has joined the group "+groupName);
								    	Intent refresh = new Intent(GroupActivity.this,GroupActivity.class);
								    	refresh.putExtra("groupId", groupId);
								    	startActivity(refresh);
								    	finish();
								    }
								    @Override
								    public void onCancelled(FirebaseError firebaseError) {
								    }
								});
							}
						});
		        	}
		        	
		        	// Leave or Delete Button
		        	if(!membersList.contains(emailId)){
		        		// User is nor a member nor the owner
		        		leaveOrDeleteButton.setText("Leave Group");
		        		leaveOrDeleteButton.setEnabled(false);
					}
		        	else
		        	{
		        		//User is not owner but a member so option to leave the group
		        		leaveOrDeleteButton.setText("Leave Group");
		        		leaveOrDeleteButton.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								final Firebase userGroupList = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid+"/Groups");
								final Firebase groupToDeleteFrom = new Firebase("https://study-group-finder.firebaseio.com/groups/"+groupId+"/members");
								userGroupList.addListenerForSingleValueEvent(new ValueEventListener() {
								    @Override
								    public void onDataChange(DataSnapshot snapshot) {
								        userGroups =  (List<String>)snapshot.getValue();
								        Log.d("User Group Before delete", userGroups.toString());
								        userGroups.remove(groupId);
								        Log.d("User Group Before delete", userGroups.toString());
								        userGroupList.setValue(userGroups);
								        
								        //Next remove the user's email id from the members list of the group
								        membersList.remove(emailId);
								        Log.d("Members list after removing user",membersList.toString());
								        groupToDeleteFrom.setValue(membersList);
								        Log.d("Firebase Ref",groupToDeleteFrom.toString());
								        notification.createNotification(emailId, groupId, emailId+ " has left the group "+groupName);
								        Intent refresh = new Intent(GroupActivity.this, GroupActivity.class);
								        refresh.putExtra("groupId", groupId);
								        startActivity(refresh);
								        finish();
								    }
								    @Override
								    public void onCancelled(FirebaseError firebaseError) {
								    }
								});
							}
						});
		        	}
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
