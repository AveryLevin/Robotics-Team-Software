package org.usfirst.frc.team4276.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4276.robot.Robot;
import org.usfirst.frc.team4276.utilities.Xbox;
import org.usfirst.frc.team4276.utilities.Toggler;

public class Elevator extends Thread implements Runnable {

	private TalonSRX elevatorDriverMainR1;
	private VictorSPX elevatorDriverR2;
	private VictorSPX elevatorDriverL1;
	private VictorSPX elevatorDriverL2;
	private Toggler manualOverrideToggler;

	// Constants - Lower Rail
	private double STATIC_GAIN_LOWER = 0;
	private double KP_LOWER = 490 * 1e-3;
	private double KI_LOWER = 11 * 1e-3;
	private double KD_LOWER = 21 * 1e-3;
	private final double MAX_HEIGHT_LOWER = 2.625; // ft

	// Constants - Upper Rail
	private double STATIC_GAIN_UPPER = STATIC_GAIN_LOWER;
	private double KP_UPPER = 490 * 1e-3;
	private double KI_UPPER = 11 * 1e-3;
	private double KD_UPPER = 21 * 1e-3;
	private final double MAX_HEIGHT_UPPER = 6.125; // ft

	// Constants - General
	public final double STARTING_HEIGHT = 0; // ft
	public final double SETPOINT_PREP = 2; // ft
	public final double SETPOINT_SCALE = 5.75; // ft
	public final double SETPOINT_SWITCH = 3; // ft
	public final double SETPOINT_BOTTOM = 0; // ft
	private final double SETPOINT_INCREMENT = .1; // ft
	private final double OVERRIDE_INCREMENT = 0.3; // 30%
	private final double HEIGHT_PER_PULSE = (-1.562 * 1e-4); // (1/6400)
	private final double MAX_POWER_UP = 0.7;
	private final double MAX_POWER_DOWN = 0.275;
	private final double HEIGHT_THRESHOLD = 2; // ft
	private final double HEIGHT_COAST_RATE = 1; // ft/s

	// General parameters
	private boolean manualOverrideIsEngaged;
	private boolean isClimbing = false;
	private double encoderOffset = 0;
	private double estimatedHeight = 0;
	public double commandedHeight = STARTING_HEIGHT;
	private double manualPower = 0;
	private double staticPower = 0;
	private double activePower = 0;
	private double commandedPower = 0;

	// PID parameters
	private boolean initializePID = true;
	private double heightError = 0;
	private double heightErrorLast = 0;
	private double accumulatedError = 0;
	private double rateError = 0;
	private double timeNow;
	private double timePrevious;
	private double timeStep;

	public Elevator(int elevator1CANPort, int elevator2CANPort, int elevator3CANPort, int elevator4CANPort) {
		elevatorDriverMainR1 = new TalonSRX(elevator1CANPort);
		elevatorDriverR2 = new VictorSPX(elevator2CANPort);
		elevatorDriverL1 = new VictorSPX(elevator3CANPort);
		elevatorDriverL2 = new VictorSPX(elevator4CANPort);
		manualOverrideToggler = new Toggler(Xbox.Start);

		// elevatorDriverFollow.set(ControlMode.Follower, elevator1CANPort);
		encoderOffset = STARTING_HEIGHT
				- elevatorDriverMainR1.getSensorCollection().getQuadraturePosition() * HEIGHT_PER_PULSE;
	}

	private void computeManualPowerOffset() {
		if (Robot.xboxController.getRawAxis(Xbox.RAxisY) < -0.15) {
			manualPower = OVERRIDE_INCREMENT;
		} else if (Robot.xboxController.getRawAxis(Xbox.RAxisY) > 0.15) {
			manualPower = -OVERRIDE_INCREMENT;
		} else {
			manualPower = 0;
		}
	}

	private void computeStaticPower() {
		if (estimatedHeight < MAX_HEIGHT_LOWER) {
			staticPower = STATIC_GAIN_LOWER;
		} else {
			staticPower = STATIC_GAIN_UPPER;
		}
	}

