package prajnan.hci.studygroupfinder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.ResultHandler;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import session.SessionManager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ProfileFragment extends Fragment implements OnClickListener {
	
	ImageView profilePicture;
	Button pictureButton, changePasswordButton, updateCoursesButton;
	SessionManager session;
	Map<String, String> userDetails = new HashMap<String,String>();
	String uid, email, oldPassword, newPassword;
	
	Firebase sgfFirebase, coursesFirebase;
	EditText oldPasswordText, newPasswordText, coursesEditText;
	private static final int IMAGE_PICKER_SELECT = 999;
	List<String> courses = new ArrayList<String>();
	String profilePicString = "";
	
	public ProfileFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		 View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		 
		 Firebase.setAndroidContext(getActivity());
		 session = new SessionManager(getActivity());
		 userDetails = session.getUserDetails();
		 uid = userDetails.get("uid");
		 
		 pictureButton = (Button) rootView.findViewById(R.id.pictureButton);
		 changePasswordButton = (Button) rootView.findViewById(R.id.changePasswordButton);
		 profilePicture = (ImageView) rootView.findViewById(R.id.imageView1);
		 oldPasswordText = (EditText) rootView.findViewById(R.id.oldPasswordText);
		 newPasswordText = (EditText) rootView.findViewById(R.id.newPasswordText);
		 coursesEditText = (EditText) rootView.findViewById(R.id.coursesEditText);
		 updateCoursesButton = (Button) rootView.findViewById(R.id.updateCoursesButton);
		 
		 sgfFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid);
		 
		 final Firebase profilePicRef = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid+"/profilepic/");
		 profilePicRef.addListenerForSingleValueEvent(new ValueEventListener() {
			    @Override
			    public void onDataChange(DataSnapshot snapshot) {
			    	Log.d("PhotoRef",profilePicRef.toString());
			        profilePicString = (String)snapshot.getValue();
			        if(profilePicString != null)
					 {
						 Log.d("Profile Pic",profilePicString);
						 profilePicture.setImageBitmap(decodeBase64(profilePicString));
					 }
			        //Log.d("ProfilePicString",profilePicString);
			    }
			    @Override
			    public void onCancelled(FirebaseError firebaseError) {
			    }
			});
		 
		// Courses list
		coursesFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid+"/Courses");
		coursesFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot snapshot) {
		        courses = (List<String>) snapshot.getValue();
		        String course ="";
		        int i=0;
		        for(i=0;i<courses.size()-1;i++)
		        {
		        	course += courses.get(i) + ",";
		        }
		        course+=courses.get(i);
		        Log.d("Courses",course);
		        coursesEditText.setText(course);
		        }
		    @Override
		    public void onCancelled(FirebaseError firebaseError) {
		    }
		});
		 
		 //Log.d("Profile Pic before match",profilePicString);
		 
		 pictureButton.setOnClickListener(this);
		 
		 changePasswordButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			email = userDetails.get("email");
			oldPassword = oldPasswordText.getText().toString();
			newPassword = newPasswordText.getText().toString();
			
			if(oldPassword.matches("") || newPassword.matches("")){
				Toast.makeText(getActivity(), "Please enter both fields to change password", Toast.LENGTH_LONG).show();
			}
			else
			{
			sgfFirebase.changePassword(email, oldPassword, newPassword, new ResultHandler() {
				
				public void onSuccess() {
					Toast.makeText(getActivity(), "Password has been changed successfully!", Toast.LENGTH_LONG).show();
					oldPasswordText.setText("");
					newPasswordText.setText("");
				}
				
				public void onError(FirebaseError arg0) {
					Toast.makeText(getActivity(), arg0.getMessage(), Toast.LENGTH_LONG).show();
					oldPasswordText.setText("");
					newPasswordText.setText("");
				}
			});
			}
			}
			
		});
		 
		 updateCoursesButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String course = coursesEditText.getText().toString();
				if(course.matches(""))
					Toast.makeText(getActivity(), "Please enter your courses", Toast.LENGTH_LONG);
				else
				{
					String courses[] = course.split(",");
					coursesFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid+"/Courses");
					coursesFirebase.setValue(courses);
					Toast.makeText(getActivity(), "Your courses have been updated", Toast.LENGTH_LONG);
				}
				
			}
		});
		 
		 return rootView;
	}

	@Override
	public void onClick(View v) {
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_PICKER_SELECT);
	}
	
	/**
     * Photo Selection result
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_SELECT  && resultCode == Activity.RESULT_OK) {
            HomeActivity activity = (HomeActivity)getActivity();
            Bitmap bitmap = getBitmapFromCameraData(data, activity);
            profilePicture.setImageBitmap(bitmap);
            
            // Convert bitmap to base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            
            // Upload to firebase
            sgfFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid);
            sgfFirebase.child("profilepic").setValue(encoded);
        }
    }
    
    private void setFullImageFromFilePath(String imagePath) {
        // Get the dimensions of the View
        int targetW = profilePicture.getWidth();
        int targetH = profilePicture.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        profilePicture.setImageBitmap(bitmap);
    }

	private Bitmap getBitmapFromCameraData(Intent data, Context context) {
		Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
	}
	
	public static Bitmap decodeBase64(String input) 
	{
	    byte[] decodedByte = Base64.decode(input, 0);
	    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
	}
	
}
