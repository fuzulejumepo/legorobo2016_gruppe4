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
		robot.calibrateArm();
		robot.centerArm();

		
		Strategy currentStrategy;
		for (int i = 0; i < 1; ++i) {
			currentStrategy = new FollowLineStrategy(robot);
			currentStrategy.execute();
			//currentStrategy = new BridgeStrategy(robot);
			//currentStrategy.execute();
			//currentStrategy = new ElevatorStrategy(robot);
			//currentStrategy.execute();
			//robot.ev3.getKeys().waitForAnyPress();
		}
		
		/*Strategy currentStrategy = null;
		Status status = robot.getStatus();
		
		while (status != Status.FINISH) {
			System.out.println(status);
			switch (status) {
				case START:
					currentStrategy = new LabyrinthStrategy(robot);
					break;
				case BARCODE_FIND:
					currentStrategy = new FindBarcodeStrategy(robot);
					break;
				case BARCODE_READ:
					currentStrategy = new ReadBarcodeStrategy(robot);
					break;
				case LINE:
					currentStrategy = new FollowLineStrategy(robot);
					break;
				case BRIDGE:
					currentStrategy = new BridgeStrategy(robot);
					break;
				case ELEVATOR:
					currentStrategy = new ElevatorStrategy(robot);
					break;
				case SEESAW: 
					currentStrategy = new FollowLineStrategy(robot);
					break;
				case SUSPENSE: 
					currentStrategy = new SuspensionBridgeStrategy(robot);
					break;
				case SWAMP: 
					currentStrategy = new SwampStrategy(robot);
					break;
				case RACE: 
					currentStrategy = new RacingStrategy(robot);
					break;
				default:
					currentStrategy = null;
			}
			
			if (currentStrategy != null) {
				currentStrategy.execute();
				status = robot.getStatus();
			}
			else {
				robot.ev3.getLED().setPattern(1);
				System.out.println("No strategy found!");
				break;
			}
		}*/
		
		robot.close();
	}

}
