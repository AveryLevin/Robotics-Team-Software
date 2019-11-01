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
 //need to actually test and calibrate all the different coordinates




public class AutoCases {

	void autoModes() {
		//int automode = autoModeSelector.autonomousModeNumber;
		
		final double RED_STARTING_X = -25.8;//feet
		final double BLUE_STARTING_X = 25.8;//feet
		//these starting X coordinates are true for all mode numbers
		//each side only has one starting X coordinate
		
		final double MODE_1_STARTING_Y = -6.7;//feet
		final double MODE_2_STARTING_Y = 0;//feet
		final double MODE_3_STARTING_Y = 6.7;//feet
		//these starting Y coordinates are true for both alliance colors 
		//each mode only has one starting Y coordinate
		/*order of modes: 1, 2, 3, with 1 being the positions
		 * on both sides that are closest to the boilers
		 * while 3 positions are on sides that are closest 
		 * to the retrieval zones 
		 * 2 positions are the positions on both sides that are 
		 * in the middle between the boilers and the retrieval zones
		 */
		
		final double TIME_TO_COLLECT_BOILER = 3.0;
		
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
		
		final int testGearDeposit = 40;
		
		int autoMode = testGearDeposit;
		
		switch (autoMode) {
		case nothing:
			break;
		case redAuto2_ScoreGear:
			mecanumNavigation.setStartingPosition(RED_STARTING_X, MODE_2_STARTING_Y);
			while(!mecanumDrive.driveToCoordinate(-18.2, 0, 0));
			
			while(!mecanumDrive.gearAlign(Robot.gearPegOffset));
			
			gearCollection.autoGearDeposit(1);
			gearCollection.setArmPosition(0);
			break;

		case redAuto2_GearandZone:
			mecanumNavigation.setStartingPosition(RED_STARTING_X, MODE_2_STARTING_Y);
			while(!mecanumDrive.driveToCoordinate(-18.2, 0, 0));
			
			while(!mecanumDrive.gearAlign(Robot.gearPegOffset));
			
			gearCollection.autoGearDeposit(.5);
			gearCollection.setArmPosition(0);
			
			while(!mecanumDrive.driveToCoordinate(-21, 0, 0));
			
			while(!mecanumDrive.driveToCoordinate(-21, 6.7, 0));
			
			while(!mecanumDrive.driveToCoordinate(-15, 6.7, 0));
			
			break;

		case redAuto3_GearandZone:
			mecanumNavigation.setStartingPosition(RED_STARTING_X,MODE_3_STARTING_Y);
			while(!mecanumDrive.driveToCoordinate(-17.5,6.7,-60));
			
			//while(!mecanumDrive.rotateToHeading(-60));
			
			while(!mecanumDrive.gearAlign(Robot.gearPegOffset));
			while(!mecanumDrive.driveToCoordinate(-15,3.3,-60));
			
			gearCollection.autoGearDeposit(1);
			gearCollection.setArmPosition(0);
			break;

		case redAuto1_GearandZone:

			mecanumNavigation.setStartingPosition(RED_STARTING_X,MODE_1_STARTING_Y);
			while(!mecanumDrive.driveToCoordinate(-16.3,-6.7,60));
			
			//while(!mecanumDrive.rotateToHeading(60));
			
			while(!mecanumDrive.gearAlign(Robot.gearPegOffset));
			
			while(!mecanumDrive.driveToCoordinate(-15,-3.3,60));
			
			gearCollection.autoGearDeposit(1);
			gearCollection.setArmPosition(0);
			
			break;

		case redAuto1_ShootFromHopper:
			
			mecanumNavigation.setStartingPosition(RED_STARTING_X,MODE_1_STARTING_Y);
			while(!mecanumDrive.driveToCoordinate(-18.9,-12,180));
			
			while(!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset));
			
			BallShooter.autoShoot();
			
			break;

		case redAuto2_ShootFromHopper:
			mecanumNavigation.setStartingPosition(RED_STARTING_X, MODE_2_STARTING_Y);
			while(!mecanumDrive.driveToCoordinate(-18.9,-12,180));
			
			while(!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset));
			
			BallShooter.autoShoot();

			break;
			
		case redAuto1_GearandShootFromHopper:
			mecanumNavigation.setStartingPosition(RED_STARTING_X,MODE_1_STARTING_Y);
			while(!mecanumDrive.driveToCoordinate(-16.3,-6.7,60));
			
			//while(!mecanumDrive.rotateToHeading(60));
			
			while(!mecanumDrive.gearAlign(Robot.gearPegOffset));//place holder
			
			while(!mecanumDrive.driveToCoordinate(-15,-3.3,60));
			
			gearCollection.autoGearDeposit(1);
			gearCollection.setArmPosition(0);
			
			while(!mecanumDrive.driveToCoordinate(-18.9,-12,180));
			
