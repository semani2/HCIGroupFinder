package prajnan.hci.studygroupfinder;

import java.util.ArrayList;
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

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateGroup2 extends Activity {
	
	EditText membersEditText, messageEditText;
	Button inviteButton;
	String message, groupId, members, uid;
	Firebase sgfFirebase, userRefFirebase;
	//Map<Integer,String> groups = new HashMap<Integer,String>();
	List<String> groups = new ArrayList<String>();
	SessionManager session;
	Map<String, String> userDetails = new HashMap<String,String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group2);
		
		//Setting up UI elements
		membersEditText = (EditText) findViewById(R.id.membersEditText);
		messageEditText = (EditText) findViewById(R.id.messageEditText);
		inviteButton = (Button) findViewById(R.id.inviteButton);
		
		
		
		//Setting up Firebase
		Firebase.setAndroidContext(this);
		
		// group id from intent
		Intent intent = getIntent();
		groupId = intent.getStringExtra("groupId");
		
		//Retreving uid
		session = new SessionManager(this);
		
		userDetails = session.getUserDetails();
		uid = userDetails.get("uid");
		
		//Initializing firebase instance of the group
		sgfFirebase = new Firebase("https://study-group-finder.firebaseio.com/groups/"+groupId+"/");
		
		// Invite button onclick listener
		inviteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Take user data
				members = membersEditText.getText().toString();
				message = messageEditText.getText().toString();
				
				//Parse input data
				if(members.matches("")){
					Toast.makeText(getApplicationContext(), "Please add members to your group", Toast.LENGTH_LONG).show();
					
				}
				else{
					if(message.matches(""))
					{
						message = "Welcome to the study group!";
					}
					new CreateGroup().execute();
				}
			}
		});
		
	}
	
	private class CreateGroup extends AsyncTask<Void,Void,String>
	{

		@Override
		protected String doInBackground(Void... params) {
			
			// Adding members to group
			
			HashMap<Integer, String> groupMembership = new HashMap<Integer, String>();
			
			//Split members entered
			final String[] memberEmail = members.split(",");
			int i=0;
			for(i=0;i<memberEmail.length;i++)
			{
				groupMembership.put(i+1, memberEmail[i]);
			}
			groupMembership.put(i+1, userDetails.get("email"));
			sgfFirebase.child("members").setValue(groupMembership);
			//Adding message to group
			sgfFirebase.child("message").setValue(message);
			
			// Next add the group into each members list of groups
			
			//First lets add it into the user who created it.
			userRefFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid+"/");
			Log.d("Ref check",userRefFirebase.toString());
			userRefFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
			    @Override
			    public void onDataChange(DataSnapshot snapshot) {
			        //System.out.println(snapshot.getValue());
			    	groups = (List<String>)snapshot.child("Groups").getValue();
			    	if(groups == null)
			    	{
			    		
			    		groups = new ArrayList<String>();
			    		groups.add(groupId);
			    		Log.d("Groups Null",groups.toString());
			    	}
			    	else{
			    	//int index = groups.size();
			    	groups.add(groupId);
			    	}
			    	userRefFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid+"/");
			    	userRefFirebase.child("Groups").setValue(groups);
					Log.d("Frebase ref",userRefFirebase.toString());
			    }
			    @Override
			    public void onCancelled(FirebaseError firebaseError) {
			        System.out.println("The read failed: " + firebaseError.getMessage());
			    }
			});
			//userRefFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid+"/");
			
			
			//Add group info to all the added members
			userRefFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/");
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
			    		Log.d("UsersHashMap",pairs.getKey()+"="+pairs.getValue());
			    		Map<String, String> currentUser = (HashMap<String, String>)pairs.getValue();
			    		Log.d("CurrentUSerKeys",currentUser.keySet().toString());
			    		for(int j=0;j<memberEmail.length;j++)
			    		{
			    			// Check if the current user is present in the member emails list
			    			Log.d("Variable check",currentUser.get("Email")+" ,"+memberEmail[j]);
			    			if(currentUser.get("Email").equals(memberEmail[j]))
			    			{
			    				if(!memberEmail[j].equals(userDetails.get("email")))
			    				{
			    					// Not the owner of the group
			    					
			    					uids.add(pairs.getKey().toString());
			    					Log.d("Added",pairs.getKey().toString() +" to "+uids.toString());
			    				}
			    			}
			    		}
			    		it.remove();
			    	}
			    	
			    	// UIDS of members
			    	Log.d("UIDS List",uids.toString());
			    	
			    	// next to each of the UID in the UIDS list add the group ID to their Groups attribute
			    	for(int uidCount=0;uidCount<uids.size();uidCount++)
			    	{
			    		final String localUid = uids.get(uidCount).toString();
			    		userRefFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/"+localUid+"/");
						Log.d("Ref check",userRefFirebase.toString());
						userRefFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
						    @Override
						    public void onDataChange(DataSnapshot snapshot) {
						        //System.out.println(snapshot.getValue());
						    	groups = (List<String>)snapshot.child("Groups").getValue();
						    	if(groups == null)
						    	{
						    		
						    		groups = new ArrayList<String>();
						    		groups.add(groupId);
						    		Log.d("Groups Null",groups.toString());
						    	}
						    	else{
						    	//int index = groups.size();
						    	groups.add(groupId);
						    	}
						    	userRefFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/"+localUid+"/");
						    	userRefFirebase.child("Groups").setValue(groups);
								Log.d("Frebase ref",userRefFirebase.toString());
						    }
						    @Override
						    public void onCancelled(FirebaseError firebaseError) {
						        System.out.println("The read failed: " + firebaseError.getMessage());
						    }
						});
			    	}
			    	
			    	
			    }
			    @Override
			    public void onCancelled(FirebaseError firebaseError) {
			        System.out.println("The read failed: " + firebaseError.getMessage());
			    }
			});
			
			
			//Toast.makeText(getApplicationContext(), groupId+"  "+sgfFirebase.toString(), Toast.LENGTH_LONG).show();
		
			// Navigate to new activity
			return null;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_group2, menu);
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
