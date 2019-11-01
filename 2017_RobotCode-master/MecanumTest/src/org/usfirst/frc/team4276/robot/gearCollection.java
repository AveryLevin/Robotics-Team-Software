package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID;

public class gearCollection {

	static VictorSP armMotor;
	static VictorSP gearIntake;
	DigitalInput gearLimitSwitch;
	static Encoder armAngle;
	
	static final double INTAKE_POWER = .8; //testing needed
	static final double OUTTAKE_POWER = -.4; //testing needed
	static final double OFF = 0;
	
	static double desiredArmAngle = 0;
	
	public gearCollection(int pwm6, int pwm7, int dio14, int dio8, int dio9)
	{
		armMotor = new VictorSP(pwm6);
		gearIntake = new VictorSP(pwm7);
		gearLimitSwitch = new DigitalInput(dio14);
		armAngle = new Encoder(dio8,dio9);
		armAngle.setDistancePerPulse(1/497); //testing needed
	}
	

	
	void performMainProcessing()
	{
		if(Robot.XBoxController.getRawButton(XBox.LB) == true) 
		{
			if(gearLimitSwitch.get() == true)
			{
				Robot.XBoxController.setRumble(GenericHID.RumbleType.kLeftRumble, .5);
				gearIntake.set(OFF);

			}
			else
			{
				Robot.XBoxController.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
				gearIntake.set(INTAKE_POWER);

			}
		}
		
		else if(Robot.XBoxController.getRawButton(XBox.RB) == true) 
		{
			Robot.XBoxController.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
			gearIntake.set(OUTTAKE_POWER);

		}
		
		else
		{
			Robot.XBoxController.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
			gearIntake.set(OFF);

		}
	}

	static void setArmPosition(double desiredAngle)
	{
		ArmPID.setpoint = desiredAngle;
	}
	
	static void autoGearDeposit(double timeToRun)
	{
		gearIntake.set(OUTTAKE_POWER);
		setArmPosition(-45);
		Timer.delay(timeToRun-.05);
		gearIntake.set(OFF);

	}
}
