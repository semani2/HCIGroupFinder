package prajnan.hci.studygroupfinder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.Picture;

import session.SessionManager;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	
	public static String picture ="";
	private SimpleDateFormat dateFormatter;
	EditText groupNameText, placeEditText, dateEditText, timeEditText, courseEditText;
	Button createButton;
	Calendar mcurrentDate;
	Calendar mcurrentTime;
	String groupName, place, date, time, uid, groupId, createdBy, course;
	Firebase sgfFirebase;
	SessionManager session;
	Map<String,String> userDetails;
	Map<String, String> newGroup;
	final Map<String,String> groupPicture = new HashMap<String, String>();
	
     
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
        courseEditText = (EditText) rootView.findViewById(R.id.courseEditText);
        
        //Setting up Firebase
        Firebase.setAndroidContext(getActivity());
        
        //Setting up Sesion manager
        session = new SessionManager(getActivity());
        userDetails = new HashMap<String,String>();
        userDetails = session.getUserDetails();
        createdBy = userDetails.get("email");
        uid = userDetails.get("uid");
        
  
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
        final Firebase groupsRef = sgfFirebase.child("groups");
		final Firebase newGroupsRef = groupsRef.push(); 
        // Create group button onClick listener
        createButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Parse user input
				groupName = groupNameText.getText().toString();
				place = placeEditText.getText().toString();
				date = dateEditText.getText().toString();
				time = timeEditText.getText().toString();
				course = courseEditText.getText().toString();
				
				if(groupName.matches("") || place.matches("") || date.matches("") || time.matches("") || course.matches(""))
				{
					Toast.makeText(getActivity(), "Please enter all the details of the group", Toast.LENGTH_LONG).show();
				}
				else
				{
					// Get Image for group
					
					Firebase userRef = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid+"/profilepic");
					 
					userRef.addListenerForSingleValueEvent(new ValueEventListener() {
						
						   
						@Override
					    public void onDataChange(DataSnapshot snapshot) {
					    	
							//Log.d("Picture",(String)snapshot.getValue());
					        Picture.image = (String)snapshot.getValue();
					        //Log.d("Picture got assigned",Picture.image);
					        
					        if((String)snapshot.getValue() == null)
					        {
					        	Log.d("GroupPic","Null");
					        	String temp = "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";
					        	 Picture.image = temp;
	
					        }
					        newGroup = new HashMap<String, String>();
							Log.d("Pic before hashmap",Picture.image);
							newGroup.put("groupPic", Picture.image);
							newGroup.put("name", groupName);
							newGroup.put("place", place);
							newGroup.put("date",date);
							newGroup.put("time", time);
							newGroup.put("createdby",createdBy);
							newGroup.put("course", course);
					        newGroupsRef.setValue(newGroup);
							Log.d("Keys",newGroup.entrySet().toString());
							groupId = newGroupsRef.getKey();
					        Intent addMembers = new Intent(getActivity(),CreateGroup2.class);
							addMembers.putExtra("groupId", groupId);
							startActivity(addMembers);

					    }
					    @Override
					    public void onCancelled(FirebaseError firebaseError) {
					        //System.out.println("The read failed: " + firebaseError.getMessage());
					    }
					});
					
					
					
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