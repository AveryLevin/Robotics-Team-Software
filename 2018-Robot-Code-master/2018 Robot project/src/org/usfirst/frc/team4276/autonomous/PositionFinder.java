package org.usfirst.frc.team4276.autonomous;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team4276.systems.ADIS16448_IMU;

import edu.wpi.first.networktables.NetworkTable;

public class PositionFinder extends Thread implements Runnable {

	Encoder driveEncoderL, driveEncoderR;
	static ADIS16448_IMU robotIMU;

	static double[] previousXY = new double[2];
	public static double[] currentXY = new double[2];

	static double currentHeadingDeg = 0;
	static double currentHeadingRad = 0;

	static double currentX = 0;
	static double currentY = 0;

	double previousLeft = 0;
	double previousRight = 0;

	public boolean breakLoop = false;
	private static boolean skipXYAssignment = false;

	public PositionFinder(int encoder1A, int encoder1B, int encoder2A, int encoder2B) {

		driveEncoderL = new Encoder(encoder1A, encoder1B);
		driveEncoderR = new Encoder(encoder2A, encoder2B);

		driveEncoderL.setDistancePerPulse((10 / 11564.75));// was 10 /
															// 8304.316667
		driveEncoderR.setDistancePerPulse((10 / 11564.75));

		robotIMU = new ADIS16448_IMU();

	}

	private void updateHeading() {
		double currentHeadingTemp = -1 * robotIMU.getAngleZ();
		while (currentHeadingTemp > 180) {
			currentHeadingTemp = currentHeadingTemp - 360;
		}
		while (currentHeadingTemp < -180) {
			currentHeadingTemp = currentHeadingTemp + 360;
		}
		currentHeadingDeg = currentHeadingTemp;
		currentHeadingRad = Math.toRadians(currentHeadingDeg);
	}

	public static double getHeadingDeg() {
		return currentHeadingDeg;
	}

	public static double getHeadingRad() {
		return currentHeadingRad;
	}

	private void updatePosition() {
		double currentLeft = driveEncoderL.getDistance();
		double PL = -1 * (currentLeft - previousLeft);
		previousLeft = currentLeft;
		//driveEncoderL.reset(); // disable to calibrate

		double currentRight = driveEncoderR.getDistance();
		double PR = currentRight - previousRight;
		previousRight = currentRight;
		//driveEncoderR.reset(); // disable to calibrate

		SmartDashboard.putNumber("LEFT ENCODER", PL);
		SmartDashboard.putNumber("RIGHT ENCODER", PR);

		SmartDashboard.putNumber("LEFT ENCODER Raw", currentLeft);
		SmartDashboard.putNumber("RIGHT ENCODER Raw", currentRight);

		double deltaPosition = 0.5 * (PL + PR);
		double deltaX = Math.cos(currentHeadingRad) * deltaPosition;
		double deltaY = Math.sin(currentHeadingRad) * deltaPosition;

		currentX = previousXY[0] + deltaX;
		currentY = previousXY[1] + deltaY;
		currentXY[0] = currentX;
		currentXY[1] = currentY;
	}

	public static double[] getCurrentLocation() {
		return currentXY;
	}

	public static void setStartPoint(double XY[]) {
		previousXY = XY;
		// won't be overwritten
		skipXYAssignment = true;
	}

	public void kill() {
		breakLoop = true;
	}

	public void calibrateImu() {
		robotIMU.calibrate();
	}

	private void updateSmartDashboard() {
		SmartDashboard.putNumber("Heading", currentHeadingDeg);
		SmartDashboard.putNumber("Robot X:", currentXY[0]);
		SmartDashboard.putNumber("Robot Y:", currentXY[1]);
	}

	public void run() {

		while (true) {
			// if set to skip overwrite
			if (skipXYAssignment) {
				// only skip once
				skipXYAssignment = false;
			} else {
				// save last recorded position
				previousXY = currentXY;
			}
			updateHeading();
			updatePosition();
			updateSmartDashboard();

			if (breakLoop) {
				break;
			}

			Timer.delay(00.05);
		}
	}
}
