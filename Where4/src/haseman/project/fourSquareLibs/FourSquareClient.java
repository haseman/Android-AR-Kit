package haseman.project.fourSquareLibs;

import haseman.project.where4.HoldMeUp;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.location.Location;
import android.util.Log;

public class FourSquareClient
{
	int highestCheckin = 0;
	private void adjustAz(Vector v)
	{
		Enumeration e = v.elements();
		while(e.hasMoreElements())
		{
			FourSqareVenue ven = (FourSqareVenue)e.nextElement();
			ven.inclination = (ven.checkins / highestCheckin) * 100;
			Log.e("inc","Inclination is "+ven.inclination);
		}
	}
	public Vector<FourSqareVenue> getVenuList(Location loc)
	{
		//10-28 18:19:50.908: ERROR/where4(3843): Url: http://api.foursquare.com/v1/venues?geolat=37.76299774646759&geolong=-122.41124510765076

		Vector<FourSqareVenue> v = new Vector<FourSqareVenue>();
		try{
		URL url = new URL("http://api.foursquare.com/v1/venues?l=10&geolat="+loc.getLatitude()+"&geolong="+loc.getLongitude());
		Log.e("where4","Url: "+url.toString());
		HttpURLConnection httpconn;
		httpconn = (HttpURLConnection) url.openConnection();
		httpconn.setDoInput(true);
		httpconn.setDoOutput(false);
		httpconn.connect();
		int httpRC = httpconn.getResponseCode();
		if (httpRC == HttpURLConnection.HTTP_OK)
		{
			XmlPullParserFactory x = XmlPullParserFactory.newInstance();
			x.setNamespaceAware(false);
			XmlPullParser p = x.newPullParser();
			p.setInput(new InputStreamReader(httpconn.getInputStream()));
			boolean parsing = true;
			String curText="", name;
			float lat=-1,lon=-1;
			FourSqareVenue curVen = new FourSqareVenue(HoldMeUp.ctx);
			while(parsing)
			{
				int next = p.next();
				switch(next)
				{
				case XmlPullParser.START_TAG:
					name = p.getName();
					if(name.equals("venue"))
						curVen = new FourSqareVenue(HoldMeUp.ctx);
					break;
				case XmlPullParser.END_TAG:
					name = p.getName();
					if(name.equals("venue"))
					{
						curVen.location = new Location("FourSqareApi");
						curVen.location.setLatitude(lat);
						curVen.location.setLongitude(lon);
						v.add(curVen);
					}
					else if(name.equals("name"))
						curVen.name = curText;

					else if(name.equals("geolat"))
						lat = Float.parseFloat(curText);
					else if(name.equals("geolong"))
						lon = Float.parseFloat(curText);
					else if(name.equals("checkins"))
					{
						curVen.checkins = Integer.parseInt(curText);
						if(curVen.checkins > highestCheckin)
							highestCheckin = curVen.checkins;
					}
					break;
				case XmlPullParser.CDSECT:
				case XmlPullParser.TEXT:
					curText = p.getText();
					break;
				case XmlPullParser.END_DOCUMENT:
					parsing = false;
					
				}
			}
			adjustAz(v);
		}
		}catch(Exception e)
		{
			Log.e("4sqrClient","Failed to get");
		}
		
		return v;
	}
}
