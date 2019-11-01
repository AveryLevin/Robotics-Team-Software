package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class autoModeSelector extends Thread implements Runnable {

	static int autonomousModeNumber = 0;
	String alliance = "init in progress";
	static boolean autoSelectError = false;

	int autoversion;
	boolean pressed=false;
	
	int allianceColor = 9;//
	int plusOne = 7;//p3.5
	int plusTwo = 6;//p3.6
	int plusFour = 8;//
	int plusEight = 5;//p4.1

	int AutonomousMode() {
		autoSelectError = false;
		SmartDashboard.putBoolean("Auto Selector Error", autoSelectError);
		int modeNumber = 0;

		if (Robot.autoSelector.getRawButton(allianceColor) == true) // Blue
		{															// Alliance
			modeNumber += 20;
		}
		if (Robot.autoSelector.getRawButton(allianceColor) == true) // Blue
		{															// Alliance
			alliance = "BLUE";
		}
		//we can probably combine those 2 if statements into 1
		else if (Robot.autoSelector.getRawButton(allianceColor) == false)//Red
		{																 //Alliance
			alliance = "RED";
		}
		if (Robot.autoSelector.getRawButton(plusEight)) {
			modeNumber += 8;
			SmartDashboard.putNumber("plus8:", 8);
		} else {
			SmartDashboard.putNumber("plus8:", 0);
		}

		if (Robot.autoSelector.getRawButton(plusFour)) {
			modeNumber += 4;
			SmartDashboard.putNumber("plus4:", 4);
		} else {
			SmartDashboard.putNumber("plus4:", 0);
		}

		if (Robot.autoSelector.getRawButton(plusTwo)) {
			modeNumber += 2;
			SmartDashboard.putNumber("plus2:", 2);
		} else {
			SmartDashboard.putNumber("plus2:", 0);
		}

		if (Robot.autoSelector.getRawButton(plusOne)) {
			modeNumber += 1;
			SmartDashboard.putNumber("plus1:", 1);
		} else {
			SmartDashboard.putNumber("plus1:", 0);
		}

		SmartDashboard.putString("Alliance", alliance);
		
		return modeNumber;
	}

	int AutonomousMode(Joystick j) {
		autoSelectError = false;
		SmartDashboard.putBoolean("Auto Selector Error", autoSelectError);
		if(!pressed&&j.getRawButton(8))
		{
			pressed=true;
			autoversion++;
			
			if(autoversion>35)
			{
				autoversion=0;
			}
		}
		else if(!pressed&&j.getRawButton(7))
		{
			pressed=true;
			autoversion--;
			
			if(autoversion<0)
			{
				autoversion=35;
			}
		}
		else if(!j.getRawButton(8)&&!j.getRawButton(7))
			pressed=false;
    	
    	return autoversion;
	}
	
	public void run() {
		try {
			while (true) {
				Timer.delay(0.1);
				/*
				if((AutonomousMode() == 0) ||(AutonomousMode() == 1) ||(AutonomousMode() == 3) ||(AutonomousMode() == 4) ||(AutonomousMode() == 15))
				{
					autonomousModeNumber = AutonomousMode();
				}
				else if((AutonomousMode() == 0) ||(AutonomousMode() == 21) ||(AutonomousMode() == 23) ||(AutonomousMode() == 24) ||(AutonomousMode() == 35))
				{
					autonomousModeNumber = AutonomousMode();
				}
				else
				{
					autonomousModeNumber = 0;
				}*/
				autonomousModeNumber = AutonomousMode(Robot.logitechJoystick);
				
				SmartDashboard.putNumber("Auto Mode", autonomousModeNumber);
			}
		} catch (Exception errorInAuto) {
			autoSelectError = true;
			SmartDashboard.putBoolean("Auto Selector Error", autoSelectError);
			SmartDashboard.putString("Auto Selector Error desc", errorInAuto.getMessage());
		}
	}

}
