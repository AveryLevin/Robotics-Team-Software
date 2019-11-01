package org.usfirst.frc.team4276.robot;

public class autoShooter extends Thread implements Runnable {
	
	BallShooter autoShoot;
	
	public boolean autoFlywheelOn = false;
	public boolean autoFeederOn = false;
	
	public autoShooter(BallShooter autoFuel){
		
		autoShoot = autoFuel;
		
	}
	
	public void setFlywheelState(boolean state){
		autoFlywheelOn = state;
	}
	
	public void setFeederState(boolean state){
		autoFeederOn = state;
	}
	
	public void allStop(){
		autoFeederOn = false;
		autoFlywheelOn = false;
	}

	public void run(){
		try{
			while(true){
				if(autoFlywheelOn)
				{
					autoShoot.startFlywheel();
				}
				else{
					autoShoot.stopFlywheel();
				}
				if(autoFeederOn){
					
				}
				else{
					
				}
				
			}
		}
		catch(Exception autoShooterError)
		{
			
		}
	}
	
}
