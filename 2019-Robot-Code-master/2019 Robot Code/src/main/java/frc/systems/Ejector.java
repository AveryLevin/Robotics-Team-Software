package frc.systems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.utilities.Xbox;

public class Ejector {
    private DoubleSolenoid ejectSol;
    double activateTime = 0.25;
    boolean isEjecting = false;
    private final Value kApart = DoubleSolenoid.Value.kReverse;
    private final Value kTogether = DoubleSolenoid.Value.kForward;

    public Ejector(int port1, int port2) {
        ejectSol = new DoubleSolenoid(port1, port2);
    }

    public void performMainProcessing() {
        if (Math.abs(Robot.xboxJoystick.getRawAxis(Xbox.RT)) > 0.2) {
            Robot.xboxJoystick.setRumble(RumbleType.kRightRumble, 0.3);
            eject();
        } else {
            Robot.xboxJoystick.setRumble(RumbleType.kRightRumble, 0.0);
            unject();
        }
        updateTelemetry();
    }

    public void eject() {
        isEjecting = true;
        ejectSol.set(kTogether);
        // ejectTime.setTimer(activateTime);

    }

    public void unject() {
        isEjecting = false;
        ejectSol.set(kApart);

    }

    public void updateTelemetry() {
        SmartDashboard.putBoolean("Ejecting?", isEjecting);
    }
}
