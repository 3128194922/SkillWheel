package com.uniye.skillwheel.client;

public final class ClientInputState {
    private ClientInputState() {
    }

    public static boolean isSneakKeyDown() {
        return KeyBindings.isShiftStateDown();
    }
}
