package g2scad;

import gcodeviewer.parsers.GCodeParser;
import gcodeviewer.parsers.MightyParser;
import gcodeviewer.toolpath.GCodeEvent;
import gcodeviewer.toolpath.GCodeEventToolpath;
import gcodeviewer.toolpath.events.EndExtrusion;
import gcodeviewer.toolpath.events.MoveTo;
import gcodeviewer.toolpath.events.SetFeedrate;
import gcodeviewer.toolpath.events.SetMotorSpeedRPM;
import gcodeviewer.toolpath.events.StartExtrusion;
import gcodeviewer.utils.Point5d;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import replicatorg.ToolModel;

public class HugosThing {

	public void writeToScad(GCodeEventToolpath path) {
		double feedrate = 0;
		double extrusionSpeed = 0;
		boolean isExtruding = false;
		Point5d lastPoint = null;
		for(GCodeEvent evt : path.events()) {

			if(evt instanceof SetMotorSpeedRPM) {
				extrusionSpeed = ((SetMotorSpeedRPM)evt).speed;
			}
			if(evt instanceof SetFeedrate) {
				feedrate = ((SetFeedrate)evt).feedrate;
			}

			if(evt instanceof StartExtrusion) {
				if(((StartExtrusion)evt).direction == ToolModel.MOTOR_COUNTER_CLOCKWISE)
					extrusionSpeed = Math.abs(extrusionSpeed)*-1;
				if(((StartExtrusion)evt).direction == ToolModel.MOTOR_CLOCKWISE)
					extrusionSpeed = Math.abs(extrusionSpeed);
				isExtruding = true;
			}

			if(evt instanceof EndExtrusion) {
				isExtruding = false;
			}
			
			if(evt instanceof MoveTo) 
			{
				String magick = "extrude";
				Point5d newPoint = ((MoveTo)evt).point;
				if(lastPoint != null)
				{	
					
					if(extrusionSpeed == 0 || !isExtruding)
					{
						magick = "moveto";
					}
					if(extrusionSpeed > 10 && isExtruding)
					{
						magick = "squirt";
					}
					if(extrusionSpeed < 0 && isExtruding)
					{
						magick = "snort";
					}
					
					System.out.println(magick + "(" +lastPoint.x()+
										", " + 
										lastPoint.y() +
										", " +
										lastPoint.z() + 
										", " + 
										newPoint.x() + 
										", " +
										newPoint.y() + 
										", " + 
										newPoint.z() +
										", " + 
										feedrate + 
										", " + 
										extrusionSpeed +
										");");
				}
				lastPoint = newPoint;
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("// wham bam thank you Ted :-)\n\n");
		System.out.println("// wham bam thank you Ted :-)\n\n");
		
		String sourceFile = args[0];
		System.out.println("(file: " + sourceFile + ")");
		try
		{
			Scanner in = new Scanner( new File(sourceFile)); 
			ArrayList<String> vect = new ArrayList<String>();
			while( in.hasNext())
			{
				
				String nextline = in.nextLine();
				System.out.println(nextline);
				vect.add(nextline);
			}
			
			GCodeParser parser = new MightyParser();
			parser.parse(vect);
			new HugosThing().writeToScad(parser.getPath());
		}
		catch(Exception fail)
		{
			System.out.println(fail);
			System.out.println("You moron! you made me crash");
		}	
	}
	

}
