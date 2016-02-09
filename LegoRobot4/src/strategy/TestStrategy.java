package strategy;

import main.Robot;

public class TestStrategy extends Strategy {
	
	public TestStrategy(Robot robot) {
		super(robot);
	}
	
	public void execute() {
		robot.ev3.getTextLCD().drawString("TestStrategy", 1, 1);

		//testUltraSensor();
		testColorSensorAmbientMode();
	}
	
	public void testUltraSensor() {
		robot.ev3.getTextLCD().drawString("UltraSensor", 2, 2);

		float[] sample = { 0.0f };
		
		for (int i = 0; i < 10; ++i) {
			robot.ultraSensor.fetchSample(sample, 0);
			robot.ev3.getTextLCD().drawString("" + sample[0], 2, 3);
			
			robot.ev3.getKeys().waitForAnyPress();
		}
	}
	
	public void testColorSensorAmbientMode() {
		robot.ev3.getTextLCD().drawString("ColorSensorAmbientMode", 2, 2);
		
		robot.colorSensor.setFloodlight(false);
		
		float[] sample = { 0.0f };
		
		for (int i = 0; i < 10; ++i) {
			robot.colorSensor.getAmbientMode().fetchSample(sample, 0);
			robot.ev3.getTextLCD().drawString("" + sample[0], 2, 3);
			
			robot.ev3.getKeys().waitForAnyPress();
		}
	}

}
