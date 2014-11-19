package prajnan.hci.studygroupfinder;

import java.util.HashMap;
import java.util.Map;

import session.SessionManager;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {
	
	SessionManager session;
	Map<String, String> userDetails = new HashMap<String,String>();
	TextView welcomeText;
	
	public HomeFragment(){}
	
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			
			 View rootView = inflater.inflate(R.layout.fragment_app_home, container, false);
			 session = new SessionManager(getActivity());
			 session.checkLogin();
			 
			 welcomeText = (TextView)rootView.findViewById(R.id.textView2);
			 
			 
			 userDetails = session.getUserDetails();
			 
			 welcomeText.setText(welcomeText.getText().toString() + "" +userDetails.get("email"));
			 
			 return rootView;
	 }

}
