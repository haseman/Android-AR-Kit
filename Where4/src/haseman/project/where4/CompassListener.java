//package haseman.project.where4;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.location.GpsStatus.Listener;
//import android.os.Bundle;
//import android.view.View;
//
////Height degrees =25
////Width degrees =38
//public class CompassListener extends View
//{
//	public static SensorManager sensorMan;
//	public static LocationManager locMan;
//	public static Location curLocation = null;
//	private Context ctx;
//	public static float direction = (float) 22.4;
//	public static double inclination;
//	public static double rollingX = (float)0;
//	public static double rollingZ = (float)0;
//	public static float kFilteringFactor = (float)0.05;
//	public static float one = (float)0;
//	public static float two = (float)0;
//	public static float three = (float)0;
//	private boolean locationChanged = false;
//	private LocationListener gpsListener = new LocationListener(){
//
//		public void onLocationChanged(Location location)
//		{
//			if(curLocation == null)
//			{
//				curLocation = location;
//				ARSphericalView.deviceLocation = location;
//				locationChanged = true;
//			}
//			else if(curLocation.getLatitude() == location.getLatitude() &&
//					curLocation.getLongitude() == location.getLongitude())
//				locationChanged = false;
//			else
//				locationChanged = true;
//			
//			curLocation = location;
//		}
//
//		public void onProviderDisabled(String provider){}
//
//		public void onProviderEnabled(String provider){}
//
//		public void onStatusChanged(String provider, int status, Bundle extras){}
//		
//	};
//	private SensorEventListener listener = new SensorEventListener(){
//
//		public void onAccuracyChanged(Sensor arg0, int arg1)
//		{
//			// TODO Auto-generated method stub
//			
//		}
//
//		public void onSensorChanged(SensorEvent evt)
//		{
//			float vals[] = evt.values;
//			
//			if(evt.sensor.getType() == Sensor.TYPE_ORIENTATION)
//			{
//				float tmp = vals[0];
//				//tmp = tmp-90;
//				if(tmp < 0)
//					tmp = tmp+360;
//				
//				direction =(float) ((tmp * kFilteringFactor) + (direction * (1.0 - kFilteringFactor)));
//				//direction = direction-90;
//				if(direction < 0)
//					direction = 360+direction;
////			    rollingZ = (float) ((vals[2] * kFilteringFactor) + (rollingZ * (1.0 - kFilteringFactor)));
////			    if(rollingZ < 0)
////			    	rollingZ = rollingZ * -1;
//			    
//			    //inclination = two*10;
//			    
//			    //inclination = inclination * -1;
//			    
//			    if(locationChanged)
//			    	updateLayouts(direction+90, (float)inclination, curLocation);
//			    else
//			    	updateLayouts(direction, (float)inclination, null);
//			}
//			if(evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
//			{
//				//two =(float) ((vals[2] * kFilteringFactor) + (two * (1.0 - kFilteringFactor)));
//				//hree = (float) ((vals[1] * kFilteringFactor) + (three * (1.0 - kFilteringFactor)));
//				 rollingZ = (vals[2] * kFilteringFactor) + (rollingZ * (1.0 - kFilteringFactor));
//				 rollingX = (vals[0] * kFilteringFactor) + (rollingX * (1.0 - kFilteringFactor));
//				 
//				if (rollingZ != 0.0) 
//				{
//					inclination = Math.atan(rollingX / rollingZ);// + Math.PI / 2.0;
//				} else if (rollingX < 0) 
//				{
//					inclination = Math.PI/2.0;
//				} else if (rollingX >= 0) 
//				{
//					inclination = 3 * Math.PI/2.0;
//				}
//				
//				//convert to degress
//				inclination = inclination * (360/(2*Math.PI));
//				
//				//flip!
//				if(inclination < 0)
//					inclination = inclination + 90;
//				else
//					inclination = inclination -90;
//				
//			}
//			
//			ARLayout.postStaticInvalidate();
//			CompassListener.this.postInvalidate();
//			if(locationChanged)
//		    	ARLayout.updateLayouts(direction+90, (float)inclination, curLocation);
//		    else
//		    	ARLayout.updateLayouts(direction, (float)inclination, null);
//		}
//	};
//	public CompassListener(Context ctx)
//	{
//		super(ctx);
//		this.ctx = ctx;
////		new Thread(){
////			
////			public void run(){
//				sensorMan = (SensorManager)CompassListener.this.ctx.getSystemService(Context.SENSOR_SERVICE);
//				sensorMan.registerListener(listener, sensorMan.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);
//				sensorMan.registerListener(listener, sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
//				locMan = (LocationManager)CompassListener.this.ctx.getSystemService(Context.LOCATION_SERVICE);
//				locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, gpsListener);
//			//}
////		}.start();
//	}
//	
//	protected void onDraw(Canvas c)
//	{
//		super.onDraw(c);
//		//c.drawColor(Color.WHITE);
//		Paint p = new Paint();
//		p.setColor(Color.WHITE);
//
//		c.drawText("Compass:"+String.valueOf(direction), 20, 20, p);
//		c.drawText("ZDirection"+String.valueOf(rollingZ), 40, 40, p);
//		c.drawText("XDirection"+String.valueOf(rollingX), 50, 120, p);
////		
////		c.drawText("ZDirection"+String.valueOf(one), 50, 80, p);
////		if(curLocation != null)
////			c.drawText("LatLon: "+String.valueOf(curLocation.getLatitude()) +":"+String.valueOf(curLocation.getLongitude()), 50, 60, p);
//		//c.drawText("Accel"+String.valueOf(two), 50, 100, p);
//		//c.drawText("YDirection"+String.valueOf(three), 50, 120, p);
//		c.drawText("Inclination"+String.valueOf(inclination), 50, 140, p);
//	}
//	public void close()
//	{
//		sensorMan.unregisterListener(listener);
//		locMan.removeGpsStatusListener((Listener) gpsListener);
//	}
//}
