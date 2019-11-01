package org.usfirst.frc.team4276.systems;

import org.usfirst.frc.team4276.autonomous.PositionFinder;
import org.usfirst.frc.team4276.robot.Robot;
import org.usfirst.frc.team4276.utilities.SoftwareTimer;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {

	Joystick leftJoystick, rightJoystick;
	DoubleSolenoid gearShifter;

	VictorSPX leftMotor;
	VictorSPX rightMotor;
	VictorSPX leftMotorFollower1;
	VictorSPX rightMotorFollower1;
	VictorSPX leftMotorFollower2;
	VictorSPX rightMotorFollower2;

	SoftwareTimer shiftTimer;

	private boolean shiftInit = true;

	public double leftDrivePower = 0;
	public double rightDrivePower = 0;
	public int highShifter = 4;
	public int lowShifter = 3;
	public final double SHIFT_TIME = 0.05; // sec
	final Value HI_GEAR_VALUE = DoubleSolenoid.Value.kForward;
	final Value LO_GEAR_VALUE = DoubleSolenoid.Value.kReverse;

	public boolean driveInit = true;

	private double accumulatedError;
	private double errorLast = 0;

	private double timeNow;
	private double timePrevious;
	private double timeStep;

	public String driveMode = "init";

	public DriveTrain(int shifterAPort, int shifterBPort, int leftCANPort, int rightCANPort, int leftCANPort1,
			int rightCANPort1, int leftCANPort2, int rightCANPort2) {

		leftMotor = new VictorSPX(leftCANPort);
		rightMotor = new VictorSPX(rightCANPort);
		leftMotorFollower1 = new VictorSPX(leftCANPort1);
		rightMotorFollower1 = new VictorSPX(rightCANPort1);
		leftMotorFollower2 = new VictorSPX(leftCANPort2);
		rightMotorFollower2 = new VictorSPX(rightCANPort2);

		leftMotorFollower1.set(ControlMode.Follower, leftCANPort);
		rightMotorFollower1.set(ControlMode.Follower, rightCANPort);
		leftMotorFollower2.set(ControlMode.Follower, leftCANPort);
		rightMotorFollower2.set(ControlMode.Follower, rightCANPort);

		gearShifter = new DoubleSolenoid(shifterAPort, shifterBPort);

		shiftTimer = new SoftwareTimer();

	}

	public void getJoystickValues() {
		final double DRIVE_PROFILE_EXPONENT = 3 / 2; // must be > 1
		final double JOYSTICK_DEADBAND = 0.2; // must be positive

		if (Robot.logitechJoystickL.getY() > JOYSTICK_DEADBAND) {
			leftDrivePower = Math.pow(Robot.logitechJoystickL.getY(), DRIVE_PROFILE_EXPONENT);
		} else if (Robot.logitechJoystickL.getY() < -JOYSTICK_DEADBAND) {
			leftDrivePower = -1 * Math.abs(Math.pow(Robot.logitechJoystickL.getY(), DRIVE_PROFILE_EXPONENT));
		} else {
			leftDrivePower = 0;
		}

		if (Robot.logitechJoystickR.getY() > JOYSTICK_DEADBAND) {
			rightDrivePower = Math.pow(Robot.logitechJoystickR.getY(), DRIVE_PROFILE_EXPONENT);
		} else if (Robot.logitechJoystickR.getY() < -JOYSTICK_DEADBAND) {
			rightDrivePower = -1 * Math.abs(Math.pow(Robot.logitechJoystickR.getY(), DRIVE_PROFILE_EXPONENT));
		} else {
			rightDrivePower = 0;
		}
	}

	public void checkForGearShift() {
		boolean shiftHi = Robot.logitechJoystickR.getRawButton(highShifter);
		boolean shiftLo = Robot.logitechJoystickR.getRawButton(lowShifter);

		if (shiftHi) {
			if (shiftInit) {
				shiftTimer.setTimer(SHIFT_TIME);
				shiftInit = false;
			}
			if (shiftTimer.isExpired()) {
				setMotorSpeeds();
				shiftInit = true;
			} else {
				setMotorSpeeds(0);
			}
			gearShifter.set(HI_GEAR_VALUE);
		} else if (shiftLo) {
			if (shiftInit) {
				shiftTimer.setTimer(SHIFT_TIME);
				shiftInit = false;
			}
			if (shiftTimer.isExpired()) {
				setMotorSpeeds();
				shiftInit = true;
			} else {
				setMotorSpeeds(0);
			}
			gearShifter.set(LO_GEAR_VALUE);
		} else {
			setMotorSpeeds();
		}

	}

	public void setMotorSpeeds() {
		leftMotor.set(ControlMode.PercentOutput, leftDrivePower);
		rightMotor.set(ControlMode.PercentOutput, (-1 * rightDrivePower));
	}

	public void setMotorSpeeds(double speed) {
		leftMotor.set(ControlMode.PercentOutput, speed);
		rightMotor.set(ControlMode.PercentOutput, -speed);
	}

	public boolean rotateToHeading(double desiredHeading) {
		if (driveInit == true) {
			accumulatedError = 0;
			errorLast = 0;
			driveInit = false;
		}
		boolean status = false;
		double timeStep = Robot.systemTimer.get();
		double currentHeading = PositionFinder.getHeadingDeg();
		double headingErrorCurrent = desiredHeading - currentHeading;
		accumulatedError = accumulatedError + (headingErrorCurrent + errorLast) * timeStep; // calculates
																							// integral
																							// of
																							// heading

		SmartDashboard.putNumber("Heading Error", headingErrorCurrent);
		double errorRate = (headingErrorCurrent - errorLast) / timeStep; // integral

		final double PROPORTIONAL_GAIN = .007;
		final double INTEGRAL_GAIN = 0.000007;
		final double POSITION_DEADBAND = 2; // degrees
		final double RATE_DEADBAND = 10; // degrees per second

		leftDrivePower = PROPORTIONAL_GAIN * headingErrorCurrent + INTEGRAL_GAIN * accumulatedError;
		rightDrivePower = -1 * (PROPORTIONAL_GAIN * headingErrorCurrent + INTEGRAL_GAIN * accumulatedError);

		if (Math.abs(headingErrorCurrent) < POSITION_DEADBAND /*
																 * && Math.abs( errorRate) < RATE_DEADBAND
																 */) {
			status = true;
			leftDrivePower = 0;
			rightDrivePower = 0;
		}
		setMotorSpeeds();
		driveMode = "Rotating to: " + desiredHeading;
		return status;
	}

	public void resetDrive() {
		setMotorSpeeds(0);
		driveInit = true;
	}

	public boolean rotateToCoordinate(double[] desiredCoordinateFacing) {
		if (driveInit == true) {
			accumulatedError = 0;
			errorLast = 0;
			timeNow = Robot.systemTimer.get();
			driveInit = false;
		}
		double desiredHeading = Math
				.toDegrees(Math.atan2(desiredCoordinateFacing[1] - PositionFinder.getCurrentLocation()[1],
						desiredCoordinateFacing[0] - PositionFinder.getCurrentLocation()[0]));
		// calculates heading needed to face coordinates based on inputed array

		boolean status = false;
		// return status of method (true when has reached target)

		timePrevious = timeNow;
		timeNow = Robot.systemTimer.get();
		double currentHeading = PositionFinder.getHeadingDeg();
		timeStep = timeNow - timePrevious;
		double headingErrorCurrent = desiredHeading - currentHeading;
		accumulatedError = accumulatedError + (headingErrorCurrent + errorLast) * timeStep;
		// calculates integral of heading error

		final double PROPORTIONAL_GAIN = 0.0027;
		final double INTEGRAL_GAIN = 0.0006;
		final double POSITION_DEADBAND = 2; // degrees

		leftDrivePower = PROPORTIONAL_GAIN * headingErrorCurrent + INTEGRAL_GAIN * accumulatedError;
		rightDrivePower = -1 * (PROPORTIONAL_GAIN * headingErrorCurrent + INTEGRAL_GAIN * accumulatedError);

		if (Math.abs(headingErrorCurrent) < POSITION_DEADBAND) {
			status = true;
			leftDrivePower = 0;
			rightDrivePower = 0;
		}
		setMotorSpeeds();
		driveMode = "Pointing to: " + desiredCoordinateFacing[0] + "," + desiredCoordinateFacing[1];
		return status;
	}

	public boolean driveToCoordinate(double[] desiredCoordinate) {
		if (driveInit == true) {
			accumulatedError = 0;
			errorLast = 0;
			timeNow = Robot.systemTimer.get();
			driveInit = false;
		}
		double desiredHeading = Math
				.toDegrees(Math.atan2((desiredCoordinate[1] - PositionFinder.getCurrentLocation()[1]),
						(desiredCoordinate[0] - PositionFinder.getCurrentLocation()[0])));
		// calculates heading needed to face coordinates based on inputed array

		boolean status = false;
		// return status of method (true when has reached target)

		timePrevious = timeNow;
		timeNow = Robot.systemTimer.get();
		double errorCurrent = Math.sqrt(Math.pow(desiredCoordinate[0] - PositionFinder.currentXY[0], 2)
				+ Math.pow(desiredCoordinate[1] - PositionFinder.currentXY[1], 2));
		timeStep = timeNow - timePrevious;

		double currentHeading = PositionFinder.getHeadingDeg();
		double headingError = desiredHeading - currentHeading;
		accumulatedError = accumulatedError + (errorCurrent + errorLast) * timeStep;
		// calculates integral of heading error

		SmartDashboard.putNumber("Heading Error", headingError);

		SmartDashboard.putNumber("Distance Error", errorCurrent);

		final double MAX_POWER = 0.5;
		final double LINEAR_PROPORTIONAL_GAIN = .09;// 1/feet
		final double LINEAR_INTEGRAL_GAIN = 0.009;
		double ANGULAR_PROPORTIONAL_GAIN = .008; // 1/degree
		final double LINEAR_ANGLE_DEADBAND = 0.5;// feet
		final double LINEAR_DEADBAND = 0.5; // feet

		boolean facingTarget = (Math.abs(headingError) < 90);

		if (Math.abs(errorCurrent) < LINEAR_ANGLE_DEADBAND) {
			ANGULAR_PROPORTIONAL_GAIN = 0;
		}

		if (facingTarget) {
			leftDrivePower = -1 * (LINEAR_PROPORTIONAL_GAIN * errorCurrent + LINEAR_INTEGRAL_GAIN * accumulatedError
					- ANGULAR_PROPORTIONAL_GAIN * headingError);
			rightDrivePower = -1 * (LINEAR_PROPORTIONAL_GAIN * errorCurrent + LINEAR_INTEGRAL_GAIN * accumulatedError
					+ ANGULAR_PROPORTIONAL_GAIN * headingError);
			SmartDashboard.putString("Direction", "facing");
		} else {
			leftDrivePower = LINEAR_PROPORTIONAL_GAIN * errorCurrent + LINEAR_INTEGRAL_GAIN * accumulatedError
					+ ANGULAR_PROPORTIONAL_GAIN * headingError;
			rightDrivePower = LINEAR_PROPORTIONAL_GAIN * errorCurrent + LINEAR_INTEGRAL_GAIN * accumulatedError
					- ANGULAR_PROPORTIONAL_GAIN * headingError;

			SmartDashboard.putString("Direction", "away");
		}

		if (Math.abs(errorCurrent) < LINEAR_DEADBAND) {
			status = true;
			leftDrivePower = 0;
			rightDrivePower = 0;
		}

		if (leftDrivePower > MAX_POWER)
			leftDrivePower = MAX_POWER;

		if (rightDrivePower > MAX_POWER)
			rightDrivePower = MAX_POWER;

		if (leftDrivePower < -MAX_POWER)
			leftDrivePower = -MAX_POWER;

		if (rightDrivePower < -MAX_POWER)
			rightDrivePower = -MAX_POWER;

		setMotorSpeeds();
		driveMode = "Driving to: " + desiredCoordinate[0] + "," + desiredCoordinate[1];
		return status;
	}

	public void goReverse(boolean isReversing) {

		if (isReversing) {
			leftDrivePower = .25;
			rightDrivePower = .25;
		} else {
			leftDrivePower = 0;
			rightDrivePower = 0;
		}
		setMotorSpeeds();
		driveMode = "Driving Back ";
	}

	public void goForward(boolean isForward) {

		if (isForward) {
			leftDrivePower = -.5;
			rightDrivePower = -.5;
		} else {
			leftDrivePower = 0;
			rightDrivePower = 0;
		}
		setMotorSpeeds();
		driveMode = "Driving Back ";
	}

	public void performMainProcessing() {
		getJoystickValues();
		checkForGearShift();
		driveMode = "Operator Control";
	}

	public void setHiGear() {
		gearShifter.set(HI_GEAR_VALUE);
	}

	public void setLoGear() {
		gearShifter.set(LO_GEAR_VALUE);
	}

	public void updateTelemetry() {
		Value gearMode = gearShifter.get();
		SmartDashboard.putBoolean("High Gear", gearMode == HI_GEAR_VALUE);
		SmartDashboard.putBoolean("Shifting In Progress", !shiftTimer.isExpired());
		SmartDashboard.putNumber("Left Drive Power", leftDrivePower);
		SmartDashboard.putNumber("Right Drive Power", rightDrivePower);
		SmartDashboard.putString("Drive Mode", driveMode);
	}
}
