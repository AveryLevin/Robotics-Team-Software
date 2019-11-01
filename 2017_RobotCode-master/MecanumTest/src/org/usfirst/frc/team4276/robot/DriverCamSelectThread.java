package org.usfirst.frc.team4276.robot;

import org.opencv.core.Mat;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriverCamSelectThread extends Thread implements Runnable {

	public static final int DRIVER_CAM_IMG_WIDTH = 424;
	public static final int DRIVER_CAM_IMG_HEIGHT = 240;
	public static final int DRIVER_CAM_FPS = 30;

	int selectCam = 0;

	public void run() {

		UsbCamera driverCam2 = CameraServer.getInstance().startAutomaticCapture(2);
		//driverCam2.setResolution(DRIVER_CAM_IMG_WIDTH, DRIVER_CAM_IMG_HEIGHT);
		//driverCam2.setFPS(DRIVER_CAM_FPS);
		UsbCamera driverCam3 = CameraServer.getInstance().startAutomaticCapture(3);
		//driverCam3.setResolution(DRIVER_CAM_IMG_WIDTH, DRIVER_CAM_IMG_HEIGHT);
		//driverCam3.setFPS(DRIVER_CAM_FPS);

		CvSink cvSink1 = CameraServer.getInstance().getVideo(driverCam2);
		CvSink cvSink2 = CameraServer.getInstance().getVideo(driverCam3);
		CvSource outputStream = CameraServer.getInstance().putVideo("Driver Cam", DRIVER_CAM_IMG_WIDTH,
				DRIVER_CAM_IMG_HEIGHT);

		Mat image = new Mat();

		while (!Thread.interrupted()) {

			// if(oi.getGamepad().getRawButton(9)) {
			// selectCam = !selectCam;
			// }
			switch (selectCam) {
			case 0:
				SmartDashboard.putString("Driver Camera", "Front");
				cvSink2.setEnabled(false);
				cvSink1.setEnabled(true);
				cvSink1.grabFrame(image);
				break;
				
			case 1:
				SmartDashboard.putString("Driver Camera", "Back");
				cvSink1.setEnabled(false);
				cvSink2.setEnabled(true);
				cvSink2.grabFrame(image);
				break;
			}
			

			outputStream.putFrame(image);
		}

	}
}