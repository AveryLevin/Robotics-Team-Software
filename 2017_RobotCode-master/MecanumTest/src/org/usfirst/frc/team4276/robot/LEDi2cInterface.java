package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.I2C;

public class LEDi2cInterface {

	I2C wire;
	
	public LEDi2cInterface()
	{
		wire = new I2C(I2C.Port.kOnboard ,42);
	}
	
	void testI2C()
	{
		for(int i = 1; i<20; i++)
		{
		wire.write(42, i);
		}
	}
	
}
