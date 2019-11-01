package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BoilerTracker {

	// This class controls movement of a turntable motor upon which is mounted a
	// GRIP vision camera and a LIDAR.
	//
	// If GripVisionThread does not detect the retro-reflective tape on the
	// boiler, the turntable is moved through a scan pattern from -190 to +190
	// degrees.
	//
	// If GripVisionThread provides an X pixel, the current yaw angle is
	// sampled, and an estimate of a desired yaw angle is computed that would
	// place the X pixel
	// in the middle of the frame.

	private boolean _isBoilerRangeValid = false;
	private double _boilerRangeFeet = 0.0;

	private boolean _isRobotPositionValid = false;
	private double _fieldFrameAngleBoilerToRobotDegrees = 0.0;

	private boolean _isValidCurrentRobotFieldPosition = false;
	private RobotPositionPolar _currentRobotFieldPosition = new RobotPositionPolar();

	public synchronized RobotPositionPolar currentRobotFieldPosition() {
		return _currentRobotFieldPosition;
	}

	public synchronized void visionUpdate() {

		SmartDashboard.putBoolean("isValidGripCameraCenterX:  ", GripVisionThread.isValidGripCameraCenterX());

		if (!GripVisionThread.isValidGripCameraCenterX()) {
			_isBoilerRangeValid = false;
			_isValidCurrentRobotFieldPosition = false;
			SmartDashboard.putString("Robot Field Position", "*** Not valid ***");
		} else {
			_boilerRangeFeet = Robot.boilerLidar.lidarDistanceCentimeters() / (12.0 * 2.54);
			_isBoilerRangeValid = (_boilerRangeFeet != 0.0);
			if (_isBoilerRangeValid) {
				SmartDashboard.putNumber("LIDAR Range feet", _boilerRangeFeet);
			} else {
				SmartDashboard.putString("LIDAR Range feet", "*** Not valid ***");
			}

			double offCenter = GripVisionThread.degreesOffCenterX();
			double currentYaw = Robot.imu.getYaw() + Robot.turntable1.encoderYawDegrees();
			Robot.turntable1.setDesiredEncoderYawDegrees(currentYaw + offCenter);

			if (_isBoilerRangeValid) {
				// publish robot position
				_currentRobotFieldPosition.radius = _boilerRangeFeet;

				// rotational orientation of the robot at this position
				_currentRobotFieldPosition.yawOffsetRobot = currentYaw;

				// turntable angle in robot frame
				_currentRobotFieldPosition.hdgToBoiler = Robot.turntable1.encoderYawDegrees();

				// turntable angle wherever the front of the robot is pointing
				_currentRobotFieldPosition.hdgToBoiler += Robot.imu.getYaw();

				// turntable angle in field frame
				_currentRobotFieldPosition.hdgToBoiler += Robot.yawOffsetToFieldFrame();

				_currentRobotFieldPosition.isBlueBoiler = (_currentRobotFieldPosition.hdgToBoiler < 90);

				SmartDashboard.putString("Robot Field Position", _currentRobotFieldPosition.displayText());
			} else {
				// Robot position is not valid
				_isValidCurrentRobotFieldPosition = false;
				SmartDashboard.putString("Robot Field Position", "*** Not valid ***");
			}
		}
	}
}
