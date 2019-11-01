package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArmPID extends Thread implements Runnable {

	boolean armError;

	final double raisedSetPoint = -5.0;
	final double middleSetPoint = -45.0;
	final double collectingSetPoint = -90.0;
	double estimatedArmAngle;
	double ang;
	final double armJogSpeed = 7.5;

	final double kUp = .01;
	final double kDown = .02;
	double deadband = 1;
	// final double CHAIN_SLACK_ANGLE = 8.0; // degrees
	static double initialArmAngle = 0; // degrees
	static double commandedArmAngle = initialArmAngle;
	// final double TARGETING_ERROR = 0.0; // degrees

	final double upperLimit = 1;
	final double lowerLimit = -105.0;

	public void run() {
		double errorProportional = 0;

		double power;

		//try {
			armError = false;

			while (true) {

				if (Robot.XBoxController.getRawButton(XBox.Start)) {
					gearCollection.armMotor.set(Robot.XBoxController.getRawAxis(XBox.LStickY));
				} else {
					double encoderAngle = gearCollection.armAngle.getDistance();
					estimatedArmAngle = initialArmAngle - encoderAngle;

					errorProportional = commandedArmAngle - estimatedArmAngle;

					if (errorProportional < -deadband) {
						power = kDown * errorProportional;
					} else if (errorProportional > deadband) {
						power = kUp * errorProportional;
					} else {
						power = 0;
					}

					gearCollection.armMotor.set(-power);

					if (Robot.XBoxController.getRawAxis(XBox.LStickY) > 0.5) {
						commandedArmAngle -= armJogSpeed;
					} else if (Robot.XBoxController.getRawAxis(XBox.LStickY) < -0.5) {
						commandedArmAngle += armJogSpeed;
					}

					if (Robot.XBoxController.getRawButton(XBox.Back) && Robot.XBoxController.getRawButton(XBox.Start)) {
						initialArmAngle++;
					} else if (Robot.XBoxController.getRawButton(XBox.Back)) {
						initialArmAngle--;
					}

					/*
					 * if (setpoint >= 0 + CHAIN_SLACK_ANGLE + TARGETING_ERROR)
					 * setpoint = 0 + CHAIN_SLACK_ANGLE + TARGETING_ERROR; if
					 * (setpoint <= -90 - TARGETING_ERROR) setpoint = -90 -
					 * TARGETING_ERROR; if
					 * (Robot.XBoxController.getRawButton(XBox.Y)) setpoint = 0
					 * + CHAIN_SLACK_ANGLE + TARGETING_ERROR; if
					 * (Robot.XBoxController.getRawButton(XBox.X)) setpoint =
					 * -90 - TARGETING_ERROR;
					 */

					if (Robot.XBoxController.getRawButton(JoystickMappings.gearArmUp)) {
						commandedArmAngle = raisedSetPoint;
					} else if (Robot.XBoxController.getRawButton(JoystickMappings.gearArmDown)) {
						commandedArmAngle = collectingSetPoint;
					} else if (Robot.XBoxController.getRawButton(JoystickMappings.gearArmMiddle)) {
						commandedArmAngle = middleSetPoint;
					}
					
					
					if (commandedArmAngle >= upperLimit) {
						commandedArmAngle = upperLimit;
					} else if (commandedArmAngle <= lowerLimit) {
						commandedArmAngle = lowerLimit;
					}
					
					
					if(!(gearCollection.gearLimitSwitch.get() == true))
					{
						
						gearCollection.gotGear = true;
						LEDi2cInterface.gearCollected = true;
					}
					else
					{
						
						gearCollection.gotGear = false;
						LEDi2cInterface.gearCollected = false;
					}

					SmartDashboard.putNumber("Arm Offset: ", errorProportional);
					SmartDashboard.putNumber("Setpoint: ", commandedArmAngle);
					SmartDashboard.putNumber("Power: ", power);
					SmartDashboard.putNumber("Arm Angle: ", estimatedArmAngle);
					SmartDashboard.putNumber("Encoder Value: ", encoderAngle);
					SmartDashboard.putNumber("Arm Start Angle", initialArmAngle);
					SmartDashboard.putBoolean("WHERE'S MY DAMN GEAR?", gearCollection.gotGear);

					
					if(Robot.XBoxController.getRawButton(XBox.Back)){
						LEDi2cInterface.awesome=true;
					}else{
						LEDi2cInterface.awesome=false;
					}
					
				}

				SmartDashboard.putBoolean("Arm Error", armError);

				Timer.delay(0.05);
			}

		//}

		/*catch (Exception errorInArm) {

			armError = true;
			SmartDashboard.putBoolean("Arm Error", armError);
			SmartDashboard.putString("Arm Error desc", errorInArm.getMessage());

		}*/
	}

}