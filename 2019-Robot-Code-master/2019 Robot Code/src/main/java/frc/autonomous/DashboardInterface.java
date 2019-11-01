/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 * Add your docs here.
 */
public class DashboardInterface {

    
    //start position definitions
    public enum Position {LEFT, RIGHT, CENTER, kdefault}
    private Position startPosition = Position.kdefault;
    private String startPositionString = "kdefault";
    private Position targetPosition = Position.kdefault;
    private String targetPositionString = "kdefault";

    private static boolean DRIVE_OFF_PLATFORM = false;
    private static boolean DRIVE_PATH = false;
    private static boolean DEPLOY_HATCH = false;
    private static boolean DEPLOY_CARGO = false;

    public DashboardInterface() {

    }

    public Position getStartPosition(){
        return startPosition;
    }

    public Position getTargetPosition(){
        return targetPosition;
    }

    public boolean getDRIVE_OFF_PLATFORM(){
        return DRIVE_OFF_PLATFORM;
    }

    public boolean getDRIVE_PATH(){
        return DRIVE_PATH;
    }

    public boolean getDEPLOY_HATCH(){
        return DEPLOY_HATCH;
    }

    public boolean getDEPLOY_CARGO(){
        return DEPLOY_CARGO;
    }

    private void determinePositions() {
        if (Robot.leftJoystick.getRawButton(7)) {
            startPosition = Position.LEFT;
            startPositionString = "LEFT";
        } else if (Robot.leftJoystick.getRawButton(8)) {
            startPosition = Position.CENTER;
            startPositionString = "CENTER";
        } else if (Robot.leftJoystick.getRawButton(9)) {
            startPosition = Position.RIGHT;
            startPositionString = "RIGHT";
        }

        if (Robot.leftJoystick.getRawButton(10)) {
            targetPosition = Position.LEFT;
            targetPositionString = "LEFT";
        } else if (Robot.leftJoystick.getRawButton(11)) {
            targetPosition = Position.CENTER;
            targetPositionString = "CENTER";
        } else if (Robot.leftJoystick.getRawButton(12)) {
            targetPosition = Position.RIGHT;
            targetPositionString = "RIGHT";
        }
    }

    private void determinePlan() {
        if (Robot.rightJoystick.getRawButton(7)) {
            DRIVE_OFF_PLATFORM = true;
        } else if (Robot.rightJoystick.getRawButton(10)) {
            DRIVE_OFF_PLATFORM = false;
        }
        if (Robot.rightJoystick.getRawButton(8)) {
            DRIVE_PATH = true;
        } else if (Robot.rightJoystick.getRawButton(11)) {
            DRIVE_PATH = false;
        }
        if (Robot.rightJoystick.getRawButton(9) && Robot.rightJoystick.getRawButton(12)) {
            DEPLOY_CARGO = false;
            DEPLOY_HATCH = false;
        } else if (Robot.rightJoystick.getRawButton(9)) {
            DEPLOY_CARGO = true;
            DEPLOY_HATCH = false;
        } else if (Robot.rightJoystick.getRawButton(12)) {
            DEPLOY_CARGO = false;
            DEPLOY_HATCH = true;
        }
    }

    public void performMainProcessing() {
        determinePositions();
        determinePlan();
        updateTelemetry();
    }

    public void updateTelemetry(){
        SmartDashboard.putString("Starting Position", startPositionString);
        SmartDashboard.putBoolean("Drive off platform", DRIVE_OFF_PLATFORM);
        SmartDashboard.putBoolean("Drive Path",DRIVE_PATH);
        SmartDashboard.putBoolean("Deploy Hatch", DEPLOY_CARGO);
    }
}
