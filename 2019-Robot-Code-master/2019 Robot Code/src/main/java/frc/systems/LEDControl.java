/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.systems;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
/**
 * Add your docs here.
 */
public class LEDControl {

    Relay enablePin;
    Relay intakePin;
    Relay ballPin;
    Relay shootingPin;
    private static LightMode currentMode = LightMode.DISABLE;
    Value kFalse = Relay.Value.kOff;
    Value kTrue = Relay.Value.kForward;

    public LEDControl(int ePort, int iPort, int bPort, int sPort) {

        enablePin = new Relay(ePort);
        intakePin = new Relay(iPort);
        ballPin = new Relay(bPort);
        shootingPin = new Relay(sPort);

    }

    public static enum LightMode {
        DISABLE, ENABLE, INTAKE, FULL, SHOOT
    }

    public void setMode(LightMode mode) {
        currentMode = mode;
    }

    public void performMainProcessing() {
        switch (currentMode) {
        case DISABLE:
            enablePin.set(kFalse);
            intakePin.set(kFalse);
            ballPin.set(kFalse);
            shootingPin.set(kFalse);
            break;
        case ENABLE:
            enablePin.set(kTrue);
            intakePin.set(kFalse);
            ballPin.set(kFalse);
            shootingPin.set(kFalse);
            break;
        case INTAKE:
            enablePin.set(kTrue);
            intakePin.set(kTrue);
            ballPin.set(kFalse);
            shootingPin.set(kFalse);
            break;
        case FULL:
            enablePin.set(kTrue);
            intakePin.set(kFalse);
            ballPin.set(kTrue);
            shootingPin.set(kFalse);
            break;
        case SHOOT:
            enablePin.set(kTrue);
            intakePin.set(kFalse);
            ballPin.set(kFalse);
            shootingPin.set(kTrue);
            break;
        default:
            break;
        }
    }
}
