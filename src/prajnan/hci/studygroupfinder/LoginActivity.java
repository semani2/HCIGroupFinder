package prajnan.hci.studygroupfinder;

import session.SessionManager;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity {
	
	Button loginButton, signupButton;
	EditText emailEditText, passwordEditText;
	Firebase sgfFirebase;
	String email, password;
	SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        
        // Setting up Firebase
        Firebase.setAndroidContext(this);
        
        //Setting up Session Manager
        session = new SessionManager(getApplicationContext());
        
        //Setting up UI Elements
        loginButton = (Button) findViewById(R.id.loginButton);
        signupButton = (Button) findViewById(R.id.signupButton);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        
        //Initializing Firebase
        sgfFirebase = new Firebase("https://study-group-finder.firebaseio.com");
        
        //Login button click listener
        loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Parse data input
				email = emailEditText.getText().toString();
				password = passwordEditText.getText().toString();
				
				//Check for null strings
				if(email.matches("")){
					Toast.makeText(getApplicationContext(), "Please enter a valid email Id", Toast.LENGTH_SHORT).show();
				}
				if(password.matches("")){
					Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
				}
				
				//Perform login on firebase
				sgfFirebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
				    @Override
				    public void onAuthenticated(AuthData authData) {
				    	
				    	// Create session
				    	session.createLoginSession(email);
				    	
				        //Login successful navigating to home page
				    	Intent goHome = new Intent(LoginActivity.this, HomeActivity.class);
				    	startActivity(goHome);
				    	finish();
				    	
				    }
				    @Override
				    public void onAuthenticationError(FirebaseError error) {
				        //Login Failed
				    	Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
				    	
				    	// clear edit text
				    	emailEditText.setText("");
				    	passwordEditText.setText("");
				    }
				});
			}
		});
        
        //Sign up button onclicklistener
        signupButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Take user to sign up page
				Intent goToRegistration = new Intent(LoginActivity.this, RegistrationActivity.class);
				startActivity(goToRegistration);
				finish();
			}
		});
        
    }
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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
