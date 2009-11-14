package ARKit;

public class SphericalPoint
{
	public float azimuth; //Angle from north
	public float distance; //Distance to object
	public float inclination; //angle off horizon.
	
	//used to compute inclination
	public static float currentAltitude = 0;
	
	public SphericalPoint(float angleFromNorth, float distance, float altitude)
	{
		this.distance = distance;
		//arctan of opposite/adjacent
		float opposite;
		boolean neg = false;
		if(altitude > currentAltitude)
		{
			opposite = altitude - currentAltitude;
		}
		else
		{
			opposite = currentAltitude - altitude;
			neg = true;
		}
		inclination = (float) Math.atan(((double)opposite/distance));
		if(neg)
			opposite = opposite * -1;
	}
}
