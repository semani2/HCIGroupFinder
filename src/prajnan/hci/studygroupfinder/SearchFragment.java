package prajnan.hci.studygroupfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Group;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import session.SessionManager;
import adapter.CustomListAdapter;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SearchFragment extends Fragment {
	
	Button searchButton;
	ListView searchList;
	EditText searchEditText;
	SessionManager session;
	Map<String, String> userDetails = new HashMap<String,String>();
	TextView welcomeText;
	private ProgressDialog pDialog;
	private CustomListAdapter adapter;
	Firebase groupsFirebase;
	private List<Group> groupList = new ArrayList<Group>();
	Map<String, String> groupDetails  = new HashMap<String, String>();
	String searchKey;
	
	public SearchFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		 View rootView = inflater.inflate(R.layout.fragment_search, container, false);
		 
		 session = new SessionManager(getActivity());
		 
		 //Firebase
		 Firebase.setAndroidContext(getActivity());
		 
		 searchButton = (Button) rootView.findViewById(R.id.searchButton);
		 searchList = (ListView) rootView.findViewById(R.id.searchList);
		 searchEditText = (EditText) rootView.findViewById(R.id.searchEditText);
		 
		 pDialog = new ProgressDialog(getActivity());
	      // Showing progress dialog before making http request
	     pDialog.setMessage("Searching...");
	     pDialog.show();
	      
	     searchButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				groupList.clear();
				if(adapter != null)
				{
					adapter.notifyDataSetChanged();
				}
				searchKey = searchEditText.getText().toString();
				groupsFirebase = new Firebase("https://study-group-finder.firebaseio.com/groups");
				 groupsFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
					    @Override
					    public void onDataChange(DataSnapshot snapshot) {
					    	Map<String, Object> groups = new HashMap<String, Object>();
					    	groups = (HashMap<String, Object>)snapshot.getValue();
					    	if(groups != null){
					    	Log.d("SearchGroups",groups.toString());
					    	
					    	// Next iterate over the users to get the UID of the users added to the group
					    	Log.d("Search Key Set",groups.keySet().toString());
					    	Set keySet = groups.keySet();
					    	
					    	Iterator it = groups.entrySet().iterator();
					    	//List<String> uids = new ArrayList<String>();
					    	while(it.hasNext()){
					    		Map.Entry pairs = (Map.Entry) it.next();
					    		Log.d("Search Groups Hash MAp",pairs.getKey()+"="+pairs.getValue());
					    		Map<String, Object> currentGroup = (HashMap<String, Object>)pairs.getValue();
					    		Log.d("Search Current Group Keys",currentGroup.keySet().toString());
					    		Group group = new Group();
	                	    	groupDetails = (HashMap<String, String>) pairs.getValue();
	                	    	Log.d("Search GroupKeys",groupDetails.keySet().toString());
	                	    	group.setTitle(groupDetails.get("name"));
	                	    	group.setPlace(groupDetails.get("place"));
	                	    	group.setDate(groupDetails.get("date"));
	                	    	group.setCourse(groupDetails.get("course"));
	                	    	group.setGroupPic(groupDetails.get("groupPic"));
	                	    	group.setGroupId(pairs.getKey().toString());
	                	    	//Add group to group List
	                	    	if(groupDetails.get("course").equals(searchKey))
	                	    	{
	                	    	groupList.add(group);
	                	    	}
					    		it.remove();
					    		adapter.notifyDataSetChanged();
					    	}
					    	if(groupList.size() ==0){
					    		Toast.makeText(getActivity(), "No search results found", Toast.LENGTH_LONG).show();
					    	}
					    	}
					    	else
					    	{
					    		Toast.makeText(getActivity(), "No study grouos found!", Toast.LENGTH_LONG).show();
					    	}
					    }
					    @Override
					    public void onCancelled(FirebaseError firebaseError) {
					    }
					});
				
			}
		});
		 
	     adapter = new CustomListAdapter(getActivity(), groupList);
	     searchList.setAdapter(adapter);
	     adapter.notifyDataSetChanged();
	     hidePDialog();
	     
	     searchList.setOnItemClickListener(new OnItemClickListener() {

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
		 //TODO 
	}
	private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}
