package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class mecanumDrive {

	static RobotDrive mecanumControl;
	Joystick mecanumJoystick;
	VictorSP forwardRightMotor;
	VictorSP forwardLeftMotor;
	VictorSP backRightMotor;
	VictorSP backLeftMotor;
	boolean robotFrame;
	boolean fieldFrame;
	boolean Xtest;
	boolean Ytest;
	boolean Twisttest;
	static String driveStatus = "initiation";

	int mode = 0;

	public mecanumDrive(int pwm0, int pwm1, int pwm2, int pwm3) {

		mecanumJoystick = new Joystick(0);
		forwardRightMotor = new VictorSP(pwm0);
		forwardLeftMotor = new VictorSP(pwm1);
		backRightMotor = new VictorSP(pwm2);
		backLeftMotor = new VictorSP(pwm3);

		mecanumControl = new RobotDrive(forwardLeftMotor, backLeftMotor, forwardRightMotor, backRightMotor);
	}

	void robotFrameDrive() {

		driveStatus = "Driving In Robot Frame";

		double X = mecanumJoystick.getX();
		double Y = mecanumJoystick.getY();
		double Twist = mecanumJoystick.getTwist();
		double magnitude;
		double direction;
		double rotation;

		if (Math.abs(X) > .02 || Math.abs(Y) > .05)
			magnitude = Math.sqrt((X * X) + (Y * Y));
		else
			magnitude = 0;

		if (Math.abs(X) > .02 || Math.abs(Y) > .05)
			direction = (180 / Math.PI) * Math.atan2(Y, X);
		else
			direction = 0;

		if (Math.abs(Twist) > .05)
			rotation = Twist;
		else
			rotation = 0;

		mecanumControl.mecanumDrive_Polar(magnitude, direction, rotation);
	}

	void fieldFrameDrive() {
		driveStatus = "Driving In Robot Frame";

		double yaw = 0.0;
		double X = 0.0;
		double Y = 0.0;
		double Twist = 0.0;

		yaw = 0;

		if (Math.abs(mecanumJoystick.getX()) > .05)
			X = mecanumJoystick.getX();
		else
			X = 0;

		if (Math.abs(mecanumJoystick.getY()) > .05)
			Y = mecanumJoystick.getY();
		else
			Y = 0;
		if (Math.abs(mecanumJoystick.getTwist()) > .05)
			Twist = mecanumJoystick.getTwist();
		else
			Twist = 0;

		mecanumControl.mecanumDrive_Cartesian(X, Y, Twist, yaw);
		;
	}

	/*
	 * void XTest() { fieldFrame = false; robotFrame = false; Xtest = true;
	 * Ytest = false; Twisttest = false;
	 * 
	 * double X;
	 * 
	 * if(Math.abs(mecanumJoystick.getX())>.02) X = mecanumJoystick.getX(); else
	 * X= 0;
	 * 
	 * mecanumControl.mecanumDrive_Cartesian(X, 0, 0, 0); }
	 * 
	 * void YTest() { fieldFrame = false; robotFrame = false; Xtest = false;
	 * Ytest = true; Twisttest = false;
	 * 
	 * 
	 * double Y;
	 * 
	 * if(Math.abs(mecanumJoystick.getY())>.02) Y = mecanumJoystick.getY(); else
	 * Y= 0;
	 * 
	 * mecanumControl.mecanumDrive_Cartesian(0, Y, 0, 0); }
	 * 
	 * void TwistTest() { fieldFrame = false; robotFrame = false; Xtest = false;
	 * Ytest = false; Twisttest = true;
	 * 
	 * 
	 * double Twist;
	 * 
	 * if(Math.abs(mecanumJoystick.getX())>.02) Twist = mecanumJoystick.getX();
	 * else Twist= 0;
	 * 
	 * mecanumControl.mecanumDrive_Cartesian(0, 0, Twist, 0); }
	 */
	void Operatordrive() {
		fieldFrameDrive();
	}

	/*
	 * void driveTest() { if(mode < 3 && mecanumJoystick.getRawButton(5)) { mode
	 * ++; } else if(mode >= 3 && mecanumJoystick.getRawButton(5)) { mode = 1; }
	 * 
	 * if(mode == 1) { YTest(); } if(mode == 2) { XTest(); } if(mode == 3) {
	 * TwistTest(); } }
	 */
	static void modeReadout() {
		SmartDashboard.putString("Drive Status:", driveStatus);
	}

	static boolean driveToCoordinate(double Xgoal, double Ygoal, double RotationGoal) {

		driveStatus = "Driving to " + Xgoal + ", " + Ygoal + " with a desired rotation of" + RotationGoal;

		boolean Xacheived = false; // default
		boolean Yacheived = false; // default
		boolean rotationAcheived = false; // default

		double linearDeadband = .05;// feet
		double rotationDeadband = 5;// degrees

		double yaw = Robot.imu.getYaw();
		double Xdiff = Xgoal - mecanumNavigation.currentFieldX;
		double Ydiff = Ygoal - mecanumNavigation.currentFieldY;
		double RotationDiff = RotationGoal - yaw;

		double XpowerConstant = .2; // place holder
		double YpowerConstant = .2; // place holder
		double rotationPowerConstant = .2; // place holder

		double Xpower = Xdiff * XpowerConstant;
		if (Xpower > .75) {
			Xpower = .75;
		} else if (Xpower < -.75) {
			Xpower = -.75;
		}
		// This if statement prevent the power in the X direction of the field
		// from being too high

		if (Math.abs(Xdiff) < linearDeadband) {
			Xpower = 0;
			Xacheived = true;
		}
		/*
		 * if the difference between the robot's X position on the field and the
		 * goal is less than the deadband, so if the robot is getting close then
		 * the power in the X direction is set to 0, so that the robot stops
		 * moving in the X direction of the field
		 */

		double Ypower = Ydiff * YpowerConstant;
		if (Ypower > .75) {
			Ypower = .75;
		} else if (Ypower < -.75) {
			Ypower = -.75;
		}
		// This if statement prevent the power in the Y direction of the field
		// from being too high

		if (Math.abs(Ydiff) < linearDeadband) {
			Ypower = 0;
			Yacheived = true;
		}
		/*
		 * if the difference between the robot's Y position on the field and the
		 * goal is less than the deadband, so if the robot is getting close then
		 * the power in the Y direction is set to 0, so that the robot stops
		 * moving in the Y direction of the field
		 */

		double rotationPower = RotationDiff * rotationPowerConstant;
		if (rotationPower > .75) {
			rotationPower = .75;
		} else if (rotationPower < -.75) {
			rotationPower = -.75;
		}
		/*
		 * This if statement prevent the rotational power from being too high So
		 * that the robot won't rotate too fast
		 */

		if (Math.abs(RotationDiff) < rotationDeadband) {
			rotationPower = 0;
			rotationAcheived = true;
		}
		/*
		 * if the difference in rotation is less than the deadband, so if the
		 * robot has less of an angle to rotate to get to its desired angle then
		 * the rotational power is less than 0, so that the robot stops rotating
		 */

		mecanumControl.mecanumDrive_Cartesian(rotationPower, Ypower, rotationPower, yaw);

		if (Xacheived == true && Yacheived == true && rotationAcheived == true) {
			return true;

		} else {
			return false;
		}
	}

	static boolean rotateToHeading(double RotationGoal) {

		driveStatus = "Rotating to " + RotationGoal;

		boolean value = false;
		double yaw = Robot.imu.getYaw();
		double rotationDeadband = 5;// degrees, place holder
		double RotationDiff = RotationGoal - yaw;
		double rotationConstant = 0.2;// place holder
		double rotationPower = rotationConstant * RotationDiff;

		if (Math.abs(rotationPower) > 0.75) {
			rotationPower = 0.75;
		}
		// this if statement needs to be fixed later
		/*
		 * This if statement prevent the rotational power from being too high So
		 * that the robot won't rotate too fast
		 */
		
		if (Math.abs(RotationDiff) < rotationDeadband) {
			rotationPower = 0;
			value = true;
		} else {
			value = false;
		}
		/*
		 * if the difference in rotation is less than the deadband, so if the
		 * robot has less of an angle to rotate to get to its desired angle then
		 * the rotational power is less than 0, so that the robot stops rotating
		 */
		mecanumControl.mecanumDrive_Cartesian(0, 0, 0, rotationPower);
		return value;
	}

	static boolean visionBoilerAlignment(double boilerAngleOffset) // boiler to the right of center = + boiler to the left of center = -
	{

		driveStatus = "Rotating to align with Boiler";

		boolean value = false;
		double rotationConstant = 0.2; // place holder
		double rotationPower = 0; // default
		double rotationDeadband = 10; // pixels
		if (Math.abs(boilerAngleOffset) > rotationDeadband) {
			rotationPower = boilerAngleOffset * rotationConstant;
			value = false;
		} else {
			rotationPower = 0;
			value = true;
		}
		mecanumControl.mecanumDrive_Cartesian(0, 0, 0, rotationPower);
		return value;
	}

	// edit later (rotation for boiler in auto)
	static boolean gearAlign(double targetXOffset) {

		driveStatus = "Aligning with gear lift";

		double deadband = 5; // pixels
		double k = .02;// power applied for every pixel off from center
		double power = k * targetXOffset;

		if (Math.abs(targetXOffset) > deadband) {
			mecanumControl.mecanumDrive_Cartesian(power, 0, 0, 0);
			return false;
		} else {
			return true;
		}
	}

}