			while(!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset));
			
			BallShooter.autoShoot();
			break;
			
		case redAuto1_HopperandShootFromBoiler:
			mecanumNavigation.setStartingPosition(RED_STARTING_X,MODE_1_STARTING_Y);
			while(!mecanumDrive.driveToCoordinate(-18.9,-12,180));
			
			Timer.delay(TIME_TO_COLLECT_BOILER);
			
			while(!mecanumDrive.driveToCoordinate(-22.9,-9.2,-135));
			
			while(!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset));
			
			BallShooter.autoShoot();
			
			break;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//blue
		
		case blueAuto2_ScoreGear:
			mecanumNavigation.setStartingPosition(BLUE_STARTING_X, MODE_2_STARTING_Y);
			while(!mecanumDrive.driveToCoordinate(15, 0, 180));
			
			while(!mecanumDrive.gearAlign(Robot.gearPegOffset));
			
			gearCollection.autoGearDeposit(.5);
			gearCollection.setArmPosition(0);
			
			break;
			
		case blueAuto2_GearandZone:
			mecanumNavigation.setStartingPosition(BLUE_STARTING_X, MODE_2_STARTING_Y);
			while(!mecanumDrive.driveToCoordinate(15, 0, 180));
			
			while(!mecanumDrive.gearAlign(Robot.gearPegOffset));
			
			gearCollection.autoGearDeposit(.5);
			gearCollection.setArmPosition(0);
			
			while(!mecanumDrive.driveToCoordinate(16.3, 0, 180));
			
			while(!mecanumDrive.driveToCoordinate(17.5, 0, 180));
			
			while(!mecanumDrive.driveToCoordinate(15, 6.7, 180));
			break;
			
		case blueAuto3_GearandZone:
			mecanumNavigation.setStartingPosition(BLUE_STARTING_X, MODE_3_STARTING_Y);
			
			while(!mecanumDrive.driveToCoordinate(17.5, 6.7, -120));
			
			//while(!mecanumDrive.rotateToHeading(-120));
			
			while(!mecanumDrive.gearAlign(Robot.gearPegOffset));
			
			while(!mecanumDrive.driveToCoordinate(15,3.3,-120));
			
			gearCollection.autoGearDeposit(.5);
			gearCollection.setArmPosition(0);
			break;
			
		case blueAuto1_GearandZone:
			mecanumNavigation.setStartingPosition(BLUE_STARTING_X,MODE_1_STARTING_Y);
			
			while(!mecanumDrive.driveToCoordinate(16.3,-6.7,120));
			
			//while(!mecanumDrive.rotateToHeading(120));
			
			while(!mecanumDrive.gearAlign(Robot.gearPegOffset));
			
			while(!mecanumDrive.driveToCoordinate(15,3.3,-120));
			
			gearCollection.autoGearDeposit(.5);
			gearCollection.setArmPosition(0);
			break;
			
		case blueAuto1_ShootFromHopper:
			mecanumNavigation.setStartingPosition(BLUE_STARTING_X,MODE_1_STARTING_Y);
			
			while(!mecanumDrive.driveToCoordinate(18.9,-12,0));
			
			while(!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset));
			
			BallShooter.autoShoot();
			
			break;
			
		case blueAuto2_ShootFromHopper:
			mecanumNavigation.setStartingPosition(BLUE_STARTING_X,MODE_2_STARTING_Y);
			
			while(!mecanumDrive.driveToCoordinate(18.9,-12,0));
			
			while(!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset));
			
			BallShooter.autoShoot();
			break;
			
		case blueAuto1_GearandShootFromHopper:
			mecanumNavigation.setStartingPosition(BLUE_STARTING_X,MODE_1_STARTING_Y);
			
			while(!mecanumDrive.driveToCoordinate(16.3,-6.7,-120));
			
			//while(!mecanumDrive.rotateToHeading(-120));
			
			while(!mecanumDrive.gearAlign(Robot.gearPegOffset));
			
			gearCollection.autoGearDeposit(.5);
			gearCollection.setArmPosition(0);
			
			while(!mecanumDrive.driveToCoordinate(18.9,-12,0));
			
			while(!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset));
			
			BallShooter.autoShoot();
			break;
			
		case blueAuto1_HopperandShootFromBoiler:
			mecanumNavigation.setStartingPosition(BLUE_STARTING_X,MODE_1_STARTING_Y);
			while(!mecanumDrive.driveToCoordinate(18.9,-12,0));
			
			Timer.delay(TIME_TO_COLLECT_BOILER);
			
			while(!mecanumDrive.driveToCoordinate(22.9,-9.2,-45));
			
			while(!mecanumDrive.visionBoilerAlignment(Robot.boilerOffset));
			
			BallShooter.autoShoot();
			break;
			
		case testGearDeposit:
			
			gearCollection.autoGearDeposit(1);
			gearCollection.setArmPosition(0);
			
			break;
			
		default:
			break;
		}
	}
}
