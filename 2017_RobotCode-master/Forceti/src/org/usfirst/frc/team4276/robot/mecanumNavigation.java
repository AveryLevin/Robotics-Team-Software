package org.usfirst.frc.team4276.robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

public class mecanumNavigation extends Thread implements Runnable  {
	
	
	/*
	 * This continuously running thread receives input from the 
	 * encoders on the robot's mecanum drive train and uses the
	 * linear directionality transformation to solve for the delta
	 * movement of the robot in the robot's X and Y axes. This 
	 * thread then uses these deltas to solve for the robot's 
	 * movement in the field's coordinate system (shown below) 
	 * using the Direction Cosine Matrix. The absolute robot 
	 * position is then calculated with the accumulating deltas.
	 * 
	 * Field Coordinate System
	 *                             ^
	 *    _________________________|__________________________
	 *    |                        |                         |
	 *    |                        |+Y                       |
 	 *    |                        |                         |
 	 *    |                        |                         |
 	 *    |     -X                 |              +X         |
 	 *  <-|------------------------|-------------------------|->
 	 *    |                        |                         |
 	 *    |                        |                         |
 	 *    |	Red Alliance           |        Blue Alliance    | 
 	 *    |                        |-Y                       |
 	 *    |________________________|_________________________|
 	 *    Boiler                   |                    Boiler
 	 *                             V
 	 *                             
 	 *   @author Avery                         
 	 *                             
	*/

	
	static double robotX = 0;
	static double robotY = 0;
	static double robotDeltaX;
	static double robotDeltaY;
	
