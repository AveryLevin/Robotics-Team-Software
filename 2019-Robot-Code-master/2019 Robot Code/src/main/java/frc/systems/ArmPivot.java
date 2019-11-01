package frc.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;

import frc.robot.Robot;
import frc.utilities.Xbox;
import frc.utilities.SoftwareTimer;
import frc.utilities.Toggler;

public class ArmPivot extends Thread implements Runnable {

	private VictorSPX pivoter1;
	private DigitalInput calibrateSwitch;
	private Toggler manualOverrideTogglerPivot;
	private SoftwareTimer armTimer;
	Encoder pivotEncoder;

	// Constants
	public final double CARGO_IN_SETPOINT = 0.0;
	public final double DOWN_SETPOINT = -90.0;

	public final double UP_SETPOINT = 20.0;

	private final double BACKDRIVE_POWER = 0.3;

	private double STATIC_GAIN = 0.0;// 0.41 max 0.34 min
	private double PROPORTIONAL_GAIN = 65800e-6;
	private double INTEGRAL_GAIN = 7600e-6;
	private double DERIVATIVE_GAIN = -3450e-6;
	private final double STARTING_ANGLE = DOWN_SETPOINT;
	private final double SETPOINT_INCREMENT = 5; // deg
	private final double MAX_POWER = 1.0;
	private final double UPPER_LIMIT = 20;
	private final double LOWER_LIMIT = -90;
	private final double DEGREES_PER_PULSE = (-0.2036244521);
	private final double ANGLE_THRESHOLD = 90; // deg
	private final double ANGLE_COAST_RATE = 90; // deg/s

	// General parameters
	private boolean delayInit = true;
	private boolean manualOverrideIsEngaged = false;
	private boolean isBackDriven = false;
	private double encoderOffset = 0;
	private double estimatedAngle = 90; // deg
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

	public ArmPivot(int pivoterCANPort1, int encA, int encB, int limSwitchDIO) {
		pivoter1 = new VictorSPX(pivoterCANPort1);
		calibrateSwitch = new DigitalInput(limSwitchDIO);

		manualOverrideTogglerPivot = new Toggler(Xbox.Back);
		armTimer = new SoftwareTimer();
		pivotEncoder = new Encoder(encA, encB);
		pivotEncoder.setDistancePerPulse(DEGREES_PER_PULSE);
		pivotEncoder.reset();
		encoderOffset = STARTING_ANGLE - pivotEncoder.getDistance();
	}

	private void computeManualPower() {
		if (Math.abs(Robot.xboxJoystick.getRawAxis(Xbox.LAxisY)) > 0.2) {
			manualPower = -Robot.xboxJoystick.getRawAxis(Xbox.LAxisY) ;
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
		determineSetpoint();
		if (initializePID == true) {
			timeNow = Robot.systemTimer.get();
			estimatedAngle = pivotEncoder.getDistance() + encoderOffset;
			angleError = commandedAngle - estimatedAngle;
			accumulatedError = 0.0;
			activePower = 0.0;
			initializePID = false;
		} else {
			angleErrorLast = angleError;
			timePrevious = timeNow;
			timeNow = Robot.systemTimer.get();
			estimatedAngle = pivotEncoder.getDistance() + encoderOffset;
			timeStep = timeNow - timePrevious;

			// Compute control errors
			angleError = commandedAngle - estimatedAngle; // deg
			accumulatedError = accumulatedError + (angleErrorLast + angleError) / 2 * timeStep; // deg*s
			rateError = -pivotEncoder.getDistance(); // deg/s

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
		if (Robot.xboxJoystick.getRawButton(Xbox.LB)) {
			commandedAngle = CARGO_IN_SETPOINT;
		} else if (Robot.xboxJoystick.getRawButton(Xbox.RB)) {
			commandedAngle = UP_SETPOINT;
		} else if (Robot.xboxJoystick.getRawAxis(Xbox.LT) > 0.5) {
			commandedAngle = DOWN_SETPOINT;
		} else if (Robot.xboxJoystick.getRawAxis(Xbox.LAxisY) < -0.15) {
			commandedAngle = commandedAngle + SETPOINT_INCREMENT;
		} else if (Robot.xboxJoystick.getRawAxis(Xbox.LAxisY) > 0.15) {
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

	private void calibrate() {
		commandedAngle = UP_SETPOINT;
		encoderOffset = UP_SETPOINT - pivotEncoder.getDistance();

	}

	private void tuneControlGains() {
		if (Robot.leftJoystick.getRawButton(5) == true) {
			STATIC_GAIN = STATIC_GAIN + 10e-3;
		}
		if (Robot.leftJoystick.getRawButton(3) == true) {
			STATIC_GAIN = STATIC_GAIN - 10e-3;
		}
		if (Robot.leftJoystick.getRawButton(7) == true) {
			PROPORTIONAL_GAIN = PROPORTIONAL_GAIN + 100e-6;
		}
		if (Robot.leftJoystick.getRawButton(8) == true) {
			PROPORTIONAL_GAIN = PROPORTIONAL_GAIN - 100e-6;
		}
		if (Robot.leftJoystick.getRawButton(9) == true) {
			INTEGRAL_GAIN = INTEGRAL_GAIN + 10e-6;
		}
		if (Robot.leftJoystick.getRawButton(10) == true) {
			INTEGRAL_GAIN = INTEGRAL_GAIN - 10e-6;
		}
		if (Robot.leftJoystick.getRawButton(11) == true) {
			DERIVATIVE_GAIN = DERIVATIVE_GAIN + 10e-6;
		}
		if (Robot.leftJoystick.getRawButton(12) == true) {
			DERIVATIVE_GAIN = DERIVATIVE_GAIN - 10e-6;
		}
	}

	public void updateTelemetry() {
		SmartDashboard.putNumber("Commanded Arm Angle", commandedAngle);
		SmartDashboard.putNumber("Estimated Arm Angle", estimatedAngle);
		SmartDashboard.putBoolean("Pivoter override", manualOverrideIsEngaged);
		SmartDashboard.putBoolean("Pivoter BackDrive", isBackDriven);

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

	public boolean backDrive(boolean state) {
		if (state) {

			if (estimatedAngle > 85.0) {
				isBackDriven = true;
				return true;
			} else {
				isBackDriven = false;
				return false;
			}
		} else {
			isBackDriven = false;
			return true;
		}
	}

	public void performMainProcessing() {
		tuneControlGains(); // for gain tuning only - COMMENT THIS LINE
		// OUT FOR
		// COMPETITION
		// if (calibrateSwitch.get()) {
		// calibrate();
		// }

		computeManualPower();
		computeStaticPower();
		computeActivePower();

		manualOverrideIsEngaged = manualOverrideTogglerPivot.getMechanismState();

		manualOverrideTogglerPivot.updateMechanismState();
		if (manualOverrideIsEngaged) {
			commandedPower = manualPower;
		} else {
			commandedPower = staticPower + activePower;
		}
		limitCommandedPower();
		pivoter1.set(ControlMode.PercentOutput, -commandedPower);
		updateTelemetry();
	}
}
