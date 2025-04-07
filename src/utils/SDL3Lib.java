package utils;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;

public interface SDL3Lib extends Library {

    SDL3Lib INSTANCE = Native.load("SDL3", SDL3Lib.class);

    boolean SDL_Init(int flags);
    void SDL_Quit();

    boolean SDL_HasGamepad();

    Pointer SDL_GetGamepads(IntByReference count);
    Pointer SDL_OpenGamepad(int instance_id);
    void SDL_CloseGamepad(Pointer gamepad);

    short SDL_GetGamepadAxis(Pointer gamepad, int axis);
    boolean SDL_GetGamepadButton(Pointer gamepad, int button);

    String SDL_GetError();
}
