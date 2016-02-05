package main;

import lejos.hardware.Keys;
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
		robot.drawText("Start!", 5, 4);
		Strategy strategy = new InitialStrategy();
		
		while (sensorWatcher.getState() != RobotState.ERROR &&
				strategy != null) {
			sensorWatcher.setListener(strategy);
			Thread sensorThread = new Thread(sensorWatcher);
			sensorThread.start();
			
			robot.lcdClear();
			robot.drawText(strategy.toString(), 1, 4);
			strategy = strategy.apply();
			
			try {
				sensorThread.join();
			} catch (InterruptedException e) {
				return;
			}
		}
		
		robot.lcdClear();
		robot.drawText("Finished!", 5, 4);
		
		robot.close();
		
		Keys keys = robot.getBrick().getKeys();
		keys.waitForAnyPress();
	}

}
