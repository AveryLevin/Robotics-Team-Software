package org.usfirst.frc.team4276.robot;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import edu.wpi.cscore.CvSource;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GripVisionThread extends Thread implements Runnable {

	// The VEX Spike relay used to control turntable position
	// can open or close no more often than 20 times per second.
	//
	// This control code may start the turntable moving as often as the vision
	// camera frame rate, and stop it again between camera frames, so even if
	// the camera can run faster we should limit the frame rate to 10 per
	// second.
	private static final int GRIPCAM_FRAMES_PER_SECOND = 10;

	// Microsoft Lifecam HD-3000 diagonal field of view is 68.5 degrees
	private static final double CAM_HORIZ_FOV_DEGREES = 61.0;
	private static final int CAM_IMG_WIDTH = 320;
	private static final int CAM_IMG_HEIGHT = 240;

	public static final int CAP_PROP_FPS = 5;
	public static final int CAP_PROP_FRAME_WIDTH = 3;
	public static final int CAP_PROP_FRAME_HEIGHT = 4;
	public static final int CAP_PROP_BRIGHTNESS = 10;
	public static final int CAP_PROP_CONTRAST = 11;
	public static final int CAP_PROP_SATURATION = 12;
	public static final int CAP_PROP_HUE = 13;
	public static final int CAP_PROP_EXPOSURE = 15;
	public static final int CAP_PROP_AUTO_EXPOSURE = 21;

	// Using a relay to control turntable position means it stops abruptly,
	// blurring the camera image.
	// This delay is intended to let the vibration settle down so we get a clean
	// image before we decide how to move the turntable next.
	// If the robot isn't moving we expect to be in the dead zone and the
	// turntable should not need to move.
	private static final int NUMBER_OF_FRAMES_BEFORE_CHANGE_SPIN_MODE = 2;
	private int delayCountForSpinModeChange = NUMBER_OF_FRAMES_BEFORE_CHANGE_SPIN_MODE;

	private GripPipeline myGripPipeline;
	private static boolean _isValidGripCameraCenterX = false;
	private static double _gripCameraCenterX = 0.0;

	private int _gripCameraExposure = 80;
	private int _gripCameraFrameSequence = 0;

	public synchronized static boolean isValidGripCameraCenterX() {
		return _isValidGripCameraCenterX;
	}

	private synchronized static void set_isValidGripCameraCenterX(boolean val) {
		_isValidGripCameraCenterX = val;
	}

	public synchronized static double gripCameraCenterX() {
		return _gripCameraCenterX;
	}

	private synchronized static void set_gripCameraCenterX(double val) {
		_gripCameraCenterX = val;
	}

	public synchronized int gripCameraExposure() {
		return _gripCameraExposure;
	}

	public synchronized int gripCameraFrameSequence() {
		return _gripCameraFrameSequence;
	}

	private synchronized void incr_gripCameraFrameSequence() {
		_gripCameraFrameSequence++;
	}

	public GripVisionThread() {
		myGripPipeline = new GripPipeline();
		setDaemon(true);
	}

	// DC1394: exposure control done by camera, user can adjust refernce level
	// using this feature
	@Override
	public void run() {

		CvSource outputStreamStd = CameraServer.getInstance().putVideo("Boiler Tracker", CAM_IMG_WIDTH, CAM_IMG_HEIGHT);
		VideoCapture camGRIP = new VideoCapture(0);

		camGRIP.set(CAP_PROP_FPS, GRIPCAM_FRAMES_PER_SECOND);
		camGRIP.set(CAP_PROP_EXPOSURE, gripCameraExposure());
		camGRIP.set(CAP_PROP_FRAME_WIDTH, CAM_IMG_WIDTH);
		camGRIP.set(CAP_PROP_FRAME_HEIGHT, CAM_IMG_HEIGHT);

		Scalar colorGreen = new Scalar(0, 255, 0);
		Mat frame = new Mat();
		camGRIP.read(frame);
		if (!camGRIP.isOpened()) {
			System.out.println("Error");
		} else {
			while (true) {
				if (camGRIP.read(frame)) {
					incr_gripCameraFrameSequence();
					myGripPipeline.process(frame);
					if (myGripPipeline.findContoursOutput().isEmpty()) {
						set_isValidGripCameraCenterX(false);
					} else {
						Rect emptyRect = new Rect();
						Rect rLargest = findLargestContour(myGripPipeline.findContoursOutput());
						if (rLargest != emptyRect) {
							set_gripCameraCenterX(rLargest.x + (rLargest.width / 2));
							SmartDashboard.putNumber("gripCameraCenterX", gripCameraCenterX());

							set_isValidGripCameraCenterX(true);

							// Find midpoints of the 4 sides of the rectangle,
							// and draw from those points to the center
							Point pt0 = new Point(rLargest.x, rLargest.y);
							Point pt1 = new Point(rLargest.x + rLargest.width, rLargest.y);
							Point pt2 = new Point(rLargest.x, rLargest.y + rLargest.height);
							Point pt3 = new Point(rLargest.x + rLargest.width, rLargest.y + rLargest.height);

							Imgproc.line(frame, pt0, pt3, colorGreen, 2);
							Imgproc.line(frame, pt1, pt2, colorGreen, 2);
						}
					}

					outputStreamStd.putFrame(frame);
					SmartDashboard.putNumber("camGRIP frame#", gripCameraFrameSequence());
					SmartDashboard.putNumber("camGRIP Done frame#", gripCameraFrameSequence());
					if (!Robot.isBoilerTrackerEnabled) {
						Robot.turntable1.setSpinMode(LidarSpin.SpinMode.IDLE);
					} else if (GripVisionThread.isValidGripCameraCenterX()) {
						if (Robot.turntable1.spinMode() == LidarSpin.SpinMode.SCAN) {
							delayCountForSpinModeChange = NUMBER_OF_FRAMES_BEFORE_CHANGE_SPIN_MODE;
							Robot.turntable1.setSpinMode(LidarSpin.SpinMode.IDLE);
						}
						if (delayCountForSpinModeChange-- <= 0) {
							Robot.turntable1.setSpinMode(LidarSpin.SpinMode.FIXED_OFFSET_FROM_YAW);
						}
					} else {
						// !isValidGripCameraCenterX
						if (Robot.turntable1.spinMode() == LidarSpin.SpinMode.FIXED_OFFSET_FROM_YAW) {
							delayCountForSpinModeChange = NUMBER_OF_FRAMES_BEFORE_CHANGE_SPIN_MODE;
							Robot.turntable1.setSpinMode(LidarSpin.SpinMode.IDLE);
						}
						if (delayCountForSpinModeChange-- <= 0) {
							Robot.turntable1.setSpinMode(LidarSpin.SpinMode.SCAN);
						}
					}

					Robot.boilerTracker.visionUpdate();
					Robot.turntable1.spinnerex();
				}
			}
		}
	}

	public static double degreesOffCenterX() {

		// Calculate desired yaw angle so the vision target would be in the
		// center of the frame
		double degreesPerPixel = CAM_HORIZ_FOV_DEGREES / CAM_IMG_WIDTH;
		double pixelsOffCenter = gripCameraCenterX() - (CAM_IMG_WIDTH / 2);
		return pixelsOffCenter / degreesPerPixel;
	}

	private Rect findLargestContour(ArrayList<MatOfPoint> listContours) {
		Rect rRet = new Rect();
		for (int i = 0; i < listContours.size(); i++) {
			Rect r = Imgproc.boundingRect(listContours.get(i));
			if (rRet.area() < r.area()) {
				rRet = r;
			}
		}
		return rRet;
	}

}
