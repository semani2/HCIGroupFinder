package prajnan.hci.studygroupfinder;

import java.util.HashMap;
import java.util.Map;

import com.firebase.client.Firebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	String message, groupId, members;
	Firebase sgfFirebase;

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
					// Adding members to group
					//Map<String, HashMap<Integer,String>> groupMembers = new HashMap<String, HashMap<Integer, String>>();
					HashMap<Integer, String> groupMembership = new HashMap<Integer, String>();
					
					//Split members entered
					String[] memberEmail = members.split(",");
					for(int i=0;i<memberEmail.length;i++)
					{
						groupMembership.put(i+1, memberEmail[i]);
					}
					
					sgfFirebase.child("members").setValue(groupMembership);
					//Adding message to group
					sgfFirebase.child("message").setValue(message);
					
					Toast.makeText(getApplicationContext(), groupId+"  "+sgfFirebase.toString(), Toast.LENGTH_LONG).show();
				}
			}
		});
		
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
