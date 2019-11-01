package org.usfirst.frc.team4276.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team4276.robot.Robot;
import org.usfirst.frc.team4276.utilities.Xbox;
import org.usfirst.frc.team4276.utilities.SoftwareTimer;
import org.usfirst.frc.team4276.utilities.Toggler;

public class ArmPivoter extends Thread implements Runnable {

	private TalonSRX pivoter;
	private Toggler manualOverrideTogglerPivot;
	private SoftwareTimer armTimer;

	// Constants
	private double STATIC_GAIN = 0.37;// 0.41 max 0.34 min
	private double PROPORTIONAL_GAIN = 11600 * 1e-6;
	private double INTEGRAL_GAIN = 430 * 1e-6;
	private double DERIVATIVE_GAIN = 3110 * 1e-6;
	private final double STARTING_ANGLE = 90;
	private final double SETPOINT_INCREMENT = 5; // deg
	private final double MAX_POWER = 1;
	private final double UPPER_LIMIT = 85;
	private final double LOWER_LIMIT = -10;
	private final double DEGREES_PER_PULSE = 0.0004459828; // 0.0004459828
															// actual robot
	private final double ANGLE_THRESHOLD = 90; // deg
	private final double ANGLE_COAST_RATE = 90; // deg/s

	// General parameters
	private boolean delayInit = true;
	private boolean manualOverrideIsEngaged = false;
	private double encoderOffset = 0;
	private double estimatedAngle = 0; // deg
	private double commandedAngle = STARTING_ANGLE; // deg
	private double manualPower = 0;
	private double staticPower = 0;
	private double activePower = 0;
	private double commandedPower = 0;

	// PID parameters
	private boolean initializePID = true;
	private double angleError = 0; // deg
	private double angleErrorLast = 0; // deg
	private double accumulatedError = 0; // deg*s
	private double rateError = 0; // deg/s
	private double timeNow;
	private double timePrevious;
	private double timeStep;

	public ArmPivoter(int pivoterCANPort) {
		pivoter = new TalonSRX(pivoterCANPort);
		manualOverrideTogglerPivot = new Toggler(Xbox.Back);
		armTimer = new SoftwareTimer();
		encoderOffset = STARTING_ANGLE - pivoter.getSensorCollection().getQuadraturePosition() * DEGREES_PER_PULSE;
	}

	private void computeManualPower() {
		if (Math.abs(Robot.xboxController.getRawAxis(Xbox.LAxisY)) > 0.2) {
			manualPower = -Robot.xboxController.getRawAxis(Xbox.LAxisY) / 2;
		} else {
			manualPower = 0;
		}
	}

	private void computeStaticPower() {
		/**
		 * torque = m*(g+a)*xCM*cos(theta), where a=0 (for now)
		 */

		// double mass = 6.8; // placeholder
		// double gravity = 9.8;
		// double acceleration = 0; // placeholder
		// double xCM = .203; // placeholder
		// assignedPower = TORQUE_GAIN * (mass * (gravity + acceleration) * xCM
		// * Math.cos(theta));

		double theta = estimatedAngle * (Math.PI / 180); // rad
		staticPower = STATIC_GAIN * Math.cos(theta);
	}

	private void computeActivePower() {
		if (initializePID == true) {
			timeNow = Robot.systemTimer.get();
			estimatedAngle = pivoter.getSensorCollection().getQuadraturePosition() * DEGREES_PER_PULSE + encoderOffset;
			angleError = commandedAngle - estimatedAngle;
			accumulatedError = 0.0;
			activePower = 0.0;
			initializePID = false;
		} else {
			angleErrorLast = angleError;
			timePrevious = timeNow;
			timeNow = Robot.systemTimer.get();
			estimatedAngle = pivoter.getSensorCollection().getQuadraturePosition() * DEGREES_PER_PULSE + encoderOffset;
			timeStep = timeNow - timePrevious;

			// Compute control errors
			angleError = commandedAngle - estimatedAngle; // deg
			accumulatedError = accumulatedError + (angleErrorLast + angleError) / 2 * timeStep; // deg*s
			rateError = -pivoter.getSensorCollection().getQuadratureVelocity() * DEGREES_PER_PULSE * 10; // deg/s

			// For large height errors, follow coast speed until close to target
			if (angleError > ANGLE_THRESHOLD) {
				angleError = ANGLE_THRESHOLD; // limiting to 90 deg
				rateError = ANGLE_COAST_RATE + rateError; // coast speed = 90
															// deg/s
			}

			// Compute PID active power
			activePower = PROPORTIONAL_GAIN * angleError + INTEGRAL_GAIN * accumulatedError
					+ DERIVATIVE_GAIN * rateError;

		}
	}

