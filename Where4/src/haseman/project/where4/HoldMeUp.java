package haseman.project.where4;

import haseman.project.fourSquareLibs.FourSqareVenue;
import haseman.project.fourSquareLibs.FourSquareClient;

import java.util.Enumeration;
import java.util.Vector;

import ARKit.ARLayout;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class HoldMeUp extends Activity {
    /** Called when the activity is first created. */
	CustomCameraView cv;
	public static volatile Context ctx;
	ARLayout ar;
	volatile Location curLocation = null;
	
	private LocationListener gpsListener = new LocationListener(){

		public void onLocationChanged(Location location)
		{
			Log.e("HoldMe","Got first");
			if(curLocation != null)
				return;
			curLocation = location;
			new Thread(){
	    		public void run(){
	    			FourSquareClient fc = new FourSquareClient();
	    			Vector<FourSqareVenue> vc = fc.getVenuList(curLocation);
	    			Log.e("Where4","CurLocation LA:"+curLocation.getLatitude()+" LO:"+curLocation.getLongitude());
	    			ar.clearARViews();
	    			if(vc != null && vc.size() > 0)
	    			{
	    				Enumeration e = vc.elements();
	    				while(e.hasMoreElements())
	    				{
	    					FourSqareVenue fq = (FourSqareVenue) e.nextElement();
	    					Log.e("Where4","Got Venue:"+fq.name);
	    					if(fq.location != null)
	    						Log.i("Where4", "Lat:"+fq.location.getLatitude()+":"+fq.location.getLongitude());
	    					Log.e("Where4", "Azimuth: "+fq.azimuth);
	    					ar.addARView(fq);
	    				}
	    			}
	    		}
	        }.start();
	        LocationManager locMan = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
	        locMan.removeUpdates(this);
		}

		public void onProviderDisabled(String provider){}

		public void onProviderEnabled(String provider){}

		public void onStatusChanged(String provider, int status, Bundle extras){}
		
	};
    
	private void addLoadingLayouts()
	{
		FourSqareVenue fs = new FourSqareVenue(this.getApplicationContext());
		fs.azimuth = 0;
		fs.inclination = 0;
		fs.name = "Loading";
		ar.addARView(fs);
		fs = new FourSqareVenue(this.getApplicationContext());
		fs.azimuth = 45;
		fs.inclination = 0;
		fs.name = "Loading";
		ar.addARView(fs);
		fs = new FourSqareVenue(this.getApplicationContext());
		fs.azimuth = 90;
		fs.inclination = 0;
		fs.name = "Loading";
		ar.addARView(fs);
		fs = new FourSqareVenue(this.getApplicationContext());
		fs.azimuth = 135;
		fs.inclination = 0;
		fs.name = "Loading";
		ar.addARView(fs);
		fs = new FourSqareVenue(this.getApplicationContext());
		fs.azimuth = 180;
		fs.inclination = 0;
		fs.name = "Loading";
		ar.addARView(fs);
		fs = new FourSqareVenue(this.getApplicationContext());
		fs.azimuth = 210;
		fs.inclination = 0;
		fs.name = "Loading";
		ar.addARView(fs);
		fs = new FourSqareVenue(this.getApplicationContext());
		fs.azimuth = 270;
		fs.inclination = 0;
		fs.name = "Loading";
		ar.addARView(fs);
		ar.postInvalidate();
	}
	
    public void onStart()
    {
    	super.onStart();
    }
    public void onCreate(Bundle savedInstanceState) {
    	try{
	        super.onCreate(savedInstanceState);
	        
	        ctx = this.getApplicationContext();
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	        requestWindowFeature(Window.FEATURE_NO_TITLE); 
	        
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                       WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	
	        ar = new ARLayout(getApplicationContext());
	        
	        cv = new CustomCameraView(this.getApplicationContext());
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        //cl = new CompassListener(this.getApplicationContext());
	        WindowManager w = getWindowManager();
            Display d = w.getDefaultDisplay();
        	int width = d.getWidth();
            int height = d.getHeight(); 
	        ar.screenHeight = height;
	        ar.screenWidth = width;
	        FrameLayout rl = new FrameLayout(getApplicationContext());
	        rl.addView(cv,width, height);
	        ar.debug = true;
	        rl.addView(ar, width, height);
	        setContentView(rl);
			LocationManager locMan = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
			locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, gpsListener);
	        //Log.e("Where","Orientation:"+i);
	        //rl.bringChildToFront(cl);
			addLoadingLayouts();
        
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
   public void onDestroy()
   {
	   super.onDestroy();
	   cv.closeCamera();
	   ar.close();
	   //cl.close();
	   //cv.closeCamera();
   }
}