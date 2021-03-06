package main;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.utility.Delay;
import strategy.*;



public class Main {

	public static void main(String[] args) {
		final Robot robot = new Robot();
		
		KeyListener listener = new KeyListener() {
			@Override
			public void keyReleased(Key k) {}
			@Override
			public void keyPressed(Key k) {
				robot.close();
				System.exit(0);
				
			}
		};
		Button.ENTER.addKeyListener(listener);
		

		robot.ev3.getLED().setPattern(0);
		robot.centerArm();
		robot.setStatus(Status.LABYRINTH);

		robot.ev3.getKeys().waitForAnyPress();
		Delay.msDelay(1000);
		
		Strategy currentStrategy = null;
		
//		for (int i = 0; i < 100; ++i) {
//			//robot.ev3.getKeys().waitForAnyPress();
//			//Delay.msDelay(1000);
//			//currentStrategy = new RaceStrategy(robot);
//			//currentStrategy.execute();
//			currentStrategy = new BossStrategy(robot);
//			currentStrategy.execute();
//			//currentStrategy = new SwampStrategy(robot);
//			//currentStrategy.execute();
//			//currentStrategy = new FollowLineStrategy(robot);
//			//currentStrategy.execute();
//			//currentStrategy = new FindBarcodeStrategy(robot);
//			//currentStrategy.execute();
//			//currentStrategy = new ReadBarcodeStrategy(robot);
//			//currentStrategy.execute();
//			//currentStrategy = new ElevatorStrategy(robot);
//			//currentStrategy.execute();
//			robot.ev3.getKeys().waitForAnyPress();
//		}
		
		Status status = robot.getStatus();
		
		while (status != Status.FINISH) {
			switch (status) {
				case LABYRINTH:
					currentStrategy = new LabyrinthStrategy(robot);
					break;
				case FIND_BARCODE:
					currentStrategy = new FindBarcodeStrategy(robot);
					break;
				case READ_BARCODE:
					currentStrategy = new ReadBarcodeStrategy(robot);
					break;
				case FOLLOW_LINE:
					currentStrategy = new FollowLineStrategy(robot);
					break;
				case BRIDGE:
					currentStrategy = new BridgeStrategy(robot);
					break;
				case ELEVATOR:
					currentStrategy = new ElevatorStrategy(robot);
					break;
				case SEESAW: 
					currentStrategy = new SeesawStrategy(robot);
					break;
				case SUSPENSE: 
					currentStrategy = new SuspensionBridgeStrategy(robot);
					break;
				case SWAMP: 
					currentStrategy = new SwampStrategy(robot);
					break;
				case RACE: 
					currentStrategy = new RaceStrategy(robot);
					break;
				case BOSS:
					currentStrategy = new BossStrategy(robot);
					break;
				case FINISH:
				case ERROR:
				default:
					currentStrategy = null;
			}
			
			if (currentStrategy != null) {
				currentStrategy.execute();
			} else {
				robot.ev3.getLED().setPattern(9);
				robot.ev3.getTextLCD().clear();
				robot.ev3.getTextLCD().drawString("Please set", 1, 1);
				robot.ev3.getTextLCD().drawString("in fron of", 1, 2);
				robot.ev3.getTextLCD().drawString("a barcode", 1, 3);
				robot.setStatus(Status.FIND_BARCODE);
				robot.ev3.getKeys().waitForAnyPress();
			}
			
			status = robot.getStatus();
		}
		
		robot.close();
	}

}
