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
	static DigitalInput gearLimitSwitch;
	static Encoder armAngle;
	
	static boolean gotGear = false;
	
	static final double INTAKE_POWER = .7; //testing needed
	static final double OUTTAKE_POWER = -.4; //testing needed
	static final double OFF = 0;
	double armPivotDistancePerPulse = 90.0/142.5; //16/18 WAS USED FOR PRACTICE BOT
	
	static double desiredArmAngle = 0;
	
	public gearCollection(int pwm6, int pwm7, int dio7, int dio4, int dio5)
	{
		armMotor = new VictorSP(pwm6);
		gearIntake = new VictorSP(pwm7);
		gearLimitSwitch = new DigitalInput(dio7);
		armAngle = new Encoder(dio4,dio5);
		armAngle.setDistancePerPulse(armPivotDistancePerPulse); 
	}
	

	
	void performMainProcessing()
	{
		SmartDashboard.putNumber("Arm:", armAngle.getDistance());
		if(Robot.XBoxController.getRawButton(XBox.LB) == true) 
		{
			if(!(gearLimitSwitch.get() == true))
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
		ArmPID.commandedArmAngle = desiredAngle;
	}
	
	static void autoGearDeposit(double timeToRun)
	{
		gearIntake.set(OUTTAKE_POWER);
		
		Timer.delay(timeToRun-.05);
		setArmPosition(-75.0);
		gearIntake.set(OFF);

	}
}
