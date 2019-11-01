package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;

public class ArmPID extends Thread implements Runnable{
	
	static double angle;
	static double startang=0;	
	static double setpoint = startang;
	static double ang;
	
	
	public void run()
	{
		double offset;		
		
		double k=.025;
		double deadband=1; 
		double power;
		
		try
		{
			while(true)
			{
				if (Robot.XBoxController.getRawButton(XBox.Start))
				{
					gearCollection.armMotor.set(Robot.XBoxController.getRawAxis(XBox.LStickY));
				}
				else {
				angle=startang-(-1*gearCollection.armAngle.getDistance());
				
				offset = setpoint - angle;
				
				if(Math.abs(offset)>deadband)
				{
					power = k*offset;
				}
				else power=0;
				
				power+=Math.cos(angle)*.05; 
				gearCollection.armMotor.set(-power);
				if(Robot.XBoxController.getRawAxis(XBox.LStickY)>0.5)
					setpoint-=3; 
				else if(Robot.XBoxController.getRawAxis(XBox.LStickY)<-0.5)
					setpoint+=3;  
				
				if(Robot.XBoxController.getRawButton(XBox.Back)&&Robot.XBoxController.getRawButton(XBox.Start))
				{
					startang++;
				}
				else if(Robot.XBoxController.getRawButton(XBox.Back))
				{
					startang--;
				}
				
				
				if(setpoint>=0)
					setpoint=0;
				if(setpoint<=-85)
					setpoint=-85;
				if(Robot.XBoxController.getRawButton(XBox.Y))
					setpoint=0;
				if(Robot.XBoxController.getRawButton(XBox.X))
					setpoint=-85;
				
				SmartDashboard.putNumber("Arm Offset: ", offset);
				SmartDashboard.putNumber("Setpoint: ", setpoint);
				SmartDashboard.putNumber("Power: ", power);
				SmartDashboard.putNumber("Arm Angle: ", angle);
				SmartDashboard.putNumber("Encoder Value: ", gearCollection.armAngle.getDistance());
				SmartDashboard.putNumber("Arm Start Angle", startang);
				

			
				Timer.delay(0.05);
				
				
				}
			}

			}
		
		catch (Exception e)
		{
			
		}
	}

}