package prajnan.hci.studygroupfinder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import session.SessionManager;

import com.firebase.client.Firebase;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
 
public class CreateGroupFragment extends Fragment implements OnClickListener {
	
	
	private SimpleDateFormat dateFormatter;
	EditText groupNameText, placeEditText, dateEditText, timeEditText;
	Button createButton;
	Calendar mcurrentDate;
	Calendar mcurrentTime;
	String groupName, place, date, time, uid, groupId, createdBy;
	Firebase sgfFirebase;
	SessionManager session;
	Map<String,String> userDetails;
     
    public CreateGroupFragment(){}
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_create_group, container, false);
        
        // Setting up UI Elements
        dateEditText = (EditText) rootView.findViewById(R.id.dateEditText);
        groupNameText = (EditText) rootView.findViewById(R.id.groupNameText);
        placeEditText = (EditText) rootView.findViewById(R.id.placeEditText);
        timeEditText = (EditText) rootView.findViewById(R.id.timeEditText);
        createButton = (Button) rootView.findViewById(R.id.createButton);
        
        //Setting up Firebase
        Firebase.setAndroidContext(getActivity());
        
        //Setting up Sesion manager
        session = new SessionManager(getActivity());
        userDetails = new HashMap<String,String>();
        userDetails = session.getUserDetails();
        createdBy = userDetails.get("email");
        
  
        // Date EditText onClick for selecting date
        dateEditText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                mcurrentDate=Calendar.getInstance();
                int mYear=mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(getActivity(), new OnDateSetListener() {                  
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub                      
                    	dateEditText.setText(selectedmonth+"/"+selectedday+"/"+selectedyear);
                    }
                },mYear, mMonth, mDay);
                mDatePicker.getDatePicker().setCalendarViewShown(false);
                mDatePicker.setTitle("Select date");                
                mDatePicker.show();  }
        });
         
        
        timeEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mcurrentTime = Calendar.getInstance();
		        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
		        int minute = mcurrentTime.get(Calendar.MINUTE);
		        TimePickerDialog mTimePicker;
		        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
		            @Override
		            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
		                timeEditText.setText( selectedHour + ":" + selectedMinute);
		            }
		        }, hour, minute, true);//Yes 24 hour time
		        mTimePicker.setTitle("Select Time");
		        mTimePicker.show();
				
			}
		});
        
        //Initialize Firebase instance
        sgfFirebase = new Firebase("https://study-group-finder.firebaseio.com");
        
        // Create group button onClick listener
        createButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Parse user input
				groupName = groupNameText.getText().toString();
				place = placeEditText.getText().toString();
				date = dateEditText.getText().toString();
				time = timeEditText.getText().toString();
				
				if(groupName.matches("") || place.matches("") || date.matches("") || time.matches(""))
				{
					Toast.makeText(getActivity(), "Please enter all the details of the group", Toast.LENGTH_LONG).show();
				}
				else
				{
					// Create the group on firebase
					Firebase groupsRef = sgfFirebase.child("groups");
					Firebase newGroupsRef = groupsRef.push();
					
					//Add the group data to the new group ref
					Map<String, String> newGroup = new HashMap<String, String>();
					newGroup.put("name", groupName);
					newGroup.put("place", place);
					newGroup.put("date",date);
					newGroup.put("time", time);
					newGroup.put("createdby",createdBy);
					newGroupsRef.setValue(newGroup);
					
					//Retreieving the group ID
					groupId = newGroupsRef.getKey();
					//Toast.makeText(getActivity(), groupId, Toast.LENGTH_LONG).show();
					//Group created, navigate to user to page to add members to group
					Intent addMembers = new Intent(getActivity(),CreateGroup2.class);
					addMembers.putExtra("groupId", groupId);
					startActivity(addMembers);
					
				}
				
			}
		});

        
        
        return rootView;
    }
    

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}