package inputs;

import game.Game;
import gameStates.GameState;

public class ControllerManager {

	private final ControllerInput controller;
	private final Game game;

	private long lastMenuMoveTime = 0;
	private final long MENU_COOLDOWN = 300;

	private long lastSelectTime = 0;
	private final long SELECT_COOLDOWN = 500;

	public ControllerManager(Game game) {
		this.game = game;
		this.controller = new ControllerInput();
	}

	public void update() {
		controller.updateControls();

		if (GameState.state == GameState.PLAYING) {
			double x = controller.getNormLeftX();
			double y = controller.getNormLeftY();

	
			if (Math.abs(x) > 0.15 || Math.abs(y) > 0.15) {
			    double targetAngle = Math.atan2(y, x);
			    double currentAngle = game.getPlaying().getPlayer().getAngle();

			    double deltaAngle = normalizeAngle(targetAngle - currentAngle);

			   
			    double maxAllowedChange = Math.toRadians(120);
			    if (Math.abs(deltaAngle) > maxAllowedChange) {
			        return;
			    }

			  
			    double maxTurnPerUpdate = Math.toRadians(3);
			    if (Math.abs(deltaAngle) > maxTurnPerUpdate) {
			        deltaAngle = Math.signum(deltaAngle) * maxTurnPerUpdate;
			    }

			    double newAngle = normalizeAngle(currentAngle + deltaAngle);
			    game.getPlaying().getPlayer().setAngle(newAngle);
			}

			long now = System.currentTimeMillis();
			if (now - lastSelectTime > SELECT_COOLDOWN) {
				if (controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_B)) {
					simulateKeyPress(game.getPlaying(), 'E');
					lastSelectTime = now;
				}

				if (controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_X)) {
					simulateKeyPress(game.getPlaying(), 'Q');
					lastSelectTime = now;
				}
			}

		}

		if (GameState.state == GameState.MENU) {
			handleMenuInput(game.getMenu());
		} else if (GameState.state == GameState.ENDGAME) {
			handleMenuInput(game.getEndGame());
		} else if (GameState.state == GameState.BESTSCORES) {
			handleMenuInput(game.getBestScores());
		} else if (GameState.state == GameState.INSTRUCTIONS) {
			handleMenuInput(game.getInstructions());
		}
	}

	private void handleMenuInput(Object menuState) {
		long now = System.currentTimeMillis();
		double stickY = controller.getNormLeftY();

		if (now - lastMenuMoveTime > MENU_COOLDOWN) {
			if (stickY < -0.5 || controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_DPAD_UP)) {
				simulateKeyPress(menuState, 'W');
				lastMenuMoveTime = now;
			} else if (stickY > 0.5 || controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_DPAD_DOWN)) {
				simulateKeyPress(menuState, 'S');
				lastMenuMoveTime = now;
			}
		}

		if (now - lastSelectTime > SELECT_COOLDOWN) {
			if (controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_A)) {
				simulateKeyPress(menuState, '\n');
				lastSelectTime = now;
			}
			if (controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_Y)) {
				simulateKeyPress(menuState, (char) 27);
				lastSelectTime = now;
			}
		}
	}

	private double normalizeAngle(double angle) {
	    while (angle < -Math.PI) angle += 2 * Math.PI;
	    while (angle > Math.PI) angle -= 2 * Math.PI;
	    return angle;
	}
	
	private void simulateKeyPress(Object state, char key) {
		try {
			state.getClass().getMethod("simulateKeyPress", char.class).invoke(state, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
