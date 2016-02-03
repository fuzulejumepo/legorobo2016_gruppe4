package main;

import model.LegoRobot;
import model.RobotState;
import strategy.InitialStrategy;
import strategy.Strategy;

public class Parcours {

	public static void main(String[] args) {
		
		/* initialization */
		LegoRobot robot = new LegoRobot();
		SensorWatcher sensorWatcher = new SensorWatcher(robot);
		
		/* start the parcours */
		robot.drawText("Start!");
		Strategy strategy = new InitialStrategy();
		
		while (sensorWatcher.getState() != RobotState.ERROR &&
				strategy != null) {
			sensorWatcher.setListener(strategy);
			Thread sensorThread = new Thread(sensorWatcher);
			sensorThread.start();
			
			robot.drawText(strategy.toString());
			strategy = strategy.apply();
			
			try {
				sensorThread.join();
			} catch (InterruptedException e) {
				return;
			}
		}
		
		robot.drawText("Finished!");
	}

}
