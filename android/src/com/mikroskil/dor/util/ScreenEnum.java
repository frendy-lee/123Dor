package com.mikroskil.dor.util;

import com.mikroskil.dor.GameMode;
import com.mikroskil.dor.screen.AbstractScreen;
import com.mikroskil.dor.screen.LevelSelectScreen;
import com.mikroskil.dor.screen.MainMenuScreen;
import com.mikroskil.dor.screen.GameScreen;

/**
 * Created by Frendy on 1/1/2016.
 */
public enum ScreenEnum {

    MAIN_MENU {
        public AbstractScreen getScreen(Object... params) {
            return new MainMenuScreen();
        }
    },

    LEVEL_SELECT {
        public AbstractScreen getScreen(Object... params) {
            return new LevelSelectScreen();
        }
    },

    GAME {
        public AbstractScreen getScreen(Object... params) {
            return new GameScreen((GameMode.GameType) params[0], false);
        }
    };

    public abstract AbstractScreen getScreen(Object... params);
}