	private void determineSetpoint() {
		// button Y(Deposit Cube in Scale)
		if (Robot.xboxController.getRawButton(Xbox.Y)) {
			commandedHeight = SETPOINT_SCALE;
		}

		// button A(Deposit Cube in Switch)
		if (Robot.xboxController.getRawButton(Xbox.B)) {
			commandedHeight = SETPOINT_SWITCH;
		}

		// button B(Manipulator to Bottom)
		if (Robot.xboxController.getRawButton(Xbox.A)) {
			commandedHeight = SETPOINT_BOTTOM;
		}

		// Right Axis Y (Manually Change Setpoint)
		if (Robot.xboxController.getRawAxis(Xbox.RAxisY) < -0.15) {
			commandedHeight = commandedHeight + SETPOINT_INCREMENT;
		} else if (Robot.xboxController.getRawAxis(Xbox.RAxisY) > 0.15) {
			commandedHeight = commandedHeight - SETPOINT_INCREMENT;
		}

		// Limit commanded height range
		if (commandedHeight > MAX_HEIGHT_UPPER) {
			commandedHeight = MAX_HEIGHT_UPPER;
		} else if (commandedHeight < SETPOINT_BOTTOM) {
			commandedHeight = SETPOINT_BOTTOM;
		}
	}

	private void computeActivePower() {
		if (initializePID == true) {
			timeNow = Robot.systemTimer.get();
			estimatedHeight = elevatorDriverMainR1.getSensorCollection().getQuadraturePosition() * HEIGHT_PER_PULSE
					+ encoderOffset;
			heightError = commandedHeight - estimatedHeight;
			accumulatedError = 0.0;
			initializePID = false;
		} else {
			heightErrorLast = heightError;
			timePrevious = timeNow;
			timeNow = Robot.systemTimer.get();
			estimatedHeight = elevatorDriverMainR1.getSensorCollection().getQuadraturePosition() * HEIGHT_PER_PULSE
					+ encoderOffset;
			timeStep = timeNow - timePrevious;

			// Compute control errors
			heightError = commandedHeight - estimatedHeight; // height
			accumulatedError = accumulatedError + (heightErrorLast + heightError) / 2 * timeStep; // height*sec
			rateError = -elevatorDriverMainR1.getSensorCollection().getQuadratureVelocity() * 10 * HEIGHT_PER_PULSE; // height/sec

			// For large height errors, follow coast speed until close to target
			if (heightError > HEIGHT_THRESHOLD) {
				heightError = HEIGHT_THRESHOLD; // limiting to 2 ft
				rateError = HEIGHT_COAST_RATE + rateError; // coast speed = 1
															// ft/s
			}

			// Compute PID active power
			if (estimatedHeight < MAX_HEIGHT_LOWER) {
				// PID for lower rail
				activePower = (KP_LOWER * heightError) + (KI_LOWER * accumulatedError) + (KD_LOWER * rateError);
			} else {
				// PID for upper rail
				activePower = (KP_UPPER * heightError) + (KI_UPPER * accumulatedError) + (KD_UPPER * rateError);
			}

		}
	}

	private void limitCommandedPower() {
		// Limit the range of commanded power
		if (commandedPower > MAX_POWER_UP) {
			commandedPower = MAX_POWER_UP;
		} else if (commandedPower < -MAX_POWER_DOWN) {
			commandedPower = -MAX_POWER_DOWN;
		}
	}

	private void limitCommandedPower(double maxClimbPower) {
		// Limit the range of commanded power
		if (commandedPower > maxClimbPower) {
			commandedPower = maxClimbPower;
		} else if (commandedPower < -maxClimbPower) {
			commandedPower = -maxClimbPower;
		}
	}