	private void determineSetpoint() {
		// Determine commanded angle
		if (Robot.xboxController.getRawButton(Xbox.X)) {
			commandedAngle = 0;
		} else if (Robot.xboxController.getRawAxis(Xbox.LAxisY) < -0.15) {
			commandedAngle = commandedAngle + SETPOINT_INCREMENT;
		} else if (Robot.xboxController.getRawAxis(Xbox.LAxisY) > 0.15) {
			commandedAngle = commandedAngle - SETPOINT_INCREMENT;
		}

		// Limit commanded angle
		if (commandedAngle > UPPER_LIMIT) {
			commandedAngle = UPPER_LIMIT;
		} else if (commandedAngle < LOWER_LIMIT) {
			commandedAngle = LOWER_LIMIT;
		}
	}

	private void limitCommandedPower() {
		// Limit the range of commanded power
		if (commandedPower > MAX_POWER) {
			commandedPower = MAX_POWER;
		} else if (commandedPower < -MAX_POWER) {
			commandedPower = -MAX_POWER;
		}
	}

	private void tuneControlGains() {
		if (Robot.logitechJoystickL.getRawButton(5) == true) {
			STATIC_GAIN = STATIC_GAIN + 10e-3;
		}
		if (Robot.logitechJoystickL.getRawButton(3) == true) {
			STATIC_GAIN = STATIC_GAIN - 10e-3;
		}
		if (Robot.logitechJoystickL.getRawButton(7) == true) {
			PROPORTIONAL_GAIN = PROPORTIONAL_GAIN + 100e-6;
		}
		if (Robot.logitechJoystickL.getRawButton(8) == true) {
			PROPORTIONAL_GAIN = PROPORTIONAL_GAIN - 100e-6;
		}
		if (Robot.logitechJoystickL.getRawButton(9) == true) {
			INTEGRAL_GAIN = INTEGRAL_GAIN + 10e-6;
		}
		if (Robot.logitechJoystickL.getRawButton(10) == true) {
			INTEGRAL_GAIN = INTEGRAL_GAIN - 10e-6;
		}
		if (Robot.logitechJoystickL.getRawButton(11) == true) {
			DERIVATIVE_GAIN = DERIVATIVE_GAIN + 10e-6;
		}
		if (Robot.logitechJoystickL.getRawButton(12) == true) {
			DERIVATIVE_GAIN = DERIVATIVE_GAIN - 10e-6;
		}
	}

	private void updateTelemetry() {
		SmartDashboard.putNumber("current draw pivot", pivoter.getOutputCurrent());
		SmartDashboard.putNumber("Commanded Arm Angle", commandedAngle);
		SmartDashboard.putNumber("Estimated Arm Angle", estimatedAngle);
		SmartDashboard.putBoolean("Pivoter override", manualOverrideIsEngaged);

		SmartDashboard.putNumber("Pivoter Kstatic", STATIC_GAIN);
		SmartDashboard.putNumber("Pivoter Kp*1e-6", PROPORTIONAL_GAIN * 1e6);
		SmartDashboard.putNumber("Pivoter Ki*1e-6", INTEGRAL_GAIN * 1e6);
		SmartDashboard.putNumber("Pivoter Kd*1e-6", DERIVATIVE_GAIN * 1e6);
		SmartDashboard.putNumber("Pivoter Power", commandedPower);
	}

	public void commandSetpoint(double setpoint) {
		commandedAngle = setpoint;
	}

	public void initializeThread() {
		// commandedPower = 0;
		initializePID = true;
		accumulatedError = 0.0;
	}

	public void commandSetpoint(double setpoint, double delay) {
		if (delayInit) {
			armTimer.setTimer(0.1);
			delayInit = false;
		}
		if (armTimer.isExpired()) {
			commandedAngle = setpoint;
			delayInit = true;
		}
	}

	public void run() {
		while (true) {
			// tuneControlGains(); // for gain tuning only - COMMENT THIS LINE
			// OUT FOR
			// COMPETITION
			manualOverrideTogglerPivot.updateMechanismState();
			manualOverrideIsEngaged = manualOverrideTogglerPivot.getMechanismState();
			if (manualOverrideIsEngaged) {
				computeManualPower();
				computeStaticPower();
				computeActivePower();
				commandedPower = manualPower;
			} else {
				determineSetpoint();
				computeStaticPower();
				computeActivePower();
				commandedPower = staticPower + activePower;
			}
			limitCommandedPower();
			pivoter.set(ControlMode.PercentOutput, commandedPower);
			updateTelemetry();
			Timer.delay(0.0625);
		}
	}
}
