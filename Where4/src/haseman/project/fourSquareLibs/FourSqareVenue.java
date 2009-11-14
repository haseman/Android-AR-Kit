package haseman.project.fourSquareLibs;

import ARKit.ARSphericalView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

public class FourSqareVenue extends ARSphericalView
{
	public String name;
	public int checkins;
	public FourSqareVenue(Context ctx)
	{
		super(ctx);
		
	}

	public void draw(Canvas c)
	{
		p.setColor(Color.WHITE);
		if(name != null)
			c.drawText(name, getLeft(), getTop(), p);
			
	}
}
