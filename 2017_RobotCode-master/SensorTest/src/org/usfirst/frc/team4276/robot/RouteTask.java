package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RouteTask {
	public enum Operation {
		STOP, WAIT, DRIVE, DRIVE_VISION_ARC, STRAFE_ALIGN_BOILER, SHOOT_BOILER, PLACE_GEAR, COLLECT_FUEL, COLLECT_FUEL_STOP, COLLECT_GEAR_DEPLOY, COLLECT_GEAR_PICKUP
	}

	public enum DrivingSpeed {
		STOPPED, SLOW_SPEED, SLOWER_SPEED, FULL_SPEED
	}

	public enum ReturnValue {
		SUCCESS, FAILED
	}

	public Operation op = Operation.STOP;
	public RobotPositionPolar endPos = new RobotPositionPolar(true, 0.0, 0.0, 0.0);
	double initialHeading = 0.0;
	double wheelEncoderDistance = 0.0;
	public long delayMillisecs = 0;

	public RouteTask(Operation oper, int param) {
		op = oper;
		delayMillisecs = param;
	}

	public RouteTask(Operation oper) {
	}

	public RouteTask(Operation oper, RobotPositionPolar pos) {
		op = oper;

		endPos.isBlueBoiler = pos.isBlueBoiler;
		endPos.radius = pos.radius;
		endPos.hdgToBoiler = pos.hdgToBoiler;
		endPos.yawOffsetRobot = pos.yawOffsetRobot;
	}
	
	public RouteTask(Operation oper, double hdg, double dist) {
		initialHeading = hdg;
		wheelEncoderDistance = dist;
	}

	public ReturnValue driveIMU(double initialHeading, double wheelEncoderDist) {
		// TODO:  
		// Turn to initial heading and note IMU gyro heading
		// while wheel encoder distance has not been traveled
		//     Calculate current position by double integrating accelerometers
		//     Compare to previous position to get course made good
		//     Compare course to gyro and adjust motors to stay on the gyro heading
		//     Save current position for next iteration
		// Stop
		return ReturnValue.FAILED;
	}

	public ReturnValue driveIMUToArc(double initialHeading, double endRadius) {
		// TODO:  
		// If already within tolerance of arc radius
		//    Return
		// Endif
		// Select forward or reverse along initial heading based on current radius and end radius
		// If reverse
		//    add 180 to initial heading
		// Endif
		// estimate = distance to arc intersection + 5%
		// Turn to heading and note IMU gyro heading
		// while current radius is not within tolerance of endRadius
		//     Calculate current position by double integrating accelerometers
		//     Compare to previous position to get course made good
		//     Compare course to gyro and adjust motors to stay on the gyro heading
		//     Save current position for next iteration
		// Stop
		return ReturnValue.FAILED;
	}
	
	public ReturnValue driveVisionRadial(double endRadius) {
		// TODO:
		// If current position radius < endRadius
		//     Turn away from boiler
		// Else
		//     Turn toward boiler
		// Endif
		// while current radius is not within tolerance of endRadius
		//     Calculate current position by double integrating accelerometers
		//     Compare to previous position to get course made good
		//     Compare course to gyro and adjust motors to stay on the gyro heading
		//     Save current position for next iteration
		// Stop
		return ReturnValue.FAILED;
	}
	
	public ReturnValue driveVisionArc(RobotPositionPolar posEnd) {
		// TODO:
		// If current position hdgToBoiler < posEnd hdgToBoiler
		//     Turn to current position hdgToBoiler -90 degrees 
		// Else
		//     Turn to current position hdgToBoiler +90 degrees 
		// Endif
		// while position hdgToBoiler is not within tolerance of posEnd hdgToBoiler
		//     Calculate current position by double integrating accelerometers
		//     Compare to previous position to get course made good
		//     Compare course to gyro and adjust motors to stay on the gyro heading
		//     Save current position for next iteration
		// Stop
		return ReturnValue.FAILED;
	}

	public String displayText() {
		String sRet = opToText(op);
		switch(op) {
		case DRIVE:
			sRet += " " + wheelEncoderDistance + " feet on IMU heading " + initialHeading;
			sRet += " to " + endPos.displayText();
			break;
			
		case DRIVE_VISION_ARC:
			sRet += " to " + endPos.displayText() + " finishing on arc of radius " + endPos.radius;
			break;
			
		case WAIT:
			sRet += " ";
			sRet += delayMillisecs;
			sRet += " ms.";
			break;
			
		default:
			break;
		}		
		return sRet;
	}

	public ReturnValue exec() {
		SmartDashboard.putString("Auto Status", displayText());

		switch (op) {
		case STOP:
			// TODO:
			// All motors except for vision system off, including drive, collectors, gear collector, etc.
			return ReturnValue.FAILED;

		case WAIT:
			try {
				Thread.sleep(delayMillisecs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return ReturnValue.SUCCESS;

		case DRIVE:
			return driveIMU(initialHeading, wheelEncoderDistance);
			
		case DRIVE_VISION_ARC:
			// TODO:
			// If current radius is not within tolerance of endPos radius
			//     Estimate initial heading to a place on the arc of endPos radius, a foot or two before it
			//     driveIMUToArc( initialHeading, endPos.radius)
			// Endif
			return driveVisionArc(endPos);

		case STRAFE_ALIGN_BOILER:
			// TODO:
			// Drive forward a foot or so to make contact with boiler wall, and keep a little forward drive while strafing
			// Strafe side to side until vision turntable is withing tolerance of 90 degress
			return ReturnValue.FAILED;

		case SHOOT_BOILER:
			// TODO:
			return ReturnValue.FAILED;

		case PLACE_GEAR:
			// TODO:
			return ReturnValue.FAILED;

		case COLLECT_FUEL:
			// TODO:
			return ReturnValue.FAILED;

		case COLLECT_FUEL_STOP:
			// TODO:
			return ReturnValue.FAILED;

		case COLLECT_GEAR_DEPLOY:
			// TODO:
			return ReturnValue.FAILED;

		case COLLECT_GEAR_PICKUP:
			// TODO:
			return ReturnValue.FAILED;

		default:
			break;
		}
		return ReturnValue.FAILED;
	}

	public static final String opToText(Operation opr) {
		switch (opr) {
		case STOP:
			return "STOP";

		case WAIT:
			return "WAIT"; // first arg is milliseconds to wait

		case DRIVE:
			return "DRIVE";

		case STRAFE_ALIGN_BOILER:
			return "STRAFE_ALIGN_BOILER";

		case SHOOT_BOILER:
			return "SHOOT_BOILER";

		case PLACE_GEAR:
			return "PLACE_GEAR";

		case COLLECT_FUEL:
			return "COLLECT_FUEL";

		case COLLECT_FUEL_STOP:
			return "COLLECT_FUEL_STOP";

		case COLLECT_GEAR_DEPLOY:
			return "COLLECT_GEAR_DEPLOY";

		case COLLECT_GEAR_PICKUP:
			return "COLLECT_GEAR_PICKUP";

		default:
			break;
		}
		return "????";
	}

}
