package com.mydomain;

import java.awt.*;
import buttoncountpc.ButtonCounter;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import lejos.nxt.*;

/** Date: September 13, 2012
 * Class Name: Milestone3
 * Authors: Moonsoo Choi and Sherman Siu
 * Program description: Program navigates a 6x8 coordinate grid, with the user inputting the coordinate
 * values into the robot using ButtonCounter.java. Tracker is used to steer the robot.
 * Note: Tracker has not changed since Milestone 2.
 */

public class Milestone3 
{
	//Set up DifferentialPilot on the robot with light sensor. Uses tracker to track.
	DifferentialPilot myPilot = new DifferentialPilot((float)(56/25.4),4.8f,Motor.A,Motor.B,false);
	LightSensor myLeftLightSensor = new LightSensor(SensorPort.S1);
	LightSensor myRightLightSensor = new LightSensor(SensorPort.S4);
	Tracker tracker = new Tracker(myPilot, myLeftLightSensor, myRightLightSensor);
	
	//Robot keeps track of three points: old location, current location, and new location.
	Point currentPoint = new Point();
	Point newPoint = new Point();
	Point oldPoint = new Point();
	
	/**
	 * Calibrate the robot.
	 */
	void calibrate()
  	{
  		tracker.calibrate();
  	}
	
	/**
	 * The robot navigates the x-axis of the grid.
	 */
	void moveXdistance()
	{
	 /**
	  * PART ONE OF moveXdistance():
	  * Depending on the old location, the robot will turn (or turn around) left or right and set
	  * itself up to navigate the new location via the x-axis.	
	  **/	
		//If the y-value of the old location is less than the y-value of the current location, do this:
		if (oldPoint.y < currentPoint.y)
		{
			/*
			Turn right if the new location's y-value is greater than the current location's y-value;
			otherwise if less than, turn left.
			*/
			if (newPoint.x > currentPoint.x)
			{
				myPilot.rotate(90);
			}
			else if (newPoint.x < currentPoint.x)
			{
				myPilot.rotate(-90);
			}
		}
		//If the y-value of the old location is greater than the y-value of the current location, do this:
		else if (oldPoint.y > currentPoint.y)
		{
			/*
			If the new point's x-value is greater than the x-value of the current point, turn left.
			Else if it is less than, turn right. 
			 */
			if (newPoint.x > currentPoint.x)
			{
				myPilot.rotate(-90);
			}
			else if (newPoint.x < currentPoint.x)
			{
				myPilot.rotate(90);
			}
		}
		/*
		The following is to allow the robot to shuffle along the x-axis. Example: If you want your robot
		to go to (2,0) from (0,0) and then go to (3,0) and then go to (1,0), this part makes the robot
		navigate this.  
		 */
		//If the old point's y-value is equal to the current point's y-value, do the following:
		else
		{	
			/*
			If the old point's x-value is less than the current point's x-value, then check to see
			if the new point's x-value is less than the current point's x-value. If so, then turn around
			180 degrees. Otherwise, stay put and follow the next command.   
			 */
			if (oldPoint.x < currentPoint.x)
			{
				if (newPoint.x < currentPoint.x)
				{
					myPilot.rotate(180);
				}
			}
			/*
			Else if the old point's x-value is greater than the current point's x-value, 
			then check to see if the new point's x-value is greater than the current point's x-value. 
			If so, turn around 180degrees. Otherwise, stay put and follow the next command.
			*/
			else if (oldPoint.x > currentPoint.x)
			{
				if (newPoint.x > currentPoint.x)
				{
					myPilot.rotate(180);
				}
			}
		}
		
		/**
		 * PART TWO OF moveXdistance(). After knowing which direction to turn, then travel this distance
		 * along the x-axis using trackLine(). The robot uses the number of times it passes by black tape
		 * to determine its current coordinates; robot will stop when arrive at new point's x-value.
		 */
		//Initialize i, the number of times the robot passes by black tape.
		int i=0; 
		/*Initialize x_distance, number of times the robot has to pass the black tape before arriving at 
		  the new point's x-value (defined as absolute value of the difference between new point and
		  current point x-values. */
		int x_distance = Math.abs(newPoint.x - currentPoint.x);
		
		//while loop that keeps running until the robot passes the black tape an appropriate # of times
		while (i < x_distance)
		{
			int lval = tracker.lvalue(); //determine left sensor light value
  			int rval = tracker.rvalue(); //determine right sensor light value
  			tracker.trackLine(); //follow the blue tape		
  			//tells the robot what to do when it approaches the black tape
  			if (lval<0||rval<0)
  			{
  				Sound.beep(); //make a sound
  				i=i+1; //increase the "robot black tape approach count" by 1		
  				LCD.drawInt(currentPoint.x + i,12,0); //show x-value of where robot is right now
  				//if the robot reaches newPoint's x-value, do not rotate and break this loop.
  				if (i == x_distance)
  				{
  					tracker.rotate(0);
  					break;
  				}
  				//else the robot just goes past the black tape and continues its journey
  				else 
  				{
  					while (true)
  					{
  						lval = tracker.lvalue();
  						rval = tracker.rvalue();
  						myPilot.steer(0);
  						if (lval >= 0 && rval >= 0)
  						{
  							break;
  						}
  					}
  				}
  			}
		}
	}
	
