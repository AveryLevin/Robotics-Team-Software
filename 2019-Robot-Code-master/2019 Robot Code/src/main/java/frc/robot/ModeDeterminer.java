/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.utilities.Xbox;

/**
 * Add your docs here.
 */
public class ModeDeterminer {

    private boolean modeInit = true;

    public ModeDeterminer() {

    }

    public enum Mode {
        DISABLED, IDLE_BALL, COLLECT_BALL, REVERSE_BALL, LO_BALL, HI_BALL, IDLE_HATCH, COLLECT_HATCH, EJECT_HATCH
    }

    public void performMainProcessing() {

        Mode currentMode = Mode.DISABLED;

        if (Robot.xboxJoystick.getRawButton(Xbox.A)) {
            currentMode = Mode.REVERSE_BALL;
        } else if (Robot.xboxJoystick.getRawButton(Xbox.Y)) {
            currentMode = Mode.HI_BALL;
        } else if (Robot.xboxJoystick.getRawButton(Xbox.B)) {
            currentMode = Mode.LO_BALL;
        } else if (Robot.xboxJoystick.getRawButton(Xbox.LB)) {
            currentMode = Mode.COLLECT_BALL;
        } else if (Robot.xboxJoystick.getRawAxis(Xbox.LT) > 0.5) {
            currentMode = Mode.IDLE_HATCH;
        } else if (Robot.xboxJoystick.getRawAxis(Xbox.RT) > 0.5) {// NEEDS TOGGLER
            if (currentMode == Mode.COLLECT_HATCH)
                currentMode = Mode.EJECT_HATCH;
            else
                currentMode = Mode.COLLECT_HATCH;
        } else if (!Robot.isEnabled) {
            currentMode = Mode.DISABLED;
        }

        switch (currentMode) {
        case DISABLED:
            if (modeInit) {
                modeInit = false;
            }
            // set Hatch mech to Lock
            Robot.mBallLift.stop();
            break;
        case IDLE_BALL:
            if (modeInit) {
                Robot.mArmPivot.commandSetpoint(Robot.mArmPivot.UP_SETPOINT);
                modeInit = false;
            }
            // set Hatch mech to Aim
            Robot.mBallLift.stop();
            break;
        case COLLECT_BALL:
            if (modeInit) {
                Robot.mArmPivot.commandSetpoint(Robot.mArmPivot.CARGO_IN_SETPOINT);
                modeInit = false;
            }
            // set Hatch mech to Aim
            Robot.mBallLift.collect();
            break;
        case REVERSE_BALL:
            if (modeInit) {
                Robot.mArmPivot.commandSetpoint(Robot.mArmPivot.UP_SETPOINT);
                modeInit = false;
            }
            // set Hatch mech to Aim
            Robot.mBallLift.reverse();
            break;
        case LO_BALL:
            if (modeInit) {
                modeInit = false;
            }
            // set Hatch mech to Aim
            Robot.mBallLift.lowScore();
            break;
        case HI_BALL:
            if (modeInit) {
                modeInit = false;
            }
            // set Hatch mech to Aim
            Robot.mBallLift.highScore();
            break;
        case IDLE_HATCH:
            if (modeInit) {
                Robot.mArmPivot.commandSetpoint(Robot.mArmPivot.DOWN_SETPOINT);
                modeInit = false;
            }
            // set Hatch mech to Aim
            Robot.mBallLift.stop();
            break;
        case COLLECT_HATCH:
            if (modeInit) {
                Robot.mArmPivot.commandSetpoint(Robot.mArmPivot.DOWN_SETPOINT);
                modeInit = false;
            }
            // set Hatch mech to Lock
            Robot.mBallLift.stop();
            break;
        case EJECT_HATCH:
            if (modeInit) {
                Robot.mArmPivot.commandSetpoint(Robot.mArmPivot.DOWN_SETPOINT);
                modeInit = false;
            }
            // set Hatch mech to Aim
            Robot.mBallLift.stop();
            break;

        }

    }
}
