package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LidarSpin {

	// We may want to navigate radially away from the boiler and if we drive
	// directly away robot frame 180.0 will be right at the boiler so a small
	// change in course would require the scanner to change sides
	// We need a hard stop at this limit for initial calibration of encoder with
	// robot frame
	// (A limit switch would be better if we can find a DIO)
	private static final double LIDAR_SCAN_MIN_DEGREES = -134.9;
	private static final double LIDAR_SCAN_MAX_DEGREES = 224.9;

	private static final double LIDAR_FIXED_DEADZONE_DEGREES = 1.0;

	private double _minScanDegrees = LIDAR_SCAN_MIN_DEGREES;
	private double _maxScanDegrees = LIDAR_SCAN_MAX_DEGREES;

	// Destination angle for interrupt processing
	private double _desiredEncoderYaw = 0.0;

	private EncoderWithNotify _enc1;
	private Relay _spinner;

	public enum SpinMode {
		IDLE, SCAN, FIXED_OFFSET_FROM_YAW
	}

	public String spinModeToText(LidarSpin.SpinMode val) {
		switch (val) {
		case IDLE:
			return "IDLE";

		case SCAN:
			return "SCAN";

		case FIXED_OFFSET_FROM_YAW:
			return "FIXED_OFFSET_FROM_YAW";

		default:
			break;
		}
		return "???";
	}

	private SpinMode _spinMode = SpinMode.IDLE;

	public synchronized SpinMode spinMode() {
		return _spinMode;
	}

	public synchronized void setSpinMode(SpinMode val) {
		_spinMode = val;
	}

	// true = clockwise || false = counterclockwise
	private boolean _direction = true;

	public synchronized SpinMode direction() {
		return _spinMode;
	}

	public synchronized void setDirection(boolean val) {
		_direction = val;
	}

	// We need to calibrate the encoder to robot frame, but we don't have a
	// DIO to dedicate for a calibration limit switch.
	// We therefore rely on the vision system to find the boiler so we can
	// set the encoder to a known +90 or -90 relative to robot frame
	// depending on blue or red alliance.
	//
	// The underlying Encoder class only allows us to reset the encoder to zero,
	// So we need to keep track of the offset between zero on the encoder and
	// robot frame
	// and use it to provide a turntable angle in robot frame
	private double _yawOffsetTurntableToRobotFrame = 0.0;

	public synchronized void resetEncoderAtOffsetDegrees(double deg) {
		_enc1.reset();
		_yawOffsetTurntableToRobotFrame = deg;
	}

	// Current encoder angle expressed as an offset from straight ahead robot
	// frame
	public synchronized double encoderYawDegrees() {
		double retVal = _enc1.getDistance() + _minScanDegrees + _yawOffsetTurntableToRobotFrame;
		if (retVal > 180.0) {
			retVal -= 360.0;
		} else if (retVal < -180.0) {
			retVal += 360.0;
		}
		return retVal;
	}

	// Input from Vision System: Desired yaw offset from robot frame
	public synchronized void setDesiredEncoderYawDegrees(double val) {

		// Get robot frame encoder turntable yaw
		double myVal = encoderYawDegrees();

		// Add desired offset
		myVal += (val % 360.0);
		if (myVal > 180.0) {
			myVal -= 360.0;
		} else if (myVal < -180.0) {
			myVal += 360.0;
		}

		// Apply scan limit
		if (myVal < _minScanDegrees) {
			myVal = _minScanDegrees;
		}
		if (myVal > _maxScanDegrees) {
			myVal = _maxScanDegrees;
		}

		_desiredEncoderYaw = myVal;
		SmartDashboard.putNumber("desiredEncoderYaw", _desiredEncoderYaw);
	}

	public synchronized double desiredEncoderYawDegrees() {
		return _desiredEncoderYaw;
	}

	public LidarSpin(int rlay, int enc_A, int enc_B) {
		try {
			_spinner = new Relay(rlay);
			_spinner.setDirection(Relay.Direction.kBoth);
			_spinner.setSafetyEnabled(false);

			_spinMode = SpinMode.IDLE;
			_enc1 = new EncoderWithNotify(enc_A, enc_B);
			_enc1.reset();
			_enc1.setDistancePerPulse(0.724346);
		} catch (Exception e) {
			SmartDashboard.putString("debug", "LidarSpin constructor failed");
		}
	}

	public synchronized void setScanLimits(double min, double max) {
		double myMin = min;
		double myMax = max;
		if (myMin < LIDAR_SCAN_MIN_DEGREES) {
			myMin = LIDAR_SCAN_MIN_DEGREES;
		}
		if (myMin >= 0.0) {
			myMin = -1.0; // Change direction requires min scan angle < zero
		}
		if (myMax > LIDAR_SCAN_MAX_DEGREES) {
			myMax = LIDAR_SCAN_MAX_DEGREES;
		}
		if (myMax < 0.0) {
			myMax = 1.0; // Change direction requires max scan angle > 0
		}
		_minScanDegrees = myMin;
		_maxScanDegrees = myMax;
		SmartDashboard.putString("debug", "scan limits = " + _minScanDegrees + "    " + _maxScanDegrees);
	}

	public synchronized double getTurntableAngle() {
		return _enc1.getDistance() + _minScanDegrees;
	}

	public synchronized double getMinScanDegrees() {
		return _minScanDegrees;
	}

	public synchronized double getMaxScanDegrees() {
		return _maxScanDegrees;
	}

	public synchronized void encoderUpdate() {
		// When the scan motor is turning this interrupt happens
		// about 60 times as often as the vision update
		// We begin scanning less often, (based on vision),
		// and use this function to stop as soon as the
		// encoder count passes the desired angle

		boolean prevDirection = _direction;

		if (encoderYawDegrees() < _desiredEncoderYaw) {
			_direction = true;
		} else {
			_direction = false;
		}
		if (_direction != prevDirection) {
			_spinner.set(Relay.Value.kOff);
			SmartDashboard.putString("spinner", "Interrupt Off");
		}
		SmartDashboard.putString("debug", "enc interrupt, new yaw = " + encoderYawDegrees());
	}

	public synchronized void spinnerex() {
		// This is called once per vision camera frame, after the BoilerTracker
		// has updated the desired turntable angle

		try {
			double myEncoderYaw = encoderYawDegrees();
			SmartDashboard.putString("encoderYawDegrees", "yaw: " + myEncoderYaw + "   desYaw: " + _desiredEncoderYaw);

			if (_spinMode == SpinMode.IDLE) {
				_spinner.set(Relay.Value.kOff);
				SmartDashboard.putString("spinner", "IDLE Off");

			} else if (_spinMode == SpinMode.SCAN) {
				if (myEncoderYaw < _minScanDegrees) {
					_direction = true;
					_desiredEncoderYaw = _maxScanDegrees;
				} else if (myEncoderYaw > _maxScanDegrees) {
					_direction = false;
					_desiredEncoderYaw = _minScanDegrees;
				}
				if (_direction) {
					_spinner.set(Relay.Value.kForward);
					SmartDashboard.putString("spinner", "SCAN FWD");
				} else {
					_spinner.set(Relay.Value.kReverse);
					SmartDashboard.putString("spinner", "SCAN REV");
				}
			} else if (_spinMode == SpinMode.FIXED_OFFSET_FROM_YAW) {
				if (Math.abs(myEncoderYaw - _desiredEncoderYaw) < LIDAR_FIXED_DEADZONE_DEGREES) {
					_spinner.set(Relay.Value.kOff);
					SmartDashboard.putString("spinner", "FIX Off");
				} else {
					if (myEncoderYaw < _desiredEncoderYaw) {
						_direction = true;
					} else {
						_direction = false;
					}
					if (_direction) {
						_spinner.set(Relay.Value.kForward);
						SmartDashboard.putString("spinner",
								"FIX FWD  yaw: " + myEncoderYaw + "   desYaw: " + _desiredEncoderYaw);
					} else {
						_spinner.set(Relay.Value.kReverse);
						SmartDashboard.putString("spinner",
								"FIX REV  yaw: " + myEncoderYaw + "   desYaw: " + _desiredEncoderYaw);
					}
				}
			}
		} catch (Exception e) {
			SmartDashboard.putString("debug", "spinnerex failed");
		}
	}
}
