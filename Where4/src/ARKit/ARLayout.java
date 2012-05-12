package ARKit;

import java.util.Enumeration;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ARLayout extends View implements LocationListener, SensorEventListener
{
	
	private final float xAngleWidth = 29;
	private final float yAngleWidth = 19;
	
	public float screenWidth = 480;
	public float screenHeight = 320;
	private Location lastLocation;
	
	volatile Vector arViews = new Vector();
	
	public SensorManager sensorMan;
	public LocationManager locMan;
	public Location curLocation = null;
	private Context ctx;
	public float direction = (float) 22.4;
	public double inclination;
	public double rollingX = (float)0;
	public double rollingZ = (float)0;
	public float kFilteringFactor = (float)0.05;
	public float one = (float)0;
	public float two = (float)0;
	public float three = (float)0;
	private boolean locationChanged = false;
	public boolean debug = false;
	
	public ARLayout(Context context)
	{
		super(context);
		ctx = context;
		
		sensorMan = (SensorManager)ctx.getSystemService(Context.SENSOR_SERVICE);
		sensorMan.registerListener(this, sensorMan.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);
		sensorMan.registerListener(this, sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
		locMan = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
		locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, this);
		
	}
	public void onLocationChanged(Location location)
	{
		if(curLocation == null)
		{
			curLocation = location;
			ARSphericalView.deviceLocation = location;
			locationChanged = true;
		}
		else if(curLocation.getLatitude() == location.getLatitude() &&
				curLocation.getLongitude() == location.getLongitude())
			locationChanged = false;
		else
			locationChanged = true;
		
		curLocation = location;
		postInvalidate();
	}

	public void onProviderDisabled(String provider){}

	public void onProviderEnabled(String provider){}

	public void onStatusChanged(String provider, int status, Bundle extras){}

	public void onAccuracyChanged(Sensor arg0, int arg1){}

	public void onSensorChanged(SensorEvent evt)
	{
		float vals[] = evt.values;
		float localDirection;
		if(evt.sensor.getType() == Sensor.TYPE_ORIENTATION)
		{
			float tmp = vals[0];
			tmp += 90;
			if(tmp > 360)
				tmp -= 360;
			
			direction =(float) ((tmp * kFilteringFactor) + (direction * (1.0 - kFilteringFactor)));
			//direction = direction-90;
			if(direction < 0)
				localDirection = 360+direction;
			else
				localDirection = direction;
		    
		    if(locationChanged)
		    	updateLayouts(localDirection, (float)inclination, curLocation);
		    else
		    	updateLayouts(localDirection, (float)inclination, null);
		}
		if(evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			 rollingZ = (vals[2] * kFilteringFactor) + (rollingZ * (1.0 - kFilteringFactor));
			 rollingX = (vals[0] * kFilteringFactor) + (rollingX * (1.0 - kFilteringFactor));
			 
			if (rollingZ != 0.0) 
			{
				inclination = Math.atan(rollingX / rollingZ);// + Math.PI / 2.0;
			} else if (rollingX < 0) 
			{
				inclination = Math.PI/2.0;
			} else if (rollingX >= 0) 
			{
				inclination = 3 * Math.PI/2.0;
			}
			
			//convert to degress
			inclination = inclination * (360/(2*Math.PI));
			
			//flip!
			if(inclination < 0)
				inclination = inclination + 90;
			else
				inclination = inclination -90;
			
		}
		if(direction < 0)
			localDirection = 360+direction;
		else
			localDirection = direction;
		
		if(locationChanged)
	    	updateLayouts(localDirection, (float)inclination, curLocation);
	    else
	    	updateLayouts(localDirection, (float)inclination, null);
		
		postInvalidate();
	}
	
	//Sort views by distance
	private void sortArViews()
	{
		//TODO
	}
	public void addARView(ARSphericalView view)
	{
		arViews.add(view);
	}
	public void removeARView(ARSphericalView view)
	{
		arViews.remove(view);
	}
	private boolean isVisibleY(float lowerArm, float upperArm, float inc)
	{
		return true;//(inc >= lowerArm &&inc <= upperArm);
	}
	
	public void clearARViews()
	{
		arViews.removeAllElements();
	}
	//Given a point, is it visible on the screen?
	private boolean isVisibleX(float leftArm, float rightArm, float az)
	{
//		//Flip!
//		if(leftArm > rightArm)
//		{
//			if(!(az <= leftArm && az >= rightArm))
//				return false;
//		}
//		else
//		{
//			if(!(az >= leftArm && az <= rightArm))
//				return false;
//		}
		
		return true;
	}
	private  float calcXvalue(float leftArm, float rightArm, float az)
	{
		float ret = 0;
		float offset;
		if(leftArm > rightArm)
		{
			if(az >= leftArm)
			{
				offset = az - leftArm;
			}
			if(az <= rightArm)
			{
				offset =  360 - leftArm + az;
			}
			else
				offset = az - leftArm;
		}
		else
		{
			offset = az - leftArm;
		}
		
		return (offset/xAngleWidth) * screenWidth;
	}
	private float calcYvalue(float lowerArm, float upperArm, float inc)
	{
		//distance in degress to the lower arm
		float offset = ((upperArm - yAngleWidth) - inc) * -1;
		return screenHeight - ((offset/yAngleWidth) * screenHeight);
	}
	public void onDraw(Canvas c)
	{
		//Log.e("Spec","Updating "+arViews.size()+" views");
		//long time = System.currentTimeMillis();
		Enumeration<ARSphericalView> e = arViews.elements();
		if(debug)
		{
			Paint p = new Paint();
			p.setColor(Color.WHITE);

			c.drawText("Compass:"+String.valueOf(direction), 20, 20, p);

			c.drawText("Inclination"+String.valueOf(inclination), 150, 20, p);
		}
		while(e.hasMoreElements())
		{
			ARSphericalView view = e.nextElement();
//			if(!view.visible)
//				continue;
			view.draw(c);
		}
		//Log.e("Spec","Took "+(System.currentTimeMillis() - time)+" seconds");
	}
	public void updateLayouts(float Azi, float zAngle, Location l)
	{

		if(Azi != -1)
		{
			//Process the acceleromitor stuff
			float leftArm = Azi -(xAngleWidth/2);
			float rightArm = Azi +(xAngleWidth/2);
			if(leftArm < 0)
				leftArm = leftArm + 360;
			if(rightArm > 360)
				rightArm = rightArm - 360;
			
			float upperArm = zAngle + (yAngleWidth/2);
			float lowerArm = zAngle - (yAngleWidth/2);
			
			Enumeration<ARSphericalView> e = arViews.elements();

			if(arViews.size() == 0)
				return;
			
			while(e.hasMoreElements())
			{
				//If we have a location, and the view has one, update it's data
				try{
					ARSphericalView view = e.nextElement();
					if(l != null && view.location != null)
					{
						view.azimuth = l.bearingTo(view.location);
						if(view.azimuth < 0)
							view.azimuth = 360+view.azimuth;
						if(l.hasAltitude() && view.location.hasAltitude())
						{
							view.inclination = (float) Math.atan(((view.location.getAltitude() - l.getAltitude()) / l.distanceTo(view.location)));
						}
					}
	//				if(!isVisibleX(leftArm, rightArm, view.azimuth))
	//				{
	//					view.visible = false;
	//					continue;
	//				}
	//				if(!isVisibleY(lowerArm, upperArm, view.inclination))
	//				{
	//					view.visible = false;
	//					continue;
	//				}
					view.visible = true;
					
					view.layout((int)calcXvalue(leftArm, rightArm, view.azimuth), (int)calcYvalue(lowerArm, upperArm, view.inclination), view.getBottom(), view.getRight());
				}
				catch(Exception x)
				{
					Log.e("ArLayout", x.getMessage());
				}
			}
			
		}
		//37.763557,-122.410719
		
	}

	public void close()
	{
		sensorMan.unregisterListener(this);
		locMan.removeUpdates(this);
	}
	
}
