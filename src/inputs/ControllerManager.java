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
		this.controller = game.getControllerInput(); 
	}

	public void update() {
	    controller.updateControls();
	    
	    if (game.getSelectedInput() == InputType.CONTROLLER && !controller.isConnected()) {
	        System.out.println("⚠️ Controller disconnected. Switching to keyboard.");
	        game.setSelectedInput(InputType.KEYBOARD);
	        return;
	    }

	    if (controller.isButtonPressed(5)) {
	        game.setSelectedInput(InputType.KEYBOARD);
	        lastSelectTime = System.currentTimeMillis();
	        System.out.println("Switched to KEYBOARD via B");
	    }

	    if (!controllerIsAllowed()) return;

	    switch (GameState.state) {
		    case MENU -> handleMenuInput();
		    case ENDGAME -> handleEndGameInput();
		    case BESTSCORES -> handleBestScoreInput();
		    case INSTRUCTIONS -> handleInstructionsInput();
	        case PLAYING -> handlePlayingInput();
	       
	        case QUIT -> System.exit(0);
	    }
	
	}

	private boolean controllerIsAllowed() {
	    return game.getSelectedInput() == InputType.CONTROLLER;
	}

	private void handlePlayingInput() {
		  double x = controller.getNormLeftX();
          double y = controller.getNormLeftY();

          if (Math.abs(x) > 0.15 || Math.abs(y) > 0.15) {
              double targetAngle = Math.atan2(y, x);
              double currentAngle = game.getPlaying().getPlayer().getAngle();
              double deltaAngle = normalizeAngle(targetAngle - currentAngle);
              double maxTurnPerUpdate = Math.toRadians(3);

              if (Math.abs(deltaAngle) > maxTurnPerUpdate)
                  deltaAngle = Math.signum(deltaAngle) * maxTurnPerUpdate;

              double newAngle = normalizeAngle(currentAngle + deltaAngle);
              game.getPlaying().getPlayer().setAngle(newAngle);
          }

          long now = System.currentTimeMillis();
          if (now - lastSelectTime > SELECT_COOLDOWN) {
              if (controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_B)) {
                  game.getPlaying().activateShield();
                  lastSelectTime = now;
              }
              if (controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_X)) {
              	 game.getPlaying().activateMagnet();
                  lastSelectTime = now;
              }
          }
	}
	private void handleMenuInput() {
		long now = System.currentTimeMillis();
		double stickY = controller.getNormLeftY();
	
		if (now - lastMenuMoveTime > MENU_COOLDOWN) {
			if (stickY < -0.5 || controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_DPAD_UP)) {
				game.getMenu().moveCursorUp();
				lastMenuMoveTime = now;
			} else if (stickY > 0.5 || controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_DPAD_DOWN)) {
				game.getMenu().moveCursorDown();
				lastMenuMoveTime = now;
			}
		}
		if (now - lastSelectTime > SELECT_COOLDOWN) {
			if (controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_A)) {
				game.getMenu().doSelect();
				lastSelectTime = now;
			}
		}
	}
	
	private void handleBestScoreInput() {
		long now = System.currentTimeMillis();
		
		if (controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_Y)) {
			game.getBestScores().returnMenu();
			lastSelectTime = now;
		}
	}
	
	private void handleInstructionsInput() {
		long now = System.currentTimeMillis();
		
			if (controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_Y)) {
				game.getInstructions().returnMenu();
				lastSelectTime = now;
			}
		}
	
	
	private void handleEndGameInput() {
		long now = System.currentTimeMillis();
		double stickY = controller.getNormLeftY();
	
		if (now - lastMenuMoveTime > MENU_COOLDOWN) {
			if (stickY > 0.5 || controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_DPAD_DOWN)) {
				game.getEndGame().moveCursorDown();
				lastMenuMoveTime = now;
			}
		}
		if (now - lastSelectTime > SELECT_COOLDOWN) {
			if (controller.isButtonPressed(ControllerInput.SDL_GAMEPAD_BUTTON_A)) {
				game.getEndGame().doSelect();
				lastSelectTime = now;
			}
		
		}
	}
	

	private double normalizeAngle(double angle) {
	    while (angle < -Math.PI) angle += 2 * Math.PI;
	    while (angle > Math.PI) angle -= 2 * Math.PI;
	    return angle;
	}

}
