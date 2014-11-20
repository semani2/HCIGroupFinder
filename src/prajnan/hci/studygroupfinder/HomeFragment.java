package prajnan.hci.studygroupfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import model.Group;

import session.SessionManager;
import adapter.CustomListAdapter;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class HomeFragment extends Fragment {
	
	SessionManager session;
	Map<String, String> userDetails = new HashMap<String,String>();
	TextView welcomeText;
	private ProgressDialog pDialog;
    private List<Group> groupList = new ArrayList<Group>();
    private ListView listView;
    private CustomListAdapter adapter;
    Firebase userGroupFirebase, groupsFirebase;
    String uid, email;
    List<String> groupIds = new ArrayList<String>();
    Map<String, String> groupDetails  = new HashMap<String, String>();
	
	public HomeFragment(){}
	
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			
			 View rootView = inflater.inflate(R.layout.fragment_app_home, container, false);
			 session = new SessionManager(getActivity());
			 session.checkLogin();
			 
			 userDetails = session.getUserDetails();
			 uid = userDetails.get("uid");
			 email = userDetails.get("email");
			 
			 Firebase.setAndroidContext(getActivity());
			 
			 
			 listView = (ListView) rootView.findViewById(R.id.list);
		       
		 
		        pDialog = new ProgressDialog(getActivity());
		        // Showing progress dialog before making http request
		        pDialog.setMessage("Loading...");
		        pDialog.show();
		        
		        // Next lets get all the groups the user is a member of
		        userGroupFirebase = new Firebase("https://study-group-finder.firebaseio.com/users/"+uid+"/Groups");
		        userGroupFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
		            @Override
		            public void onDataChange(DataSnapshot snapshot) {
		                groupIds = (List<String>) snapshot.getValue();
		                Log.d("GroupIDs",groupIds.toString());
		                //Iterate over each of the groupID to populate the list
		                for(int i=0;i<groupIds.size();i++)
		                {
		                	final int j = i;
		                	groupsFirebase = new Firebase("https://study-group-finder.firebaseio.com/groups/"+groupIds.get(i));
		                	groupsFirebase.addValueEventListener(new ValueEventListener() {
		                	    @Override
		                	    public void onDataChange(DataSnapshot snapshot) {
		                	        //System.out.println(snapshot.getValue());
		                	    	//Retreive the group details
		                	    	Group group = new Group();
		                	    	groupDetails = (HashMap<String, String>) snapshot.getValue();
		                	    	Log.d("GroupKeys",groupDetails.keySet().toString());
		                	    	group.setTitle(groupDetails.get("name"));
		                	    	group.setPlace(groupDetails.get("place"));
		                	    	group.setDate(groupDetails.get("date"));
		                	    	group.setCourse(groupDetails.get("time"));
		                	    	group.setGroupPic(groupDetails.get("groupPic"));
		                	    	group.setGroupId(groupIds.get(j));
		                	    	//Add group to group List
		                	    	groupList.add(group);
		                	    	adapter.notifyDataSetChanged();
		                	    }
		                	    @Override
		                	    public void onCancelled(FirebaseError firebaseError) {
		                	       // System.out.println("The read failed: " + firebaseError.getMessage());
		                	    }
		                	});
		                }
		                hidePDialog();
		                
		                
		            }
		            @Override
		            public void onCancelled(FirebaseError firebaseError) {
		            }
		        });
		        Log.d("GroupList",groupList.toString());
		        adapter = new CustomListAdapter(getActivity(), groupList);
		        listView.setAdapter(adapter);
		        adapter.notifyDataSetChanged();
		        listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						TextView groupIdText = (TextView) view.findViewById(R.id.groupIdText);
						String groupId = groupIdText.getText().toString();
						
						//Toast.makeText(getActivity(), "Clicked at positon = " + position + " GroupId: "+groupId, Toast.LENGTH_SHORT).show();
						//Navigate to Group Page
						Intent groupPage = new Intent(getActivity(),GroupActivity.class);
						groupPage.putExtra("groupId", groupId);
						startActivity(groupPage);
					}
				});
		        

		        
			 
			 return rootView;
	 }
	 private void hidePDialog() {
	        if (pDialog != null) {
	            pDialog.dismiss();
	            pDialog = null;
	        }
	    }

}