	static double yaw = 0;
	
static Encoder frontLeftWheel;
static Encoder backLeftWheel;
//static Encoder frontRightWheel;
//static Encoder backRightWheel;

static double Kx = 1; //place holder
static double Ky = .76; //place holder

static double frontLeftWheelDelta = 0;
static double backLeftWheelDelta = 0;
static double frontRightWheelDelta = 0;
static double backRightWheelDelta = 0;

double totalFLWheelDistance = 0;
double totalBLWheelDistance = 0;
double totalFRWheelDistance = 0;
double totalBRWheelDistance = 0;

double deltaX_FieldFrame;
double deltaY_FieldFrame;

static double currentFieldX;
static double currentFieldY;
static double oldX_FieldFrame;
static double oldY_FieldFrame;

static double theta;
static double yawStartingOffset = 0; //default

boolean ERROR;

public mecanumNavigation(int dio0, int dio1, int dio2, int dio3)
{
	frontLeftWheel = new Encoder(dio0, dio1);
	backLeftWheel = new Encoder(dio2, dio3);
	//frontRightWheel = new Encoder(dio4, dio5);
	//backRightWheel = new Encoder(dio6, dio7);
	frontLeftWheel.setDistancePerPulse(-1.0/360.0); //place holder
	backLeftWheel.setDistancePerPulse(-1.0/360.0); //place holder
	//frontRightWheel.setDistancePerPulse(1.0/360.0); //place holder
	//backRightWheel.setDistancePerPulse(1.0/360.0); //place holder
	
	frontLeftWheel.reset();
	backLeftWheel.reset();
	//frontRightWheel.reset();
	//backRightWheel.reset();
}

double findDeltaX_RobotFrame(double FL,double BL,double FR,double BR)
{
	double leftWheelsX = FL-BL;
	//double rightWheelsX = BR-FR;
	double Xnet = Kx*((leftWheelsX));//+(rightWheelsX));
	return Xnet;
}

double findDeltaY_RobotFrame(double FL,double BL,double FR,double BR)
{
	double leftWheelsY = FL+BL;
	//double rightWheelsY = BR+FR;
	double Ynet = Ky*((leftWheelsY));//+(rightWheelsY));
	return Ynet;
}

void correctYawAngle(){
	
	yaw = -1*Robot.imu.getAngleZ()/4;
	yaw = yaw + yawStartingOffset;
	
	while (yaw > 180)
	{
		yaw = yaw-360;
	}
	while (yaw < -180)
	{
		yaw = yaw+360;
	}
	
	
}

void findDeltaMovement_RobotFrame()
{
	
	theta = Math.toRadians(yaw + yawStartingOffset);
	
	theta = 1/2*Math.PI+0;
	
	frontLeftWheelDelta = frontLeftWheel.getDistance() - totalFLWheelDistance; // finds delta distance of FrontLeft Wheel
	backLeftWheelDelta = backLeftWheel.getDistance() - totalBLWheelDistance; // finds delta distance of BackLeft Wheel
	//frontRightWheelDelta = frontRightWheel.getDistance() - totalFRWheelDistance; // finds delta distance of FrontRight Wheel
	//backRightWheelDelta = backRightWheel.getDistance() - totalBRWheelDistance; // finds delta distance of BackRight Wheel
	
	robotDeltaY = findDeltaY_RobotFrame(frontLeftWheelDelta, backLeftWheelDelta, frontRightWheelDelta, backRightWheelDelta); //returns the value of the robot's Delta Y
	robotDeltaX = findDeltaX_RobotFrame(frontLeftWheelDelta, backLeftWheelDelta, frontRightWheelDelta, backRightWheelDelta); //returns the value of the robot's Delta X
	
	robotX = robotX +robotDeltaX;
	robotY = robotY +robotDeltaY;
	
	SmartDashboard.putNumber("RobotX", robotX);
	SmartDashboard.putNumber("RobotY", robotY);
	
	totalFLWheelDistance = totalFLWheelDistance + frontLeftWheelDelta; //adds recorded delta of the Front Left wheel to update the total distance value
	totalBLWheelDistance = totalBLWheelDistance + backLeftWheelDelta; //adds recorded delta of the Back Left wheel to update the total distance value
	totalFRWheelDistance = totalFRWheelDistance + frontRightWheelDelta; //adds recorded delta of the Front Right wheel to update the total distance value
	totalBRWheelDistance = totalBRWheelDistance + backRightWheelDelta; //adds recorded delta of the Back Right wheel to update the total distance value
	SmartDashboard.putNumber("FL", totalFLWheelDistance);
	SmartDashboard.putNumber("FR", totalFRWheelDistance);
	SmartDashboard.putNumber("BL", totalBLWheelDistance);
	SmartDashboard.putNumber("BR", totalBRWheelDistance);
}

static void setStartingPosition(double X, double Y, double rotation)
{
	/*
	 * Records the starting position and
	 * heading of the robot in order to later 
	 * calculate the robot's position after
	 * moving
	 */
	
	
	oldX_FieldFrame = X;
	oldY_FieldFrame = Y;
	

		yawStartingOffset = rotation; //sets offset of the robot from field coordinate system


}

static void resetDriveEncoders()
{
	frontLeftWheel.reset();
	backLeftWheel.reset();
	//frontRightWheel.reset();
	//backRightWheel.reset();
}

void findAbsoluteLocation_FieldFrame() 
{
	/*
	 * Utilizes the Direction Cosine Matrix to find the 
	 * movement of the robot in the Field Frame
	 * 
	 * Complete Matrix:
	 * 
	 * Qfx =  Cos*Qrx + Sin*Qry + 0*Qrz
	 * Qfy = -Sin*Qrx + Cos*Qry + 0*Qrz
	 * Qfz =   0*Qrx  +  0*Qry  + 1*Qrz
	 * 
	 * Used (more practical) Matrix:
	 * 
	 * Qfx =  Cos*Qrx + Sin*Qry
	 * Qfy = -Sin*Qrx + Cos*Qry
	 * 
	 * Qr_ = movement in robot's frame in given axis
	 * Qf_ = movement in field's frame in given axis
	 */
	
	double cosine_x_RobotFrame = Math.cos(theta); //x solved from X
	double sine_y_RobotFrame = Math.sin(theta); //x solved from Y
	double sine_x_RobotFrame = -1*Math.sin(theta); //y solved from X
	double cosine_y_RobotFrame = Math.cos(theta); //y solved from Y
	
	if(mecanumDrive.rotating == false)
	{
	deltaX_FieldFrame = cosine_x_RobotFrame*robotDeltaX+sine_y_RobotFrame*robotDeltaY;
	deltaY_FieldFrame = sine_x_RobotFrame*robotDeltaX+cosine_y_RobotFrame*robotDeltaY;
	}
	else
	{
		deltaX_FieldFrame = 0;
		deltaY_FieldFrame = 0;	
	}
	currentFieldX = deltaX_FieldFrame + oldX_FieldFrame;
	currentFieldY = deltaY_FieldFrame + oldY_FieldFrame;
	
	SmartDashboard.putNumber("Current X Location:", currentFieldX);
	SmartDashboard.putNumber("Current Y Location:", currentFieldY);
	
	oldX_FieldFrame = currentFieldX;
	oldY_FieldFrame = currentFieldY;
	
}

public void run()
{
	ERROR=true;
	try
	{
		resetDriveEncoders();
		Robot.imu.calibrate();
		Robot.imu.reset();
		while(true)
		{
		ERROR = false;
		
		correctYawAngle();
		
		SmartDashboard.putNumber("YAW", yaw);
		
		
		findDeltaMovement_RobotFrame();
		findAbsoluteLocation_FieldFrame();
		
		mecanumDrive.modeReadout();
		
		SmartDashboard.putBoolean("Mecanum Location Error", ERROR);
		
		Timer.delay(.005);
		
		}
	}
		catch (Exception e)
		{
		ERROR = true;
		SmartDashboard.putBoolean("Mecanum Location Error", ERROR);
		}
	
}

}
