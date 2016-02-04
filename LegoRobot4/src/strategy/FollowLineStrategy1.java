package strategy;

import main.*;
import task.*;

public class FollowLineStrategy1 extends Strategy{

	public FollowLineStrategy1(Robot robot) {
		super(robot);
	}
	
	public Status execute() {
		new ArmCalibrate(robot).run();
		
		for (int i = 0; i < 1000; ++i) {
			new ArmSearchLine(robot).run();

			Thread armFollowThread = new Thread(new ArmFollowLine(robot));
			armFollowThread.start();
			
			Thread wheelFollowThread = new Thread(new WheelFollowLine(robot));
			wheelFollowThread.start();

			try {
				armFollowThread.join();
			} catch (Exception e) {
				return Status.FAIL;
			}
			
			wheelFollowThread.interrupt();
			robot.leftWheelMotor.stop();
			robot.rightWheelMotor.stop();
		}
		
		return Status.SUCCESS;
	}
}
