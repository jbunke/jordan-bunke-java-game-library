package com.jordanbunke.jbjgl.game;

import com.jordanbunke.jbjgl.image.JBJGLImage;
import com.jordanbunke.jbjgl.window.JBJGLWindow;

public class JBJGLGame {
    private final JBJGLGameEngine gameEngine;
    private final JBJGLGameManager gameManager;

    private final String title;

    private JBJGLGame(
            final String title,
            final JBJGLGameManager gameManager, final JBJGLGameEngine gameEngine
    ) {
        this.title = title;
        this.gameManager = gameManager;
        this.gameEngine = gameEngine;
    }

    public static JBJGLGame create(
            final String title, final JBJGLGameManager gameManager,
            final int width, final int height,
            final JBJGLImage icon,
            final boolean exitOnClose, final boolean maximized
    ) {
        JBJGLWindow window = JBJGLWindow.create(
                title, width, height, icon, exitOnClose, false, maximized
        );
        JBJGLGameEngine gameEngine = JBJGLGameEngine.newForGame(window, gameManager);
        return new JBJGLGame(title, gameManager, gameEngine);
    }

    public JBJGLGameEngine getGameEngine() {
        return gameEngine;
    }

    public JBJGLGameManager getGameManager() {
        return gameManager;
    }

    @Override
    public String toString() {
        return title;
    }
}