	private void tuneControlGains() {
		if (Robot.logitechJoystickL.getRawButton(5) == true) {
			STATIC_GAIN_LOWER = STATIC_GAIN_LOWER + 10e-3;
		}
		if (Robot.logitechJoystickL.getRawButton(3) == true) {
			STATIC_GAIN_LOWER = STATIC_GAIN_LOWER - 10e-3;
		}
		if (Robot.logitechJoystickL.getRawButton(7) == true) {
			KP_LOWER = KP_LOWER + 10e-3;
		}
		if (Robot.logitechJoystickL.getRawButton(8) == true) {
			KP_LOWER = KP_LOWER - 10e-3;
		}
		if (Robot.logitechJoystickL.getRawButton(9) == true) {
			KI_LOWER = KI_LOWER + 1e-3;
		}
		if (Robot.logitechJoystickL.getRawButton(10) == true) {
			KI_LOWER = KI_LOWER - 1e-3;
		}
		if (Robot.logitechJoystickL.getRawButton(11) == true) {
			KD_LOWER = KD_LOWER + 1e-3;
		}
		if (Robot.logitechJoystickL.getRawButton(12) == true) {
			KD_LOWER = KD_LOWER - 1e-3;
		}
		SmartDashboard.putNumber("Elevator Lower Kstatic", STATIC_GAIN_LOWER);
		SmartDashboard.putNumber("Elevator Lower Kp*1e-3", KP_LOWER * 1e3);
		SmartDashboard.putNumber("Elevator Lower Ki*1e-3", KI_LOWER * 1e3);
		SmartDashboard.putNumber("Elevator Lower Kd*1e-3", KD_LOWER * 1e3);

		if (Robot.logitechJoystickR.getRawButton(5) == true) {
			STATIC_GAIN_UPPER = STATIC_GAIN_UPPER + 10e-3;
		}
		if (Robot.logitechJoystickR.getRawButton(3) == true) {
			STATIC_GAIN_UPPER = STATIC_GAIN_UPPER - 10e-3;
		}
		if (Robot.logitechJoystickR.getRawButton(7) == true) {
			KP_UPPER = KP_UPPER + 10e-3;
		}
		if (Robot.logitechJoystickR.getRawButton(8) == true) {
			KP_UPPER = KP_UPPER - 10e-3;
		}
		if (Robot.logitechJoystickR.getRawButton(9) == true) {
			KI_UPPER = KI_UPPER + 1e-3;
		}
		if (Robot.logitechJoystickR.getRawButton(10) == true) {
			KI_UPPER = KI_UPPER - 1e-3;
		}
		if (Robot.logitechJoystickR.getRawButton(11) == true) {
			KD_UPPER = KD_UPPER + 1e-3;
		}
		if (Robot.logitechJoystickR.getRawButton(12) == true) {
			KD_UPPER = KD_UPPER - 1e-3;
		}
		SmartDashboard.putNumber("Elevator Upper Kstatic", STATIC_GAIN_UPPER);
		SmartDashboard.putNumber("Elevator Upper Kp*1e-3", KP_UPPER * 1e3);
		SmartDashboard.putNumber("Elevator Upper Ki*1e-3", KI_UPPER * 1e3);
		SmartDashboard.putNumber("Elevator Upper Kd*1e-3", KD_UPPER * 1e3);

		SmartDashboard.putNumber("Elevator power", commandedPower);
	}

	private void updateTelemetry() {
		SmartDashboard.putNumber("current draw elevator 1", elevatorDriverMainR1.getOutputCurrent());
		SmartDashboard.putNumber("current draw elevator 2", elevatorDriverR2.getOutputCurrent());
		SmartDashboard.putNumber("Commanded arm height", commandedHeight);
		SmartDashboard.putNumber("Estimated arm height", estimatedHeight);
		SmartDashboard.putBoolean("Elevator override", manualOverrideIsEngaged);
		SmartDashboard.putBoolean("Climbing Mode", isClimbing);
		SmartDashboard.putNumber("Elevator power", commandedPower);
	}

	public void commandToBottom() {
		commandedHeight = SETPOINT_BOTTOM;
	}

	public void commandToSwitch() {
		commandedHeight = SETPOINT_SWITCH;
	}

	public void commandToScale() {
		commandedHeight = SETPOINT_SCALE;
	}

	public void initializeThread() {
		commandedPower = 0;
		initializePID = true;
		accumulatedError = 0.0;
	}

	public void run() {
		while (true) {
			// tuneControlGains(); // for gain tuning only - COMMENT THIS LINE
			// OUT
			// FOR COMPETITION
			manualOverrideToggler.updateMechanismState();
			manualOverrideIsEngaged = manualOverrideToggler.getMechanismState();
			if (manualOverrideIsEngaged) {
				computeManualPowerOffset();
				if (Robot.xboxController.getPOV(Xbox.DPad) == Xbox.POVdown) {
					isClimbing = true;
					commandedPower = -1;
				} else {
					isClimbing = false;
					commandedPower = staticPower + manualPower;
				}
				computeActivePower();
			} else {
				determineSetpoint();
				computeStaticPower();
				computeActivePower();
				isClimbing = false;
				commandedPower = staticPower + activePower;
			}
			if (!isClimbing) {
				limitCommandedPower();
			}
			elevatorDriverMainR1.set(ControlMode.PercentOutput, -commandedPower);
			elevatorDriverR2.set(ControlMode.PercentOutput, -commandedPower);
			// negative because facing opposite direction
			// TODO test all motor directions
			elevatorDriverL1.set(ControlMode.PercentOutput, commandedPower);
			elevatorDriverL2.set(ControlMode.PercentOutput, commandedPower);
			updateTelemetry();
			Timer.delay(0.0625);
		}
	}
}
