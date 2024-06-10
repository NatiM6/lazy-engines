package com.natim6.lazyengines;

public enum PassiveConfig {
    DISABLED(-1),
    PASSIVE(0),
    ACTIVE(1);

    private final int value;

    PassiveConfig(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