	/**
	 * The robot navigates the y-axis of the grid after running moveXdistance().
	 */
	void moveYdistance()
	{
	/**
	 * PART ONE OF moveYdistance():
	 * The robot will turn (or turn around) left or right and set
	 * itself up to navigate the new location via the y-axis.	
	**/	
		//If the x-value of newPoint is less than the x-value of currentPoint, do this:
		if (newPoint.x < currentPoint.x)
		{
			/*
			Turn right if newPoint's y-value is greater than currentPoint's y-value;
			otherwise if less than, turn left.
			*/
			if (newPoint.y > currentPoint.y)
			{
				myPilot.rotate(90);
			}
			else if (newPoint.y < currentPoint.y)
			{
				myPilot.rotate(-90);
			}
		}
		//If the x-value of newPoint is greater than currentPoint's x-value, do this:
		else if (newPoint.x > currentPoint.x)
		{
			/*
			If newPoint's y-value is greater than the y-value of currentPoint, turn left.
			Else if it is less than, turn right. 
			 */
			if (newPoint.y > currentPoint.y)
			{
				myPilot.rotate(-90);
			}
			else if (newPoint.y < currentPoint.y)
			{
				myPilot.rotate(90);
			}
		}
		/*
		The following is to allow the robot to shuffle along the y-axis. Example: If you want your robot
		to go to (0,2) from (0,0) and then go to (0,1) and then go to (0,3), this part makes the robot
		navigate this. Also allows the robot to only navigate the y-axis. Example: at the beginning,
		the robot is facing the x-axis but needs to navigate to (0,2). 
		 */
		//If newPoint's x-value is equal to currentPoint's x-value, do the following:
		else
		{	
			/*
			If oldPoint's y-value is greater than the currentPoint's y-value, then check to see
			if newPoint's y-value is greater than currentPoint's x-value. If so, then turn around
			180 degrees. Otherwise, stay put and follow the next command.   
			 */
			if (oldPoint.y > currentPoint.y)
			{
				if (newPoint.y > currentPoint.y)
				{
					myPilot.rotate(180);
				}
			}
			/*
			If oldPoint's y-value is less than currentPoint's y-value, then check to see
			if newPoint's y-value is less than currentPoint's y-value. If so, then turn around
			180 degrees. Otherwise, stay put and follow the next command.   
			 */
			else if (oldPoint.y < currentPoint.y)
			{
				if (newPoint.y < currentPoint.y)
				{
					myPilot.rotate(180);
				}
			}
			/*
			Else oldPoint's y-value = currentPoint's y-value. In that case, execute the following:
			 */
			else
				/*
				If oldPoint's x-value is greater than currentPoint's x-value, then check to see
				if newPoint's y-value is greater than currentPoint's y-value. If so, then turn 
				right. Otherwise, if newPoint's y-value is less than currentPoint's y-value,
				turn left.
				 */
				if (oldPoint.x>currentPoint.x)
				{
					if (newPoint.y > currentPoint.y)
					{
						myPilot.rotate(90);
					}
					else if (newPoint.y < currentPoint.y)
					{
						myPilot.rotate(-90);
					}
				}
				/*
				If oldPoint's x-value is less than currentPoint's x-value, then check to see
				if newPoint's y-value is greater than currentPoint's y-value. If so, then turn 
				left. Otherwise, if newPoint's y-value is less than currentPoint's y-value,
				turn right.
				 */
				else if (oldPoint.x<currentPoint.x)
				{
					if (newPoint.y > currentPoint.y)
					{
						myPilot.rotate(-90);
					}
					else if (newPoint.y < currentPoint.y)
					{
						myPilot.rotate(90);
					}
				}
		}
		
		/**
		 * PART TWO OF moveYdistance(). After knowing which direction to turn, then travel this distance
		 * along the y-axis using trackLine(). The robot uses the number of times it passes by black tape
		 * to determine its current coordinates; robot will stop when arrive at newPoint.
		 */
		//Initialize i, the number of times the robot passes by black tape.
		int i=0; 
		/*Initialize y_distance, number of times the robot has to pass the black tape before arriving at 
		  the new point's y-value (defined as absolute value of the difference between new point and
		  current point y-values. */
		int y_distance = Math.abs(newPoint.y - currentPoint.y);
		
		//while loop that keeps running until the robot passes the black tape an appropriate # of times
		while (i < y_distance)
		{
			int lval = tracker.lvalue(); //determine left sensor light value
  			int rval = tracker.rvalue(); //determine right sensor light value
  			tracker.trackLine(); //follow the blue tape		
  			//tells the robot what to do when it approaches the black tape
  			if (lval<0||rval<0)
  			{
  				Sound.beep(); //make a sound
  				i=i+1; //increase the "robot black tape approach count" by 1		
  				LCD.drawInt(currentPoint.y + i,12,0); //show y-value of where robot is right now
  				//if the robot reaches newPoint's y-value, do not rotate and break this loop.
  				if (i == y_distance)
  				{
  					tracker.rotate(0);
  					break;
  				}
  				//else the robot just goes past the black tape and continues its journey
  				else 
  				{
  					while (true)
  					{
  						lval = tracker.lvalue();
  						rval = tracker.rvalue();
  						myPilot.steer(0);
  						if (lval >= 0 && rval >= 0)
  						{
  							break;
  						}
  					}
  				}
  			}
		}
	}
	
