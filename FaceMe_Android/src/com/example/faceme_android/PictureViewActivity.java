package com.example.faceme_android;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class PictureViewActivity extends Activity {

	public PictureViewActivity(){
		
	}
	GlobalState state;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		state = (GlobalState) getApplicationContext();
		setContentView(R.layout.activity_picture_view);
		ImageView imview = (ImageView) findViewById(R.id.imageView);
		
		Bitmap tmp = Tools.getBitmapFromPath(Environment.getExternalStorageDirectory().getPath() +"/CosplayTmp.png");
		
		
		GlobalState state = (GlobalState) getApplicationContext();
		Bitmap nonfacePoster = state.currentPoster.nonfacePoster;
		//Tools.getBitmapFromPath(Environment.getExternalStorageDirectory().getPath() +"/CosplayTmp.png"));

		CharacterFace facechoosed = state.faceChosed;
		
		
		Bitmap userFaceBmp = Bitmap.createScaledBitmap(tmp, (int)(nonfacePoster.getWidth() * facechoosed.getWidth()), (int)(nonfacePoster.getHeight() * facechoosed.getHeight()), false);
		Bitmap result = Bitmap.createBitmap(nonfacePoster.getWidth(), nonfacePoster.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		
		for(CharacterFace face : state.currentPoster.faces){
			if(face != facechoosed){
				canvas.drawBitmap(face.bmp, face.getPositionX() * nonfacePoster.getWidth(), face.getPostionY() * nonfacePoster.getHeight(), null);
			}
		}
		
		canvas.drawBitmap(userFaceBmp, facechoosed.getPositionX() * nonfacePoster.getWidth(), facechoosed.getPostionY() * nonfacePoster.getHeight(), null);
		canvas.drawBitmap(nonfacePoster, 0, 0, null);

		imview.setImageBitmap(result);
		try {
		    String filename = Environment.getExternalStorageDirectory().getPath() +"/CosplayTmp.png";
			FileOutputStream out = new FileOutputStream(filename);
			result.compress(Bitmap.CompressFormat.PNG, 90, out);
		    out.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		Button btn_share = (Button)findViewById(R.id.button_Share);
		Button btn_upload = (Button)findViewById(R.id.button_Upload);
		
		btn_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("image/png");
				File f = new File(Environment.getExternalStorageDirectory().getPath()+"/CosplayTmp.png");
				Uri uri = Uri.fromFile(f); intent.putExtra(Intent.EXTRA_STREAM, uri); 
				
				intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
				intent.putExtra(Intent.EXTRA_TEXT,
						"FaceMe is awesome! You should also try this!");
				startActivity(Intent.createChooser(intent, getTitle()));
				
			}
		});
		
		Button btn_home = (Button) findViewById(R.id.button_home);
		btn_home.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					startActivity(new Intent(getBaseContext(),MenuActivity.class));
			}
		});
		
		btn_upload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createNotification();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.picture_view, menu);
		return true;
	}
	 public void createNotification() {
	        // Prepare intent which is triggered if the
	        // notification is selected
	        Intent intent = new Intent(this, RateAndCommentActivity.class);
	        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	        Bitmap logoIcon=Tools.getBitmapFromAsset(this, "logo.png");
	        
	        // Build notification

	        NotificationCompat.Builder noti = new NotificationCompat.Builder(this)
	        .setContentTitle("You Got a Paired Photo! " )
	        .setContentText("FaceMe app")
	        .setSmallIcon(R.drawable.logo_launcher)
	        .setContentIntent(pIntent)
	        .addAction(R.drawable.ic_launcher, "Action Button", pIntent);

	        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	        // hide the notification after its selected
	        notificationManager.notify(0, noti.build());

	      }

}
