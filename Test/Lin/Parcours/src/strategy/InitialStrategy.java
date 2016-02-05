package strategy;

import model.RobotState;

public class InitialStrategy extends Strategy {

	public InitialStrategy() {
		this.state = RobotState.INITIAL;
	}
	
	@Override
	public Strategy apply() {
		while(this.state == RobotState.INITIAL) {
		}
		return changeStrategy();
	}

	@Override
	public String toString() {
		return "InitialStrategy";
	}

}
