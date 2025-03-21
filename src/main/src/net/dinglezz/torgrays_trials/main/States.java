package net.dinglezz.torgrays_trials.main;

public enum States {
    // Main
    STATE_TITLE,
    STATE_PLAY,
    STATE_PAUSE,
    STATE_DIALOGUE,
    STATE_CHARACTER,
    STATE_GAME_OVER,

    // Title Sub-States
    TITLE_STATE_MAIN,
    TITLE_STATE_MODES,

    // Pause Sub-States
    PAUSE_SETTINGS_MAIN,
    PAUSE_STATE_MAIN,
    PAUSE_CONTROLS,
    PAUSE_SETTINGS_NOTIFICATION,
    PAUSE_SETTINGS_CONFIRM
}
