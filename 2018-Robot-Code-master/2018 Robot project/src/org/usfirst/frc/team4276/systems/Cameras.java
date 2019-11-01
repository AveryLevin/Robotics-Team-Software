package org.usfirst.frc.team4276.systems;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.cscore.UsbCamera;

public class Cameras {

	UsbCamera mainCamera;
	UsbCamera armCamera;

	private final int MAIN_RES_X = 352;
	private final int MAIN_RES_Y = 288;
	private final int MAIN_FPS = 25;
	private final int MAIN_EXPOSURE = 20;

	private final int ARM_RES_X = 424;
	private final int ARM_RES_Y = 240;
	private final int ARM_FPS = 15;
	private final int ARM_EXPOSURE = 40;

	public Cameras() {
		mainCamera = CameraServer.getInstance().startAutomaticCapture(1);
		mainCamera.setResolution(MAIN_RES_X, MAIN_RES_Y);
		mainCamera.setFPS(MAIN_FPS);
	    mainCamera.setExposureManual(MAIN_EXPOSURE);

		armCamera = CameraServer.getInstance().startAutomaticCapture(0);
		armCamera.setResolution(ARM_RES_X, ARM_RES_Y);
		armCamera.setFPS(ARM_FPS);
		armCamera.setExposureManual(ARM_EXPOSURE);
	}

}