	/**
	The robot has coordinates being inputed into it via ButtonCounter.java. The method 
	storePoints() stores these inputed points so these points can be run through moveXdistance
	and moveYdistance. The robot will also store the previous point such that if you want to run
	multiple points, you do not have to reset from the beginning to run another point.
	In short: it stores all the point values (it's where you draw oldPoint, currentPoint, newPoint). 
	 */
	void StorePoints()
	{
		//Set & define all the integer values of the points inputed.
		int oldX = oldPoint.x;
		int oldY = oldPoint.y;
		int currentX = currentPoint.x;
		int currentY = currentPoint.y;
		int newX = newPoint.x;
		int newY = newPoint.y;
		/*
		When the robot is finished running, set the location of currentPoint to oldPoint,
		and move newPoint's location to currentPoint. The user will then input a new newPoint. 
		 */
		oldPoint.setLocation(currentX, currentY);
		currentPoint.setLocation(newX, newY);
		//Clear screen
		LCD.clear();
		//Display oldPoint
		LCD.drawString("Old X", 9, 0);
		LCD.drawString("Old Y", 9, 1);
		LCD.drawInt(oldPoint.x, 11, 0);
		LCD.drawInt(oldPoint.y, 11, 1);
		//Display currentPoint
		LCD.drawString("Current X", 9, 3);
		LCD.drawString("Current Y", 9, 4);
		LCD.drawInt(currentPoint.x, 11, 3);
		LCD.drawInt(currentPoint.y, 11, 4);
		//Display newPoint
		LCD.drawString("New X", 9, 6);
		LCD.drawString("New Y", 9, 7);
		LCD.drawInt(newPoint.x, 11, 6);
		LCD.drawInt(newPoint.y, 11, 7);
		//Wait 5 seconds.
		Delay.msDelay(5000);
	}	

	/**
	 * The main method puts everything together. It takes the methods from Milestone3 as well as
	 * from ButtonCounter.java to navigate the robot.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		//Set instance variable myMS3 and ButtonCounter myButtCount to operate robot
		Milestone3 myMS3 = new Milestone3();
		ButtonCounter myButtCount = new ButtonCounter();		
		//Calibrate the robot
		myMS3.calibrate();
		//Display "loading" string and wait 1 second.
		LCD.drawString("Loading...", 0, 0);
		Delay.msDelay(1000);
		//Initialize currentPoint and oldPoint at (0,0) to start things off.
		myMS3.oldPoint.setLocation(0,0);
		myMS3.currentPoint.setLocation(0,0);
		
		//Run this loop until someone manually ends the program on the robot.
		while(true)
		{
			//Display "Let's Begin!"
			myButtCount.count("Let's Begin!");
			//Draw from ButtonCounter the coordinates of the desired/new location.
			myMS3.newPoint.x = myButtCount.getX();
			myMS3.newPoint.y = myButtCount.getY();
			//Display on screen strings "X=" and Y=" as labels.
			LCD.drawString("X=",10,0);
			LCD.drawString("Y=",10,1);
			//Display x and y values of newPoint, the desired location.
			LCD.drawInt(myMS3.newPoint.x,12,3);
			LCD.drawInt(myMS3.newPoint.y,12,4);	
			//Wait 1 second.
			Delay.msDelay(1000);
			
			myMS3.moveXdistance(); //Run moveXdistance()
			myMS3.moveYdistance(); //Run moveYdistance()
			myMS3.StorePoints(); //Run StorePoints()
		}
	}

}
