package org.usfirst.frc.team4276.robot;

public class RoboRioPorts {

	//PWMs
		//drive system
	static final int PWM_DRIVE_FR = 0; // drive - front left
	static final int PWM_DRIVE_FL = 1; // drive - front right
	static final int PWM_DRIVE_BR = 2; // drive - back right
	static final int PWM_DRIVE_BL = 3; // drive - back left
	
		//shooter
	static final int PWM_SHOOTER_FEEDER = 4;
	static final int PWM_SHOOTER_FLYWHEEL = 9;
	
		//climber
	static final int PWM_CLIMBER = 5;
	
		//gear arm
	static final int PWM_GEAR_ANGLE = 6;
	static final int PWM_GEAR_INTAKE = 7;
	
		//ball intake
	static final int PWM_BALL_INTAKE = 8;
	
	//DIOs
		//drive system
	static final int DIO_DRIVE_FL_A = 0;
	static final int DIO_DRIVE_FL_B = 1;
	static final int DIO_DRIVE_BL_A = 2;
	static final int DIO_DRIVE_BL_B = 3;
	
		//gear arm
	static final int DIO_GEAR_ANGLE_A = 4;
	static final int DIO_GEAR_ANGLE_B = 5;
	static final int DIO_GEAR_LIMIT = 7;
		
		//shooter
	static final int DIO_SHOOTER_FLYWHEEL_A = 0;
	static final int DIO_SHOOTER_FLYWHEEL_B = 1;
	
		//sonar
	static final int DIO_SONAR_A = 19;
	static final int DIO_SONAR_B = 20;
}
