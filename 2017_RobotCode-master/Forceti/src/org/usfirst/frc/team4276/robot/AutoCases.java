package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/*
 * 	
 * Field Coordinate System
 *                             ^
 *    _________________________|__________________________
 *    |                        |                         |
 *    |                        |+Y                       |
 *    | 3                      |                      3  |
 *    |                        |                         |
 *    |     -X                 |              +X         |
 *  <-|-2----------------------|-----------------------2-|->
 *    |                        |                         |
 *    |                        |                         |
 *    | 1   Red Alliance       |      Blue Alliance    1 | 
 *    |                        |-Y                       |
 *    |________________________|_________________________|
 *    Boiler                   |                    Boiler
 *                             V
 *                             
 *   @author Avery                         
*/

public class AutoCases {

	autoShooter shooter;
	mecanumDrive autoDrive;
	gearCollection autoGear;
	BallCollector autoBall;
	
	// Alliances
	static final int RED = 0;
	static final int BLUE = 1;
	
	// Auto modes
	static final int NOTHING = 0;
	static final int POS_2_GEAR = 1;
	static final int POS_2_GEAR_AND_ZONE = 2;
	static final int POS_3_GEAR_AND_ZONE = 3;
	static final int POS_1_GEAR_AND_ZONE = 4;
	static final int POS_1_SHOOT_FROM_HOPPER = 5;
	static final int POS_2_SHOOT_FROM_HOPPER = 6;
	static final int POS_1_GEAR_AND_SHOOT_FROM_HOPPER = 7;
	static final int POS_1_HOPPER_AND_SHOOT_FROM_BOILER = 8;
	static final int SHOOT_FROM_BOILER = 9;
	
	// Test modes
	static final int TEST_GEAR_DEPOSIT = 10;
	static final int TEST_AUTO_ROTATE = 11;
	static final int TEST_FWD = 12;
	static final int TEST_COORDINATE_DRIVE_SIDE = 13;
	
	//private int[][] autoModeToExecute = new int[2][10];
	
	private int alliance = 0;
	private int autoMode = 0;
	private int autoModeToExecute = 0;
	
	public AutoCases(autoShooter shooterControl, mecanumDrive mecDrive, gearCollection gearArm, BallCollector ballz) {
		shooter = shooterControl;
		autoDrive = mecDrive;
		autoGear = gearArm;
		autoBall = ballz;
		
		/*
		autoModeToExecute[RED][NOTHING] = 0;
		autoModeToExecute[RED][POS_2_GEAR] = 1;
		autoModeToExecute[RED][POS_2_GEAR_AND_ZONE] = 2;
		autoModeToExecute[RED][POS_3_GEAR_AND_ZONE] = 3;
		autoModeToExecute[RED][POS_1_GEAR_AND_ZONE] = 4;
		autoModeToExecute[RED][POS_1_SHOOT_FROM_HOPPER] = 5;
		autoModeToExecute[RED][POS_2_SHOOT_FROM_HOPPER] = 6;
		autoModeToExecute[RED][POS_1_GEAR_AND_SHOOT_FROM_HOPPER] = 7;
		autoModeToExecute[RED][POS_1_HOPPER_AND_SHOOT_FROM_BOILER] = 8;
		autoModeToExecute[RED][SHOOT_FROM_BOILER] = 15;
				
		autoModeToExecute[BLUE][NOTHING] = 20;
		autoModeToExecute[BLUE][POS_2_GEAR] = 21;
		autoModeToExecute[BLUE][POS_2_GEAR_AND_ZONE] = 22;
		autoModeToExecute[BLUE][POS_3_GEAR_AND_ZONE] = 23;
		autoModeToExecute[BLUE][POS_1_GEAR_AND_ZONE] = 24;
		autoModeToExecute[BLUE][POS_1_SHOOT_FROM_HOPPER] = 25;
		autoModeToExecute[BLUE][POS_2_SHOOT_FROM_HOPPER] = 26;
		autoModeToExecute[BLUE][POS_1_GEAR_AND_SHOOT_FROM_HOPPER] = 27;
		autoModeToExecute[BLUE][POS_1_HOPPER_AND_SHOOT_FROM_BOILER] = 28;
		autoModeToExecute[BLUE][SHOOT_FROM_BOILER] = 35;
		
		autoModeToExecute[RED][TEST_GEAR_DEPOSIT] = 40;
		autoModeToExecute[RED][TEST_AUTO_ROTATE] = 41;
		autoModeToExecute[RED][TEST_FWD] = 42;
		autoModeToExecute[RED][TEST_COORDINATE_DRIVE_SIDE] = 43;

		autoModeToExecute[BLUE][TEST_GEAR_DEPOSIT] = 40;
		autoModeToExecute[BLUE][TEST_AUTO_ROTATE] = 41;
		autoModeToExecute[BLUE][TEST_FWD] = 42;
		autoModeToExecute[BLUE][TEST_COORDINATE_DRIVE_SIDE] = 43;
		*/
	}

