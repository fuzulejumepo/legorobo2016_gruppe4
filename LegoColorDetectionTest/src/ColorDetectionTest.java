
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;

public class ColorDetectionTest {

	public static void main(String[] args) {
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();
		int color = 0;
		lcd.drawString("Test the color", 4, 4);
		EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S1);
		for (int i = 0; i < 10; i++) {
			lcd.clear();
			color = lightSensor.getColorID();
			switch (color){
				case Color.BLACK: 
					lcd.drawString("Black", 4, 4);
					break;
				case Color.BLUE: 
					lcd.drawString("blue", 4, 4);
					break;
				case Color.BROWN: 
					lcd.drawString("Brown", 4, 4);
					break;
				case Color.GREEN: 
					lcd.drawString("Green", 4, 4);
					break;
				case Color.RED: 
					lcd.drawString("Red", 4, 4);
					break;
				case Color.WHITE: 
					lcd.drawString("white", 4, 4);
					break;
				case Color.YELLOW: 
					lcd.drawString("Yellow", 4, 4);
					break;
				default: 
					lcd.drawString("No specified color,", 3, 3);
					lcd.drawInt(color, 4, 4);
			}
			lcd.drawInt(color, 5, 5);
			keys.waitForAnyPress();
		}
	}

}