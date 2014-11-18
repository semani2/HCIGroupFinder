package prajnan.hci.studygroupfinder;

import java.util.HashMap;
import java.util.Map;

import session.SessionManager;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegistrationActivity extends Activity {
	
	Button loginButton, registerButton;
	EditText nameEditText, passwordEditText, emailEditText;
	String name, email, password, major;
	Spinner majorSpinner;
	Firebase sgfFirebase;
	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registration);
		
		//Setting up Firebase
		Firebase.setAndroidContext(this);
		
		//Setting up UI Elements
		loginButton = (Button) findViewById(R.id.loginButton);
		registerButton = (Button) findViewById(R.id.registerButton);
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
		majorSpinner = (Spinner) findViewById(R.id.majorSpinner);
		
		//Setting up session manager
		session = new SessionManager(getApplicationContext());

		//Initializing Firebase
		sgfFirebase = new Firebase("https://study-group-finder.firebaseio.com/");
		
		// Login Button click listener
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Navigate user to login page
				Intent goToLogin = new Intent(RegistrationActivity.this,LoginActivity.class);
				startActivity(goToLogin);
				finish();
			}
		});
		
		//Major spinner item selected listener
		majorSpinner.setOnItemSelectedListener(
	              new AdapterView.OnItemSelectedListener() {
	                  @Override
	                  public void onItemSelected(AdapterView<?> arg0, View arg1,
	                          int arg2, long arg3) {
	                    major = String.valueOf(majorSpinner.getSelectedItem());
	                    
	                  }
	                  @Override
	                  public void onNothingSelected(AdapterView<?> arg0) {
	                      major ="";
	                  }
	              }
	          );
		
		//Register Button On Click Listener
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Get user input
				name = nameEditText.getText().toString();
				email = emailEditText.getText().toString();
				password = passwordEditText.getText().toString();
				CreateUser createUser = new CreateUser();
				createUser.execute();
			}
		});
		
		
	}
	
	//Async task to create user on firebase
	private class CreateUser extends AsyncTask<Void,Void,String>
	{

		@Override
		protected String doInBackground(Void... params) {
			
			// Creating user on Firebase
			sgfFirebase.createUser(email, password, new Firebase.ResultHandler() {
			    @Override
			    public void onSuccess() {
			       // Registration successful
			    	Toast.makeText(getApplicationContext(), "You have successfully created your account!", Toast.LENGTH_LONG).show();
			    	
			    	// Log in user on Firebase
			    	sgfFirebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
			    	    @Override
			    	    public void onAuthenticated(AuthData authData) {
			    	       
			    	    	// Login successful
			    	    	Map<String, String> map = new HashMap<String, String>();
			    	        map.put("provider", authData.getProvider());
			    	        if(authData.getProviderData().containsKey("id")) {
			    	            map.put("provider_id", authData.getProviderData().get("id").toString());
			    	        }
			    	        if(authData.getProviderData().containsKey("displayName")) {
			    	            map.put("displayName", authData.getProviderData().get("displayName").toString());
			    	        }
			    	        sgfFirebase.child("users").child(authData.getUid()).setValue(map);
			    	        sgfFirebase.child("users").child(authData.getUid()).child("Name").setValue(name);
			    	        sgfFirebase.child("users").child(authData.getUid()).child("Major").setValue(major);
			    	        sgfFirebase.child("users").child(authData.getUid()).child("Email").setValue(email);
			    	        
			    	        //Initialize session
			    	        session.createLoginSession(email,authData.getUid());
			    	        
			    	        //Take user to Home Page
			    	        Intent goHome = new Intent(RegistrationActivity.this, HomeActivity.class);
			    	        startActivity(goHome);
			    	        finish();
			    	    }
			    	    @Override
			    	    public void onAuthenticationError(FirebaseError firebaseError) {
			    	        
			    	    }
			    	});
			    	
			    	
			    }
			    @Override
			    public void onError(FirebaseError firebaseError) {
			        // there was an error
			    	Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_LONG).show();
			    }
			});
			
			return null;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
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
