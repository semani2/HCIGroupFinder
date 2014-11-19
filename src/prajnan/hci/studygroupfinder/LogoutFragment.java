package prajnan.hci.studygroupfinder;

import session.SessionManager;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class LogoutFragment extends Fragment {
	
	SessionManager session;
	
	public LogoutFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		 View rootView = inflater.inflate(R.layout.fragment_logout, container, false);
		 
		 Toast.makeText(getActivity(), "You have been successfully logged out!", Toast.LENGTH_LONG).show();
		 session = new SessionManager(getActivity());
		 session.logoutUser();
		 return rootView;
		
	}

}
