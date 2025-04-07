package inputs;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import utils.SDL3Lib;

public class ControllerInput {

    public static final int SDL_INIT_GAMEPAD = 0x00002000;
    public static final int SDL_GAMEPAD_AXIS_LEFTX = 0;
    public static final int SDL_GAMEPAD_AXIS_LEFTY = 1;

    public static final int SDL_GAMEPAD_BUTTON_A = 0;
    public static final int SDL_GAMEPAD_BUTTON_B = 1;
    public static final int SDL_GAMEPAD_BUTTON_X = 2;
    public static final int SDL_GAMEPAD_BUTTON_Y = 3;
    public static final int SDL_GAMEPAD_BUTTON_DPAD_UP = 12;
    public static final int SDL_GAMEPAD_BUTTON_DPAD_DOWN = 13;

    private Pointer gamepad;

    private double normLeftX = 0;
    private double normLeftY = 0;
    private boolean[] buttonStates = new boolean[16]; 

    public ControllerInput() {
        boolean initOK = SDL3Lib.INSTANCE.SDL_Init(SDL_INIT_GAMEPAD);
        if (!initOK) {
            System.err.println("SDL_Init failed: " + SDL3Lib.INSTANCE.SDL_GetError());
            return;
        }

        if (SDL3Lib.INSTANCE.SDL_HasGamepad()) {
            IntByReference countRef = new IntByReference();
            Pointer ids = SDL3Lib.INSTANCE.SDL_GetGamepads(countRef);
            int num = countRef.getValue();
            if (num > 0 && ids != null) {
                int firstID = ids.getInt(0);
                gamepad = SDL3Lib.INSTANCE.SDL_OpenGamepad(firstID);
                if (gamepad == null) {
                    System.err.println("Failed to open gamepad 0");
                } else {
                    System.out.println("Gamepad connected!");
                }
            }
        } else {
            System.out.println("No gamepad connected.");
        }
    }

    public void updateControls() {
        if (gamepad == null) return;

        short x = SDL3Lib.INSTANCE.SDL_GetGamepadAxis(gamepad, SDL_GAMEPAD_AXIS_LEFTX);
        short y = SDL3Lib.INSTANCE.SDL_GetGamepadAxis(gamepad, SDL_GAMEPAD_AXIS_LEFTY);
        normLeftX = (x == -32768 ? -1.0 : x / 32767.0);
        normLeftY = (y == -32768 ? -1.0 : y / 32767.0);

        for (int i = 0; i < buttonStates.length; i++) {
            buttonStates[i] = SDL3Lib.INSTANCE.SDL_GetGamepadButton(gamepad, i);
        }
    }

    public double getNormLeftX() {
        return normLeftX;
    }

    public double getNormLeftY() {
        return normLeftY;
    }

    public boolean isButtonPressed(int buttonIndex) {
        if (buttonIndex >= 0 && buttonIndex < buttonStates.length) {
            return buttonStates[buttonIndex];
        }
        return false;
    }

    public void close() {
        if (gamepad != null) {
            SDL3Lib.INSTANCE.SDL_CloseGamepad(gamepad);
        }
        SDL3Lib.INSTANCE.SDL_Quit();
    }
    
    
}
