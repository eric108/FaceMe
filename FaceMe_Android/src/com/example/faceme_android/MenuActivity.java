package com.example.faceme_android;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

//import com.example.faceme_android.backgroundservice.PairingServiceActivity;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MenuActivity extends Activity{
	 String feedUrl="https://facemegatech.appspot.com/_ah/api/userendpoint/v1/user/get/Ziyi%20Jiang";
	 ListView userProfileListView;
	 Context context;
	 List<UserProfile> userProfileList=new ArrayList<UserProfile>();
     ArrayList<String>profileUrl=new ArrayList<String>();

	@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.thirdpage);
	        context=this;
	       userProfileListView=(ListView) findViewById(R.id.profile);
	       
//	       userProfileList.add(new UserProfile());
//	       
	       ArrayAdapter<UserProfile> userProfileAdapter=new ProfileAdapter(userProfileList);
	       jsonDataManipulation jsonMani=new jsonDataManipulation(userProfileAdapter);
	       jsonMani.execute();
	       userProfileListView.setAdapter(userProfileAdapter);
	       
	       Button btn_play=(Button)findViewById(R.id.Button04);
	       Button btn_browse = (Button) findViewById(R.id.Button02);
	       Button btn_pairingservice = (Button)findViewById(R.id.Button03);
	       
	       btn_pairingservice.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//	startActivity(new Intent(getBaseContext(),PairingServiceActivity.class));
			}
		});
	       
	       btn_play.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(new Intent(getBaseContext(),PosterSelectionActivity.class));
					//startActivity(new Intent(getBaseContext(),CameraActivity.class));
				}
			});
	       btn_browse.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//startActivity(new Intent(getBaseContext(),RateAndCommentActivity.class));
					startActivity(new Intent(getBaseContext(),CharacterSelectionActivityXiaofei.class));
				}
			});
	       
	 }
	
	  public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	  }
	  
	  public class ProfileAdapter extends ArrayAdapter<UserProfile>{
		  public ProfileAdapter(List<UserProfile> profileList){
			  super(MenuActivity.this,R.layout.profilelist,profileList);
		  }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View tempView=convertView;
			if(tempView==null){
				tempView=getLayoutInflater().inflate(R.layout.profilelist, parent, false);
			}
			UserProfile currentUser=userProfileList.get(position);
		    
			ImageView imageView=(ImageView) tempView.findViewById(R.id.imageView_profilePic);
			Bitmap btm=currentUser.getFaceBmp();
			
            imageView.setImageBitmap(RoundCorner.getRoundedCornerBitmap(btm, 500));
            
			TextView name=(TextView)tempView.findViewById(R.id.textView_name);
		    name.setText("Name: "+currentUser.getUsername());
		    TextView gender=(TextView)tempView.findViewById(R.id.textView_gender);
		    gender.setText("Gender: "+currentUser.getGender());
		    TextView school=(TextView)tempView.findViewById(R.id.textView_school);
		    school.setText("School: "+currentUser.getSchool());
		    
			return tempView;
		}
		
	  }
	  public class jsonDataManipulation extends AsyncTask<Void, Void, Void>{
		    ProgressDialog dialog;
		    ArrayAdapter<UserProfile> profileadapter;
		    public jsonDataManipulation( ArrayAdapter<UserProfile> profileadapt) {
				// TODO Auto-generated constructor stub
		    	profileadapter=profileadapt;
			}
			@Override
			
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// TODO Auto-generated method stub
				HttpClient client=new DefaultHttpClient();
				HttpGet httpGet=new HttpGet(feedUrl);
				try {
					
					HttpResponse response=client.execute(httpGet);
					StatusLine statusline=response.getStatusLine();
					int statusCode=statusline.getStatusCode();
					if(statusCode!=200){
						return null;
					}
					InputStream jsonStream =response.getEntity().getContent();
					BufferedReader reader=new BufferedReader(new InputStreamReader(jsonStream));
					StringBuilder builder=new StringBuilder();
					String line;
					while((line=reader.readLine())!=null){
						
						builder.append(line);
					}
					String jsonData=builder.toString();
					Log.i("JsonData", jsonData);
					JSONObject json= new JSONObject(jsonData);
					
					String name = json.getString("userID");
					String gender = json.getString("gender");
					String school = json.getString("school");
					String picUrl=json.getString("faceKey");
					
					Bitmap bmp=getImageBitmap(picUrl);
					
					userProfileList.add(new UserProfile(name,gender,school,picUrl,bmp));
					GlobalState state = (GlobalState) getApplicationContext();
					state.currentUser = userProfileList.get(0);
					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			   profileadapter.notifyDataSetChanged();
				super.onPostExecute(result);
				
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				dialog=new ProgressDialog(context);
				dialog.setTitle("loading");
				dialog.show();
				super.onPreExecute();
				
			}
			
			private Bitmap getImageBitmap(String url) { 
	            Bitmap bm = null; 
	            try { 
	                URL aURL = new URL(url); 
	                URLConnection conn = aURL.openConnection(); 
	                conn.connect(); 
	                InputStream is = conn.getInputStream(); 
	                BufferedInputStream bis = new BufferedInputStream(is); 
	                bm = BitmapFactory.decodeStream(bis); 
	                bis.close(); 
	                is.close(); 
	           } catch (IOException e) { 
	        	   e.printStackTrace();
	           } 
	           return bm; 
	        } 	  
			
			
		}
	
}
