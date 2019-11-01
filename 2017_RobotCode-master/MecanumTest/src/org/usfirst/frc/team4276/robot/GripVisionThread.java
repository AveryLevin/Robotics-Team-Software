package org.usfirst.frc.team4276.robot;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GripVisionThread extends Thread implements Runnable {

	private static int camNumber = 0;

	private static final int GRIPCAM_FRAMES_PER_SECOND = 10;

	// Microsoft Lifecam HD-3000 diagonal field of view is 68.5 degrees
	private static final double CAM_HORIZ_FOV_DEGREES = 61.0;
	private static final int CAM_IMG_WIDTH = 424;
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
		SmartDashboard.putBoolean("isValidGripCameraCenterX", _isValidGripCameraCenterX);
	}

	public synchronized static double gripCameraCenterX() {
		return _gripCameraCenterX;
	}

	private synchronized static void set_gripCameraCenterX(double val) {
		_gripCameraCenterX = val;
		SmartDashboard.putNumber("gripCameraCenterX", _gripCameraCenterX);
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

	public GripVisionThread(int nCamera) {
		camNumber = nCamera;
		myGripPipeline = new GripPipeline();
		setDaemon(true);
	}

	// DC1394: exposure control done by camera, user can adjust refernce level
	// using this feature
	@Override
	public void run() {

		UsbCamera camGRIP = CameraServer.getInstance().startAutomaticCapture(camNumber);

		camGRIP.setResolution(CAM_IMG_WIDTH, CAM_IMG_HEIGHT);
		camGRIP.setExposureManual(gripCameraExposure());
		camGRIP.setFPS(GRIPCAM_FRAMES_PER_SECOND);

		CvSink cvSink = CameraServer.getInstance().getVideo();
		CvSource outputStream = CameraServer.getInstance().putVideo("cam GRIP", CAM_IMG_WIDTH, CAM_IMG_HEIGHT);
		Scalar colorGreen = new Scalar(0, 255, 0);
		Mat frame = new Mat();
		while (true) {
			cvSink.grabFrame(frame);
			incr_gripCameraFrameSequence();
			myGripPipeline.process(frame);
			if (myGripPipeline.findContoursOutput().isEmpty()) {
				set_isValidGripCameraCenterX(false);
			} else {
				Rect emptyRect = new Rect();
				Rect rLargest = findLargestContour(myGripPipeline.findContoursOutput());
				if (rLargest != emptyRect) {
					set_gripCameraCenterX(rLargest.x + (rLargest.width / 2));
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
			outputStream.putFrame(frame);
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