	void autoModes() { 		
		
		final double RED_STARTING_X = -25.8;// feet
		final double BLUE_STARTING_X = 25.8;// feet
		// these starting X coordinates are true for all mode numbers
		// each side only has one starting X coordinate

		final double MODE_1_STARTING_Y = -6.7;// feet
		final double MODE_2_STARTING_Y = 0;// feet
		final double MODE_3_STARTING_Y = 6.7;// feet
		// these starting Y coordinates are true for both alliance colors
		// each mode only has one starting Y coordinate
		/*
		 * order of modes: 1, 2, 3, with 1 being the positions on both sides
		 * that are closest to the boilers while 3 positions are on sides that
		 * are closest to the retrieval zones 2 positions are the positions on
		 * both sides that are in the middle between the boilers and the
		 * retrieval zones
		 */

		final double DISTANCE_TO_STOP_GEAR = 7.5/12.0;//FEET
		final double FRONT_LIFT_DISTANCE = 6.4;
		final double FRONT_LIFT_TIME = 2.5;
		final double SIDE_LIFT_PREP_DISTANCE = 7.2;
		final double SIDE_LIFT_PREP_TIME = 3.0;
		final double BLUE_X_DISTANCE_TO_HOPPER = -7.0;//place holder
		final double RED_X_DISTANCE_TO_HOPPER = -7.0;//place holder
		final double BLUE_STRAFE_DISTANCE_TO_HOPPER = -6.0;//place holder
		final double RED_STRAFE_DISTANCE_FROM_HOPPER = -0.50;
		final double RED_STRAFE_DISTANCE_TO_HOPPER = 6.0;//place holder
		final double BLUE_HOPPER_DISTANCE_TO_BOILER = 5;//place holder
		final double RED_HOPPER_DISTANCE_TO_BOILER = 5.5;//place holder

		final double ARM_DEPOSIT_ANGLE = 0.0;
		final double DEPOSITING_GEAR_TIME = .3;

		final double BLUE_HOPPER_X = 18.9;
		final double BLUE_HOPPER_Y = -12.0;
		final double RED_HOPPER_X = -18.9;
		final double RED_HOPPER_Y = -12.0;

		final double RED_SIDE_GEARLIFT_X = -15.0;
		final double RED_MIDDLE_GEARLIFT_X = -18.2;

		final double BLUE_SIDE_GEARLIFT_X = 15.0;
		final double BLUE_MIDDLE_GEARLIFT_X = 18.2;

		final double GEARLIFT_1_Y = -3.3;
		final double GEARLIFT_2_Y = 0.0;
		final double GEARLIFT_3_Y = 3.3;

		final double TIME_TO_COLLECT_HOPPER = 1.0;

		final int nothing = 0;
		final int redAuto2_ScoreGear = 1;
		final int redAuto2_GearandZone = 2;
		final int redAuto3_GearandZone = 3;
		final int redAuto1_GearandZone = 4;
		final int redAuto1_ShootFromHopper = 5;
		final int redAuto2_ShootFromHopper = 6;
		final int redAuto1_GearandShootFromHopper = 7;
		final int redAuto1_HopperandShootFromBoiler = 8;

		final int blueAuto2_ScoreGear = 21;
		final int blueAuto2_GearandZone = 22;
		final int blueAuto3_GearandZone = 23;
		final int blueAuto1_GearandZone = 24;
		final int blueAuto1_ShootFromHopper = 25;
		final int blueAuto2_ShootFromHopper = 26;
		final int blueAuto1_GearandShootFromHopper = 27;
		final int blueAuto1_HopperandShootFromBoiler = 28;

		final int justShootBlue = 35;
		final int justShootRed = 15;

		final int testGearDeposit = 40;
		final int testAutoRotate = 41;
		final int testFwd = 42;
		final int testCoordinateDriveSide = 43;

		alliance = AutoSelector.alliance;
		autoMode = AutoSelector.autoMode;
		autoModeToExecute = AutoSelector.autoModeToExecute;
		
		switch (autoModeToExecute) {
		case nothing:
			break;
		case redAuto2_ScoreGear:
			SmartDashboard.putString("auto", "1");
			mecanumNavigation.setStartingPosition(RED_STARTING_X, MODE_2_STARTING_Y, 0);
			
			Robot.systemTimer.reset();

			while (!mecanumDrive.driveStraight(FRONT_LIFT_DISTANCE) && (Robot.systemTimer.get() < FRONT_LIFT_TIME));

			mecanumDrive.driveInit = true;

			gearCollection.autoGearDeposit(DEPOSITING_GEAR_TIME);

			Robot.systemTimer.reset();
			while (!mecanumDrive.driveStraight(-2.0) && (Robot.systemTimer.get() < 3))
				;

			// mecanumDrive.driveInit = true;

			Robot.systemTimer.reset();

			break;

		case redAuto3_GearandZone:
			mecanumNavigation.setStartingPosition(RED_STARTING_X, MODE_3_STARTING_Y, 0);
			Robot.systemTimer.reset();

			while (!mecanumDrive.driveStraight(SIDE_LIFT_PREP_DISTANCE) && (Robot.systemTimer.get() < 2))
				;// drive forward

			mecanumDrive.driveInit = true;

			gearCollection.setArmPosition(ARM_DEPOSIT_ANGLE);// lower arm

			Robot.systemTimer.reset();
			while (!mecanumDrive.rotateToHeading(-60) && (Robot.systemTimer.get() < 1.5))
				;// rotate to face lift

			Robot.systemTimer.reset();
			while (!mecanumDrive.driveStraight(2.5) && (Robot.systemTimer.get() < 1.5))
				;// drive to lift

			mecanumDrive.driveInit = true;

			gearCollection.autoGearDeposit(DEPOSITING_GEAR_TIME);// deposit gear

			while (!mecanumDrive.driveStraight(-4.0) && (Robot.systemTimer.get() < 2))
				;// drive from lift

			mecanumDrive.driveInit = true;

			Robot.systemTimer.reset();
			break;

		case redAuto1_GearandZone:

			mecanumNavigation.setStartingPosition(RED_STARTING_X, MODE_1_STARTING_Y, 0);
			Robot.systemTimer.reset();

			while (!mecanumDrive.driveStraight(SIDE_LIFT_PREP_DISTANCE) && (Robot.systemTimer.get() < 3))
				;// drive forward

			mecanumDrive.driveInit = true;

			gearCollection.setArmPosition(ARM_DEPOSIT_ANGLE);// lower arm

			Robot.systemTimer.reset();
			while (!mecanumDrive.rotateToHeading(60) && (Robot.systemTimer.get() < 0.5))
				;// rotate to face lift

			Robot.systemTimer.reset();
			while (!mecanumDrive.driveStraight(2.5) && (Robot.systemTimer.get() < 1.5))
				;// drive to lift

			mecanumDrive.driveInit = true;

			gearCollection.autoGearDeposit(1);// deposit gear

			while (!mecanumDrive.driveStraight(-4.0) && (Robot.systemTimer.get() < 2))
				;// drive from lift

			mecanumDrive.driveInit = true;

			Robot.systemTimer.reset();
			break;

		case justShootRed:

			mecanumNavigation.setStartingPosition(RED_STARTING_X, 0, -135);
			shooter.setFlywheelState(true);
			SmartDashboard.putNumber("AUTO timer", Robot.systemTimer.get());
			Robot.systemTimer.delay(1);
			
			Robot.systemTimer.reset();
			while (Robot.systemTimer.get() < 10) {
				SmartDashboard.putNumber("AUTO timer", Robot.systemTimer.get());
				shooter.setFeederState(true);
				//shooter.startFlywheel();
			}
			//shooter.stopFlywheel();
			shooter.allStop();
			break;
			
		case redAuto1_HopperandShootFromBoiler:
			mecanumNavigation.setStartingPosition(RED_STARTING_X, 0, 0);
			Robot.systemTimer.reset();
			while(!autoDrive.driveStraight(RED_X_DISTANCE_TO_HOPPER)&&Robot.systemTimer.get()<3);
			autoDrive.driveInit = true;
			Robot.systemTimer.reset();
			while(!autoDrive.strafeStraight(RED_STRAFE_DISTANCE_TO_HOPPER)&&Robot.systemTimer.get()<0.5);
			autoDrive.driveInit = true;
			shooter.setFlywheelState(true);
			Robot.systemTimer.delay(TIME_TO_COLLECT_HOPPER);
			Robot.systemTimer.reset();
			while(!autoDrive.strafeStraight(RED_STRAFE_DISTANCE_FROM_HOPPER)&&Robot.systemTimer.get()<0.5);
			autoDrive.driveInit = true;
			autoBall.ballCollector.set(autoBall.COLLECTOR_SPEED);
			Robot.systemTimer.reset();
			while(!autoDrive.driveStraight(RED_HOPPER_DISTANCE_TO_BOILER)&&Robot.systemTimer.get()<2.0);
			autoDrive.driveInit = true;
			Robot.systemTimer.reset();
			
			Robot.systemTimer.reset();
			while(Robot.systemTimer.get()<7)
			{
				shooter.setFeederState(true);
				//shooter.startFlywheel();
			}

			autoBall.ballCollector.set(0);
			shooter.allStop();
			//shooter.stopFlywheel();
			break;
			
		case redAuto1_GearandShootFromHopper:
			
			mecanumNavigation.setStartingPosition(RED_STARTING_X, MODE_1_STARTING_Y, 0);
			Robot.systemTimer.reset();

			while (!mecanumDrive.driveStraight(SIDE_LIFT_PREP_DISTANCE) && (Robot.systemTimer.get() < 3))
				;// drive forward

			mecanumDrive.driveInit = true;

			gearCollection.setArmPosition(ARM_DEPOSIT_ANGLE);// lower arm

			Robot.systemTimer.reset();
			while (!mecanumDrive.rotateToHeading(60) && (Robot.systemTimer.get() < 0.5))
				;// rotate to face lift

			Robot.systemTimer.reset();
			while (!mecanumDrive.driveStraight(2.5) && (Robot.systemTimer.get() < 1.0))
				;// drive to lift

			mecanumDrive.driveInit = true;

			gearCollection.autoGearDeposit(.4);// deposit gear


			shooter.setFlywheelState(true);
			
			while (!mecanumDrive.driveStraight(-6.0) && (Robot.systemTimer.get() < 1.5))
				;// drive from lift

			mecanumDrive.driveInit = true;

			Robot.systemTimer.reset();
			
			while (!mecanumDrive.rotateToHeading(180) && (Robot.systemTimer.get() < 0.5))
				;
			
			Robot.systemTimer.reset();
			
			while(!autoDrive.strafeStraight(RED_STRAFE_DISTANCE_TO_HOPPER+3)&&Robot.systemTimer.get()<1);
			autoDrive.driveInit = true;
			shooter.setFlywheelState(true);
			Robot.systemTimer.delay(TIME_TO_COLLECT_HOPPER);
			Robot.systemTimer.reset();
			while(!autoDrive.strafeStraight(RED_STRAFE_DISTANCE_FROM_HOPPER)&&Robot.systemTimer.get()<0.5);
			autoDrive.driveInit = true;
			autoBall.ballCollector.set(autoBall.COLLECTOR_SPEED);
			Robot.systemTimer.reset();
			while(!autoDrive.driveStraight(RED_HOPPER_DISTANCE_TO_BOILER)&&Robot.systemTimer.get()<2.0);
			autoDrive.driveInit = true;
			Robot.systemTimer.reset();
			
			Robot.systemTimer.reset();
			while(Robot.systemTimer.get()<4)
			{
				shooter.setFeederState(true);
				//shooter.startFlywheel();
			}

			autoBall.ballCollector.set(0);
			shooter.allStop();
			//shooter.stopFlywheel();
			break;
			
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// blue
		case blueAuto2_ScoreGear:
			mecanumNavigation.setStartingPosition(RED_STARTING_X, MODE_2_STARTING_Y, 180);
			gearCollection.setArmPosition(ARM_DEPOSIT_ANGLE);
			Robot.systemTimer.reset();

			while (!mecanumDrive.driveStraight(FRONT_LIFT_DISTANCE) && (Robot.systemTimer.get() < 3))
				;

			mecanumDrive.driveInit = true;

			gearCollection.autoGearDeposit(DEPOSITING_GEAR_TIME);

			Robot.systemTimer.reset();
			while (!mecanumDrive.driveStraight(-2.0) && (Robot.systemTimer.get() < 3))
				;

			// mecanumDrive.driveInit = true;

			Robot.systemTimer.reset();

			break;

		case blueAuto3_GearandZone:
			mecanumNavigation.setStartingPosition(BLUE_STARTING_X, MODE_3_STARTING_Y, 180);
			Robot.systemTimer.reset();

			while (!mecanumDrive.driveStraight(SIDE_LIFT_PREP_DISTANCE) && (Robot.systemTimer.get() < 3))
				;// drive forward

			mecanumDrive.driveInit = true;

			gearCollection.setArmPosition(ARM_DEPOSIT_ANGLE);// lower arm

			Robot.systemTimer.reset();
			while (!mecanumDrive.rotateToHeading(-120) && (Robot.systemTimer.get() < 1.5))
				;// rotate to face lift

			Robot.systemTimer.reset();
			while (!mecanumDrive.driveStraight(2.5) && (Robot.systemTimer.get() < 1.5))
				;// drive to lift

			mecanumDrive.driveInit = true;

			gearCollection.autoGearDeposit(DEPOSITING_GEAR_TIME);// deposit gear

			while (!mecanumDrive.driveStraight(-2.0) && (Robot.systemTimer.get() < 2))
				;// drive from lift

			mecanumDrive.driveInit = true;

			Robot.systemTimer.reset();
			break;

		case blueAuto1_GearandZone:
			mecanumNavigation.setStartingPosition(BLUE_STARTING_X, MODE_1_STARTING_Y, 180);
			Robot.systemTimer.reset();

			while (!mecanumDrive.driveStraight(SIDE_LIFT_PREP_DISTANCE) && (Robot.systemTimer.get() < 3))
				;// drive forward

			mecanumDrive.driveInit = true;

			gearCollection.setArmPosition(ARM_DEPOSIT_ANGLE);// lower arm

			Robot.systemTimer.reset();
			while (!mecanumDrive.rotateToHeading(120) && (Robot.systemTimer.get() < 1.5))
				;// rotate to face lift

			Robot.systemTimer.reset();
			while (!mecanumDrive.driveStraight(2.5) && (Robot.systemTimer.get() < 1.5))
				;// drive to lift

			mecanumDrive.driveInit = true;

			gearCollection.autoGearDeposit(DEPOSITING_GEAR_TIME);// deposit gear

			while (!mecanumDrive.driveStraight(-2.0) && (Robot.systemTimer.get() < 2))
				;// drive from lift

			mecanumDrive.driveInit = true;

			Robot.systemTimer.reset();
			break;
			
		case justShootBlue:

			mecanumNavigation.setStartingPosition(RED_STARTING_X, 0, -135);
			shooter.setFlywheelState(true);
			SmartDashboard.putNumber("AUTO timer", Robot.systemTimer.get());
			Robot.systemTimer.delay(1);
			Robot.systemTimer.reset();
			while (Robot.systemTimer.get() < 10) {
				SmartDashboard.putNumber("AUTO timer", Robot.systemTimer.get());
				//shooter.startFlywheel();
				shooter.setFeederState(true);
			}
			shooter.allStop();
			//shooter.stopFlywheel();
			break;
			
		case blueAuto1_HopperandShootFromBoiler:
			Robot.systemTimer.reset();
			while(!autoDrive.driveStraight(BLUE_X_DISTANCE_TO_HOPPER)&&Robot.systemTimer.get()<3);
			autoDrive.driveInit = true;
			Robot.systemTimer.reset();
			while(!autoDrive.strafeStraight(BLUE_STRAFE_DISTANCE_TO_HOPPER)&&Robot.systemTimer.get()<2);
			autoDrive.driveInit = true;
			shooter.setFlywheelState(true);
			Robot.systemTimer.delay(TIME_TO_COLLECT_HOPPER);
			Robot.systemTimer.reset();
			while(!autoDrive.driveStraight(BLUE_HOPPER_DISTANCE_TO_BOILER)&&Robot.systemTimer.get()<2);
			autoDrive.driveInit = true;
			Robot.systemTimer.reset();
			while(Robot.systemTimer.get()<7)
			{
				shooter.setFeederState(true);
				//shooter.startFlywheel();
			}
			shooter.allStop();
			//shooter.stopFlywheel();
			break;
			
			
			
			
		/*
		 * case redAuto2_GearandZone:
		 * mecanumNavigation.setStartingPosition(RED_STARTING_X,
		 * MODE_2_STARTING_Y, 0); Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveStraight(3) && (Robot.systemTimer.get() > 3)) ;
		 * 
		 * Robot.systemTimer.reset(); // Robot.systemTimer.reset(); //
		 * while(!mecanumDrive.gearAlign(Robot.gearPegOffset) && //
		 * (Robot.systemTimer.get()>3));
		 * 
		 * gearCollection.autoGearDeposit(.5); gearCollection.setArmPosition(0);
		 * Robot.systemTimer.reset(); while (!mecanumDrive.driveStraight(-2) &&
		 * (Robot.systemTimer.get() > 3)) ;
		 * 
		 * while (!mecanumDrive.rotateToHeading(-60)) ;
		 * 
		 * Robot.systemTimer.reset(); while (!mecanumDrive.driveStraight(5) &&
		 * (Robot.systemTimer.get() > 3)) ; Robot.systemTimer.reset(); while
		 * (!mecanumDrive.rotateToHeading(0)) ;
		 * 
		 * Robot.systemTimer.reset(); while (!mecanumDrive.driveStraight(2) &&
		 * (Robot.systemTimer.get() > 3)) ; Robot.systemTimer.reset(); break;
		 * 
		 * 
		 * case redAuto1_ShootFromHopper:
		 * 
		 * mecanumNavigation.setStartingPosition(RED_STARTING_X,
		 * MODE_1_STARTING_Y, 180); Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(RED_HOPPER_X, RED_HOPPER_Y, 180)) ;
		 * Robot.systemTimer.reset(); while
		 * (!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset)) ;
		 * Robot.systemTimer.reset(); autoShooter.autoShoot();
		 * 
		 * break;
		 * 
		 * case redAuto2_ShootFromHopper:
		 * mecanumNavigation.setStartingPosition(RED_STARTING_X,
		 * MODE_2_STARTING_Y, 180); Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(RED_HOPPER_X, RED_HOPPER_Y, 180)) ;
		 * Robot.systemTimer.reset(); while
		 * (!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset)) ;
		 * Robot.systemTimer.reset(); autoShooter.autoShoot();
		 * 
		 * break;
		 * 
		 * case redAuto1_GearandShootFromHopper:
		 * mecanumNavigation.setStartingPosition(RED_STARTING_X,
		 * MODE_1_STARTING_Y, 0); Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(-16.3, -6.7, 0)) ;
		 * Robot.systemTimer.reset(); while (!mecanumDrive.rotateToHeading(60))
		 * ;
		 * 
		 * // while(!mecanumDrive.gearAlign(Robot.gearPegOffset));//place //
		 * holder Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(RED_SIDE_GEARLIFT_X, GEARLIFT_1_Y,
		 * 60)) ;
		 * 
		 * gearCollection.autoGearDeposit(1); gearCollection.setArmPosition(0);
		 * Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(RED_HOPPER_X, RED_HOPPER_Y, 180)) ;
		 * Robot.systemTimer.reset(); while
		 * (!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset)) ;
		 * Robot.systemTimer.reset(); autoShooter.autoShoot(); break;
		 * 
		 * case redAuto1_HopperandShootFromBoiler:
		 * mecanumNavigation.setStartingPosition(RED_STARTING_X,
		 * MODE_1_STARTING_Y, 180); Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(RED_HOPPER_X, RED_HOPPER_Y, 180)) ;
		 * 
		 * Timer.delay(TIME_TO_COLLECT_HOPPER); Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(-22.9, -9.2, -135)) ;
		 * Robot.systemTimer.reset(); while
		 * (!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset)) ;
		 * Robot.systemTimer.reset(); autoShooter.autoShoot();
		 * 
		 * break;
		 * /////////////////////////////////////////////////////////////////////
		 * //////////////////////////////////////////// // blue
		 * 
		 * 
		 * 
		 * case blueAuto1_ShootFromHopper:
		 * mecanumNavigation.setStartingPosition(BLUE_STARTING_X,
		 * MODE_1_STARTING_Y, 0); Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(BLUE_HOPPER_X, BLUE_HOPPER_Y, 0)) ;
		 * Robot.systemTimer.reset(); while
		 * (!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset)) ;
		 * Robot.systemTimer.reset(); autoShooter.autoShoot();
		 * 
		 * break;
		 * 
		 * case blueAuto2_ShootFromHopper:
		 * mecanumNavigation.setStartingPosition(BLUE_STARTING_X,
		 * MODE_2_STARTING_Y, 0); Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(BLUE_HOPPER_X, BLUE_HOPPER_Y, 0)) ;
		 * Robot.systemTimer.reset(); while
		 * (!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset)) ;
		 * Robot.systemTimer.reset(); autoShooter.autoShoot(); break;
		 * 
		 * case blueAuto1_GearandShootFromHopper:
		 * mecanumNavigation.setStartingPosition(BLUE_STARTING_X,
		 * MODE_1_STARTING_Y, 180); Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(16.3, -6.7, -120)) ;
		 * 
		 * while (!mecanumDrive.rotateToHeading(-120)) ;
		 * Robot.systemTimer.reset(); //
		 * while(!mecanumDrive.gearAlign(Robot.gearPegOffset));
		 * 
		 * gearCollection.autoGearDeposit(.5); gearCollection.setArmPosition(0);
		 * Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(BLUE_HOPPER_X, BLUE_HOPPER_Y, 0)) ;
		 * Robot.systemTimer.reset(); while
		 * (!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset)) ;
		 * 
		 * autoShooter.autoShoot(); break;
		 * 
		 * case blueAuto1_HopperandShootFromBoiler:
		 * mecanumNavigation.setStartingPosition(BLUE_STARTING_X,
		 * MODE_1_STARTING_Y, 0); while
		 * (!mecanumDrive.driveToCoordinate(BLUE_HOPPER_X, BLUE_HOPPER_Y, 0)) ;
		 * 
		 * Timer.delay(TIME_TO_COLLECT_HOPPER); Robot.systemTimer.reset(); while
		 * (!mecanumDrive.driveToCoordinate(22.9, -9.2, -45)) ;
		 * Robot.systemTimer.reset(); while
		 * (!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset)) ;
		 * 
		 * autoShooter.autoShoot(); break;
		 */
		case testGearDeposit:
			mecanumNavigation.setStartingPosition(0, 0, 0);
			Robot.systemTimer.reset();
			while (!mecanumDrive.driveToCoordinate(0, 10, 0))
				;

			gearCollection.autoGearDeposit(1);
			gearCollection.setArmPosition(0);

			break;

		case testAutoRotate:
			SmartDashboard.putString("auto", "switch");
			Robot.systemTimer.reset();
			while (!mecanumDrive.rotateToHeading(90))
				;
			

			/*
			 * Robot.systemTimer.delay(1); Robot.systemTimer.reset();
			 * while(!mecanumDrive.rotateToHeading(0)&&
			 * (Robot.systemTimer.get()>2)); Robot.systemTimer.delay(1);
			 * Robot.systemTimer.reset();
			 * while(!mecanumDrive.rotateToHeading(180) &&
			 * (Robot.systemTimer.get()>2)); Robot.systemTimer.delay(1);
			 * Robot.systemTimer.reset();
			 * while(!mecanumDrive.rotateToHeading(-180) &&
			 * (Robot.systemTimer.get()>2));
			 */ SmartDashboard.putString("auto", "done");
			break;

		case testFwd:
			SmartDashboard.putString("auto", "switch");
			Robot.systemTimer.reset();
			while ((!mecanumDrive.driveStraight(5)) && (Robot.systemTimer.get() < 5))
				;

			mecanumDrive.driveInit = true;

			Robot.systemTimer.reset();
			while ((!mecanumDrive.driveStraight(5)) && (Robot.systemTimer.get() < 5))
				;

			mecanumDrive.driveInit = true;

			SmartDashboard.putString("auto", "done");

			break;

		case testCoordinateDriveSide:
			Robot.systemTimer.reset();
			while (!mecanumDrive.driveToCoordinate(5, 0, 0) && (Robot.systemTimer.get() > 3))

				break;

		default:
			break;
		}
	}
}
