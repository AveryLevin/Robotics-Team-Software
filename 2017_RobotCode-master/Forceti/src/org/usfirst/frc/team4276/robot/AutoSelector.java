package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class AutoSelector extends Thread implements Runnable {

	SendableChooser<Integer> selectionModeChooser;
	SendableChooser<Integer> allianceChooser;
	SendableChooser<Integer> autoModeChooser;

	// Selection modes
	private final int COMMIT_MODE = 0;
	private final int EDIT_MODE = 1;
	private String[] selectionModeArray = new String[2];

	// Alliances
	private final int RED = 0;
	private final int BLUE = 1;
	private String[] allianceArray = new String[2];

	// Auto modes
	private final int NOTHING = 0;
	private final int POS_2_GEAR = 1;
	private final int POS_2_GEAR_AND_ZONE = 2;
	private final int POS_3_GEAR_AND_ZONE = 3;
	private final int POS_1_GEAR_AND_ZONE = 4;
	private final int POS_1_SHOOT_FROM_HOPPER = 5;
	private final int POS_2_SHOOT_FROM_HOPPER = 6;
	private final int POS_1_GEAR_AND_SHOOT_FROM_HOPPER = 7;
	private final int POS_1_HOPPER_AND_SHOOT_FROM_BOILER = 8;
	private final int SHOOT_FROM_BOILER = 15;
	private String[] autoModeArray = new String[16];

	// Test modes
	//private final int TEST_GEAR_DEPOSIT = 40;
	//private final int TEST_AUTO_ROTATE = 41;
	//private final int TEST_FWD = 42;
	//private final int TEST_COORDINATE_DRIVE_SIDE = 43;

	private int selectionMode = COMMIT_MODE;
	static int alliance = 0;
	static int autoMode = 0;
	static int autoModeToExecute;
	private final int ALLIANCE_OFFSET = 20;

	public AutoSelector() {
		selectionModeArray[COMMIT_MODE] = "Commit mode";
		selectionModeArray[EDIT_MODE] = "Edit mode";

		allianceArray[RED] = "Red";
		allianceArray[BLUE] = "Blue";

		autoModeArray[NOTHING] = "Nothing";
		autoModeArray[POS_2_GEAR] = "Center: gear only";
		autoModeArray[POS_2_GEAR_AND_ZONE] = "Not applicable 1";
		autoModeArray[POS_3_GEAR_AND_ZONE] = "Feeder: gear only";
		autoModeArray[POS_1_GEAR_AND_ZONE] = "Boiler: gear only";
		autoModeArray[POS_1_SHOOT_FROM_HOPPER] = "Boiler: shoot from hopper";
		autoModeArray[POS_2_SHOOT_FROM_HOPPER] = "Center: shoot from hopper";
		autoModeArray[POS_1_GEAR_AND_SHOOT_FROM_HOPPER] = "Boiler: gear + shoot from hopper";
		autoModeArray[POS_1_HOPPER_AND_SHOOT_FROM_BOILER] = "Boiler: hopper + shoot from boiler";
		autoModeArray[SHOOT_FROM_BOILER] = "Corner: shoot from boiler";

		selectionModeChooser = new SendableChooser<Integer>();
		selectionModeChooser.addDefault(selectionModeArray[COMMIT_MODE], COMMIT_MODE);
		selectionModeChooser.addObject(selectionModeArray[EDIT_MODE], EDIT_MODE);
		SmartDashboard.putData("Commit Selections", selectionModeChooser);

		allianceChooser = new SendableChooser<Integer>();
		allianceChooser.addDefault(allianceArray[RED], RED);
		allianceChooser.addObject(allianceArray[BLUE], BLUE);
		SmartDashboard.putData("Alliance Selection", allianceChooser);

		autoModeChooser = new SendableChooser<Integer>();
		autoModeChooser.addDefault(autoModeArray[NOTHING], NOTHING);
		autoModeChooser.addObject(autoModeArray[POS_2_GEAR], POS_2_GEAR);
		autoModeChooser.addObject(autoModeArray[POS_2_GEAR_AND_ZONE], POS_2_GEAR_AND_ZONE);
		autoModeChooser.addObject(autoModeArray[POS_3_GEAR_AND_ZONE], POS_3_GEAR_AND_ZONE);
		autoModeChooser.addObject(autoModeArray[POS_1_GEAR_AND_ZONE], POS_1_GEAR_AND_ZONE);
		autoModeChooser.addObject(autoModeArray[POS_1_SHOOT_FROM_HOPPER], POS_1_SHOOT_FROM_HOPPER);
		autoModeChooser.addObject(autoModeArray[POS_2_SHOOT_FROM_HOPPER], POS_2_SHOOT_FROM_HOPPER);
		autoModeChooser.addObject(autoModeArray[POS_1_GEAR_AND_SHOOT_FROM_HOPPER], POS_1_GEAR_AND_SHOOT_FROM_HOPPER);
		autoModeChooser.addObject(autoModeArray[POS_1_HOPPER_AND_SHOOT_FROM_BOILER], POS_1_HOPPER_AND_SHOOT_FROM_BOILER);
		autoModeChooser.addObject(autoModeArray[SHOOT_FROM_BOILER], SHOOT_FROM_BOILER);
		SmartDashboard.putData("Auto Mode Selection", autoModeChooser);
	}

	public void run() {
		try{
		while (true) {
			if (selectionMode == COMMIT_MODE) {
				selectionMode = (int) selectionModeChooser.getSelected();
			} else {
				selectionMode = (int) selectionModeChooser.getSelected();
				alliance = (int) allianceChooser.getSelected();
				autoMode = (int) autoModeChooser.getSelected();
				autoModeToExecute = autoMode + alliance * ALLIANCE_OFFSET;
			}
			SmartDashboard.putString("Selection mode", selectionModeArray[selectionMode]);
			SmartDashboard.putString("Alliance color", allianceArray[alliance]);
			SmartDashboard.putString("Auto mode", autoModeArray[autoMode]);
			SmartDashboard.putNumber("Auto", autoModeToExecute);
			
			Robot.systemTimer.delay(0.1);
			SmartDashboard.putBoolean("AutoSelector Error", false);
		}
		}
		catch(Exception autoSelectorError)
		{

			SmartDashboard.putBoolean("AutoSelector Error", true);
			SmartDashboard.putString("Auto ERROR", autoSelectorError.getMessage());
		}
		
	}

}
