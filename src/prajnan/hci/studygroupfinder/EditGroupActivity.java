package prajnan.hci.studygroupfinder;

import java.util.HashMap;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditGroupActivity extends Activity {
	
	Button doneButton;
	EditText placeEditText, courseEditText, timeEditText, dateEditText;
	TextView groupNameText;
	SessionManager session;
	NotificationManager notification;
	Firebase groupFirebase;
	Map<String,String> userDetails = new HashMap<String, String>();
	String uid, email, groupId, groupName;
	String place, date, time, course;
	HashMap<String,String> groupDetails = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);
		
		//Set up firebase
		Firebase.setAndroidContext(this);
		
		//Session set up
		session = new SessionManager(this);
		userDetails = session.getUserDetails();
		uid = userDetails.get("uid");
		email = userDetails.get("email");
		
		notification = new NotificationManager(this);
		
		//Setting up UI Elements
		groupNameText = (TextView) findViewById(R.id.groupNameText);
		placeEditText = (EditText) findViewById(R.id.placeEditText);
		courseEditText = (EditText) findViewById(R.id.courseEditText);
		dateEditText = (EditText) findViewById(R.id.dateEditText);
		timeEditText = (EditText) findViewById(R.id.timeEditText);
		doneButton = (Button) findViewById(R.id.doneButton);
		
		Intent intent = getIntent();
		groupId = intent.getStringExtra("groupId");
		groupName = intent.getStringExtra("groupName");
		
		//Set up all the edit Texts with values
		groupFirebase = new Firebase("https://study-group-finder.firebaseio.com/groups/"+groupId);
		groupFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		        groupDetails = (HashMap<String, String>)snapshot.getValue();
		        place = groupDetails.get("place");
		        placeEditText.setText(place);
		        course = groupDetails.get("course");
		        courseEditText.setText(course);
		        date = groupDetails.get("date");
		        dateEditText.setText(date);
		        time = groupDetails.get("time");
		        timeEditText.setText(time);
		        groupNameText.setText(groupDetails.get("name"));
		    }
		    @Override
		    public void onCancelled(FirebaseError firebaseError) {
		    }
		});
	
		doneButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(place.matches("") || time.matches("") || course.matches("") || date.matches(""))
				{
					Toast.makeText(getApplicationContext(), "Please enter all details!", Toast.LENGTH_LONG).show();
				}
				else
				{
					String new_place = placeEditText.getText().toString();
					String new_date = dateEditText.getText().toString();
					String new_time = timeEditText.getText().toString();
					String new_course = courseEditText.getText().toString();
					String message = "";
					boolean isChanged = false;
					if(!new_place.equals(place))
					{
						groupDetails.put("place", new_place);
						message+="Meeting place, ";
						isChanged = true;
						
					}
					if(!new_date.equals(date))
					{
						groupDetails.put("date", new_date);
						message+="Date, ";
						isChanged = true;
					}
					if(!new_course.equals(course))
					{
						groupDetails.put("course", new_course);
						message+="Course, ";
						isChanged = true;
					}
					if(!new_time.equals(time))
					{
						groupDetails.put("time", new_time);
						message+="Time, ";
						isChanged = true;
					}
					Log.d("IsChanged",String.valueOf(isChanged));
					if(isChanged == true)
					{
						message+="have been updated for "+groupName;
						notification.createNotification(email, groupId, message);
						Log.d("Group After edit",groupDetails.toString());
						Log.d("Firebase ref",groupFirebase.toString());
						groupFirebase.setValue(groupDetails);
						Intent goToGroup = new Intent(EditGroupActivity.this, GroupActivity.class);
						goToGroup.putExtra("groupId", groupId);
						startActivity(goToGroup);
						finish();
					}
					else
					{
						Intent goToGroup = new Intent(EditGroupActivity.this, GroupActivity.class);
						goToGroup.putExtra("groupId", groupId);
						startActivity(goToGroup);
						finish();
					}
				}
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_group, menu);
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
