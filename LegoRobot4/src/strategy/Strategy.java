package strategy;

import main.*;

public abstract class Strategy {

	protected Robot robot;
	
	public Strategy(Robot robot) {
		this.robot = robot;
	}
	
	public abstract void execute();
	
}
