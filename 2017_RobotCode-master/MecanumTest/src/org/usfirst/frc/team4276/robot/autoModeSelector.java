package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.Timer;

public class autoModeSelector extends Thread implements Runnable{

 static int autonomousModeNumber = 0;
	
 int allianceColor = 1;
 int plusOne = 2;
 int plusTwo = 3;
 int plusFour = 4;
 int plusEight = 8;
	
 int AutonomousMode()
	{
		int modeNumber = 0;
		
		if(Robot.autoSelector.getRawButton(allianceColor) == false) //Blue Alliance
			modeNumber += 20;
		
		if(Robot.autoSelector.getRawButton(plusEight))
			modeNumber += 8;
		
		if(Robot.autoSelector.getRawButton(plusFour))
			modeNumber += 4;
		
		if(Robot.autoSelector.getRawButton(plusTwo))
			modeNumber += 2;
		
		if(Robot.autoSelector.getRawButton(plusOne))
			modeNumber += 1;
		
		return modeNumber;
	}
	
 	public void start()
 	{
 		while(true)
 		{
 			Timer.delay(1);
 			autonomousModeNumber = AutonomousMode();
 		}
 	}
 
}
