package net.dinglezz.torgrays_trials.main;

import net.dinglezz.torgrays_trials.entity.Entity;
import net.dinglezz.torgrays_trials.entity.EntityTags;
import net.dinglezz.torgrays_trials.event.EVT_Teleport;
import net.dinglezz.torgrays_trials.object.Coins;
import net.dinglezz.torgrays_trials.object.Heart;
import net.dinglezz.torgrays_trials.tile.MapHandler;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

public class UI {
    // General
    Game game;
    Graphics2D graphics2D;
    Font maruMonica;
    BufferedImage heart, half_heart, lost_heart, coin;
    private String currentDialogue = "";
    public int commandNumber = 0;
    public String actionMethod;

    // Mini Notifications
    ArrayList<String> miniNotifications = new ArrayList<>();
    ArrayList<Integer> miniNotificationCounter = new ArrayList<>();

    // States
    public States.UIStates uiState = States.UIStates.JUST_DEFAULT;
    public String subUIState = "Main Title";

    // Inventory
    public int playerSlotCol = 0;
    public int playerSlotRow = 0;
    public int entitySlotCol = 0;
    public int entitySlotRow = 0;
    public Entity npc;

    // Transitions
    private float transitionCounter = 0f;
    private float transitionOpenSpeed = 0f;
    private float transitionCloseSpeed = 0f;
    private Color transitionColor = Color.BLACK;
    public boolean transitioning = false;
    private boolean fadeBack = false;

    public UI(Game game) {
        this.game = game;

        try {
            InputStream inputStream = getClass().getResourceAsStream("/font/Maru_Monica.ttf");
            maruMonica = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (FontFormatException | IOException exception) {
            game.exceptionState = States.ExceptionStates.ONLY_IGNORABLE;
            throw new RuntimeException(exception);
        }

        // Make some HUD objects
        Entity obj_heart = new Heart(game);
        heart = obj_heart.image;
        half_heart = obj_heart.image2;
        lost_heart = obj_heart.image3;
        Entity Coin = new Coins(game, 1);
        coin = Coin.down1;
    }

    /// Adds a mini notification message to the side of the screen
    public void addMiniNotification(String message) {
        miniNotifications.add(message);
        miniNotificationCounter.add(0);
    }
    public void draw(Graphics2D graphics2D) {
        this.graphics2D = graphics2D;
        graphics2D.setFont(maruMonica);
        graphics2D.setColor(Color.white);

        if (transitioning) {
            drawTransitionScreen();
        }

        if (uiState.defaultUI) {
            switch (game.gameState) {
                case TITLE -> drawTitleScreen();
                case PLAY, PAUSE -> drawBasics();
                case GAME_END -> drawGameOverScreen();
                case EXCEPTION -> drawExceptionScreen();
            }
        }

        switch (uiState) {
            case DIALOGUE -> drawDialogueScreen();
            case INTERACT -> drawInteractScreen();
            case PAUSE -> drawPauseScreen();
            case TRADE -> drawTradeScreen();
            case CHARACTER -> drawCharacterScreen();
            case MAP -> drawMapScreen();
        }
    }

    /**
     * Draws the basic UI elements such as player's health, darkness state,
     * mini notifications, and debug menu if applicable. This method ensures
     * that fundamental UI components are consistently rendered during gameplay.
     * <p>
     * Functionality includes:
     * <li>Drawing the player's current and maximum health.</li>
     * <li>Displaying the current darkness state of the environment.</li>
     * <li>Showing mini notifications for brief messages.</li>
     * <li>Rendering the debug menu for game metrics when debug mode is enabled.</li>
     */
    public void drawBasics() {
        drawPlayerHealth();
        drawDarknessState();
        drawMiniNotifications();
        drawDebugMenu();
    }

    public void drawPlayerHealth() {
        int x = game.tileSize / 2;
        int y = game.tileSize / 2;
        int i = 0;

        // Draw max health
        while (i < game.player.maxHealth / 2) {
            graphics2D.drawImage(lost_heart, x, y, null);
            i++;
            x += game.tileSize;
        }

        // Reset
        x = game.tileSize / 2;
        y = game.tileSize / 2;
        i = 0;

        // Draw current health
        while (i < game.player.health) {
            graphics2D.drawImage(half_heart, x, y, null);
            i++;
            if (i < game.player.health) {
                graphics2D.drawImage(heart, x, y, null);
            }
            i++;
            x += game.tileSize;
        }
    }

    /// Draws the the messages on the side of your screen
    public void drawMiniNotifications() {
        int messageX = game.tileSize / 2;
        int messageY = game.tileSize * 12 - game.tileSize / 2;
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN,26f));

        for (int i = 0; i < miniNotifications.size(); i++) {
            if (miniNotifications.get(i) != null) {
                graphics2D.setColor(Color.white);
                graphics2D.drawString(miniNotifications.get(i), messageX, messageY);

                int counter = miniNotificationCounter.get(i) + 1;
                miniNotificationCounter.set(i, counter);
                messageY -= 30;

                if (miniNotificationCounter.get(i) > 120) {
                    miniNotifications.remove(i);
                    miniNotificationCounter.remove(i);
                }
            }
        }
    }
    public void drawDarknessState() {
        int x = game.tileSize / 2;
        int y = (game.tileSize * 2) + (game.tileSize / 3);
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD, 38f));

        graphics2D.drawString(game.environmentManager.getDarknessStateString(), x, y);
    }
    public void drawDebugMenu() {
        if (game.debug) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - game.drawStart;

            graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN, 21f));
            graphics2D.setColor(Color.white);
            int x = game.tileSize / 2;
            int y = game.tileSize * 3;
            int lineHeight = 20;

            graphics2D.drawString("FPS: " + game.FPS, x, y); y += lineHeight;
            graphics2D.drawString("Draw Time: " + passed, x ,y); y += lineHeight;
            graphics2D.drawString("Darkness Counter: " + game.environmentManager.lighting.darknessCounter + "/" + switch (game.environmentManager.lighting.darknessState) {
                case NIGHT -> game.environmentManager.lighting.nightLength;
                default -> game.environmentManager.lighting.gloomLength;
            }, x, y); y += lineHeight;
            y += lineHeight;
            graphics2D.drawString("Game State: " + game.gameState, x ,y); y += lineHeight;
            graphics2D.drawString("Time State: " + game.environmentManager.lighting.darknessState, x ,y); y += lineHeight;
            graphics2D.drawString("UI State: " + uiState, x ,y); y += lineHeight;
            graphics2D.drawString("Sub-UI State: " + subUIState, x ,y); y += lineHeight;
            y += lineHeight;
            graphics2D.drawString("World_Map: " + game.currentMap, x, y); y += lineHeight;
            graphics2D.drawString("World X: " + game.player.worldX, x, y); y += lineHeight;
            graphics2D.drawString("World Y: " + game.player.worldY, x, y);  y += lineHeight;
            graphics2D.drawString("Col: " + (game.player.worldX + game.player.solidArea.x) / game.tileSize, x, y);  y += lineHeight;
            graphics2D.drawString("Row: " + (game.player.worldY + game.player.solidArea.y) / game.tileSize, x, y); y += lineHeight;
        }
    }

    public void drawTitleScreen() {
        if (Objects.equals(subUIState, "Main Title")) {
            // Title Text
            graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD, 90f));
            String text = "Torgray's Trials";
            int x = getCentreX(text);
            int y = game.tileSize * 3;

            // Title Shadow
            graphics2D.setColor(new Color(147, 37, 37));
            graphics2D.drawString(text, x + 4, y + 4);

            // Main Title
            graphics2D.setColor(new Color(209, 25, 25));
            graphics2D.drawString(text, x, y);

            // Torgray Image
            x = game.screenWidth / 2 - (game.tileSize * 2) / 2;
            y += game.tileSize + (game.tileSize / 2);
            graphics2D.drawImage(game.player.down1, x, y, game.tileSize * 2, game.tileSize * 2, null);

            // Menu
            graphics2D.setColor(Color.white);

            graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD, 48f));
            text = "New Game";
            x = getCentreX(text);
            y += (game.tileSize * 3) + (game.tileSize / 2);
            graphics2D.drawString(text, x, y);
            if (commandNumber == 0) {
                graphics2D.drawString(">", x - game.tileSize,  y);
            }


            graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD, 48f));
            text = "Load Game";
            x = getCentreX(text);
            y += game.tileSize;
            graphics2D.drawString(text, x, y);
            if (commandNumber == 1) {
                graphics2D.drawString(">", x - game.tileSize,  y);
            }

            graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD, 48f));
            text = "Quit";
            x = getCentreX(text);
            y += game.tileSize;
            graphics2D.drawString(text, x, y);
            if (commandNumber == 2) {
                graphics2D.drawString(">", x - game.tileSize,  y);
            }
        } else if (Objects.equals(subUIState, "Modes")) {
            // GameMode Selection
            graphics2D.setColor(Color.white);
            graphics2D.setFont(graphics2D.getFont().deriveFont(42f));

            String text = "Select a GameMode";
            int x = getCentreX(text);
            int y = game.tileSize * 3;
            graphics2D.drawString(text, x, y);

            text = "Easy";
            x = getCentreX(text);
            y += game.tileSize * 3;
            graphics2D.drawString(text, x, y);
            if (commandNumber == 0) {
                graphics2D.drawString(">", x - game.tileSize, y);
            }

            text = "Medium";
            x = getCentreX(text);
            y += game.tileSize;
            graphics2D.drawString(text, x, y);
            if (commandNumber == 1) {
                graphics2D.drawString(">", x - game.tileSize, y);
            }

            text = "Hard";
            x = getCentreX(text);
            y += game.tileSize;
            graphics2D.drawString(text, x, y);
            if (commandNumber == 2) {
                graphics2D.drawString(">", x - game.tileSize, y);
            }

            text = "Back";
            y += game.tileSize * 2;
            graphics2D.drawString(text, x, y);
            if (commandNumber == 3) {
                graphics2D.drawString(">", x - game.tileSize, y);
            }
        }

    }
    public void drawPauseScreen() {
        graphics2D.setColor(new Color(0, 0, 0, 0.35f));
        graphics2D.fillRect(0, 0, game.screenWidth, game.screenHeight);

        graphics2D.setColor(Color.white);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD, 48f));

        // Sub-Window
        int frameX = game.tileSize * 6;
        int frameY = game.tileSize * 3;
        int frameWidth = game.tileSize * 8;
        int frameHeight = game.tileSize * 2;

        if (Objects.equals(subUIState, "Settings Main") || Objects.equals(subUIState, "Controls")) {
            frameY = game.tileSize;
            frameHeight = game.tileSize * 10;
        }
        else if (Objects.equals(subUIState, "Confirm") || Objects.equals(subUIState, "Notification")) {
            frameY = game.tileSize * 3;
            frameHeight = game.tileSize * 6;
        }
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        switch (subUIState) {
            case "Main Pause":
                // Title
                String text = "Game Paused";
                int x = getCentreX(text);
                int y = frameY + game.tileSize + (game.tileSize / 3);
                graphics2D.drawString(text, x, y);

                // Options Frame
                x = frameX + game.tileSize;
                y = frameY + (game.tileSize * 2) + (game.tileSize / 4);
                drawSubWindow(x, y, frameWidth - (game.tileSize * 2), frameHeight + (game.tileSize * 2) + (game.tileSize / 2));

                // Settings
                graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN, 32f));
                y += game.tileSize;
                x += game.tileSize + (game.tileSize / 2);
                graphics2D.drawString("Settings", x, y);
                if (commandNumber == 0) {
                    graphics2D.drawString(">", x - game.tileSize, y);

                    if (game.inputHandler.spacePressed) {
                        subUIState = "Settings Main";
                        commandNumber = 0;
                    }
                }

                // Controls
                y += game.tileSize;
                graphics2D.drawString("Controls", x, y);
                if (commandNumber == 1) {
                    graphics2D.drawString(">", x - game.tileSize, y);

                    if (game.inputHandler.spacePressed) {
                        subUIState = "Controls";
                        commandNumber = 0;
                    }
                }

                // End Game
                y += game.tileSize;
                graphics2D.drawString("End Game", x, y);
                if (commandNumber == 2) {
                    graphics2D.drawString(">", x - game.tileSize, y);

                    if (game.inputHandler.spacePressed) {
                        commandNumber = 1;
                        subUIState = "Confirm";
                        setCurrentDialogue("Are you sure you wanna \nend this game? Your data \nwon't be saved.");
                        actionMethod = "yesGameEnd";
                    }
                }

                // Resume
                y += game.tileSize;
                graphics2D.drawString("Resume", x, y);
                if (commandNumber == 3) {
                    graphics2D.drawString(">", x - game.tileSize, y);

                    if (game.inputHandler.spacePressed) {
                        game.gameState = States.GameStates.PLAY;
                        game.ui.uiState = States.UIStates.JUST_DEFAULT;
                        commandNumber = 0;
                    }
                }
                break;
            case "Settings Main": settingsMain(frameX, frameY); break;
            case "Notification": settingsNotification(frameX, frameY); break;
            case "Confirm": settingsConfirm(frameX, frameY); break;
            case "Controls": controlsPanel(frameX, frameY); break;
        }

        game.inputHandler.spacePressed = false;
    }

    public void settingsMain(int frameX, int frameY) {
        int textX;
        int textY;

        // Title
        String text = "Settings";
        textX = getCentreX(text);
        textY = frameY + game.tileSize + (game.tileSize / 4);
        graphics2D.drawString(text, textX, textY);

        // Music
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN, 32f));
        textX = frameX + game.tileSize;
        textY += game.tileSize * 2;
        graphics2D.drawString("Music", textX, textY);
        if (commandNumber == 0) {
            graphics2D.drawString(">", textX - 30, textY);
        }


        // Sound Effects
        textY += game.tileSize;
        graphics2D.drawString("Sounds", textX, textY);
        if (commandNumber == 1) {
            graphics2D.drawString(">", textX - 30, textY);
        }

        // Fullscreen
        textY += game.tileSize;
        graphics2D.drawString("Full Screen", textX, textY);
        if (commandNumber == 2) {
            graphics2D.drawString(">", textX - 30, textY);

            if (game.inputHandler.spacePressed) {
                if (!game.fullScreen && !game.BRendering) {
                    commandNumber = 1;
                    subUIState = "Confirm";
                    setCurrentDialogue("Are you sure you wanna \nenter full screen? It won't \nscale without BRendering.");
                    actionMethod = "yesFullScreen";
                } else {
                    yesFullScreen();
                }
            }
        }

        //BRendering
        textY += game.tileSize;
        graphics2D.drawString("BRendering", textX, textY);
        if (commandNumber == 3) {
            graphics2D.drawString(">", textX - 30, textY);

            if (game.inputHandler.spacePressed) {
                if (!game.BRendering) {
                    commandNumber = 1;
                    subUIState = "Confirm";
                    setCurrentDialogue("BRendering is a highly \nexperimental mode that \ncan break the game if on");
                    actionMethod = "yesBRendering";
                } else {
                    yesBRendering();
                }
            }
        }

        textY += game.tileSize;
        graphics2D.drawString("Pathfinding", textX, textY);
        if (commandNumber == 4) {
            graphics2D.drawString(">", textX - 30, textY);

            if (game.inputHandler.spacePressed) {
                if (!game.pathFinding) {
                    commandNumber = 1;
                    subUIState = "Confirm";
                    setCurrentDialogue("Pathfinding is a highly \nexperimental mode that \nmay not work correctly");
                    actionMethod = "yesPathfinding";
                } else {
                    yesPathfinding();
                }
            }
        }

        // Close
        textY += game.tileSize * 2;
        graphics2D.drawString("Close", textX, textY);
        if (commandNumber == 5) {
            graphics2D.drawString(">", textX - 30, textY);

            if (game.inputHandler.spacePressed) {
                subUIState = "Main Pause";
                commandNumber = 0;
            }
        }

        // Music Slider
        textX = frameX + game.tileSize * 4;
        textY = frameY + game.tileSize * 2 + (game.tileSize / 2) + 14;
        graphics2D.drawRect(textX, textY, 120, 24);
        int volumeWidth = 24 * Sound.music.volumeScale;
        graphics2D.fillRect(textX, textY, volumeWidth, 24);

        // Sound Slider
        textY += game.tileSize;
        graphics2D.drawRect(textX, textY, 120, 24);
        volumeWidth = 24 * Sound.sfx.volumeScale;
        graphics2D.fillRect(textX, textY, volumeWidth, 24);

        // Full-Screen Check Box
        textX += game.tileSize;
        textY += game.tileSize;
        graphics2D.setStroke(new BasicStroke(3));
        graphics2D.drawRect(textX, textY, 24 , 24);

        if (game.fullScreen) {
            graphics2D.fillRect(textX, textY, 24, 24);
        }

        // BRendering Check Box
        textY += game.tileSize;
        graphics2D.setStroke(new BasicStroke(3));
        graphics2D.drawRect(textX, textY, 24 , 24);

        if (game.BRendering) {
            graphics2D.fillRect(textX, textY, 24, 24);
        }
        
        // Pathfinding Check Box
        textY += game.tileSize;
        graphics2D.setStroke(new BasicStroke(3));
        graphics2D.drawRect(textX, textY, 24 , 24);

        if (game.pathFinding) {
            graphics2D.fillRect(textX, textY, 24, 24);
        }

        // Save Data
        game.config.saveConfig();
    }
    public void settingsNotification(int frameX, int frameY) {
        // Title
        String text = "Note:";
        int textX = getCentreX(text);
        int textY = frameY + game.tileSize + (game.tileSize / 4);
        graphics2D.drawString(text, textX, textY);

        // Text
        textX = frameX + game.tileSize / 2;
        textY = frameY + game.tileSize * 2 + (game.tileSize / 4);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN, 28f));

        for (String line : currentDialogue.split("\n")) {
            graphics2D.drawString(line, textX, textY);
            textY += 40;
        }

        // Close
        textY = frameY = game.tileSize * 8 + (game.tileSize / 2);
        textX += game.tileSize / 2;
        graphics2D.drawString("Close", textX, textY);
        if (commandNumber == 0) {
            graphics2D.drawString(">", textX - 30, textY);
            if (game.inputHandler.spacePressed) {
                subUIState = "Main Pause";
            }
        }
    }
    public void settingsConfirm(int frameX, int frameY) {
        // Title
        String text = "Are You Sure?";
        int textX = getCentreX(text);
        int textY = frameY + game.tileSize + (game.tileSize / 4);
        graphics2D.drawString(text, textX, textY);

        // Text
        textX = frameX + game.tileSize / 2;
        textY = frameY + game.tileSize * 2 + (game.tileSize / 4);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN,28f));

        for (String line : currentDialogue.split("\n")) {
            graphics2D.drawString(line, textX, textY);
            textY += 40;
        }

        // Yes
        textY = game.tileSize * 8 - (game.tileSize / 4);
        textX += game.tileSize / 2;
        graphics2D.drawString("Yes", textX, textY);
        if (commandNumber == 0) {
            graphics2D.drawString(">", textX - 30, textY);
            if (game.inputHandler.spacePressed) {
                invokeActionMethod();
            }
        }

        // No
        textY += 40;
        graphics2D.drawString("No", textX, textY);
        if (commandNumber == 1) {
            graphics2D.drawString(">", textX - 30, textY);
            if (game.inputHandler.spacePressed) {
                subUIState = "Main Pause";
                commandNumber = 0;
            }
        }
    }

    public void controlsPanel(int frameX, int frameY) {
        int textX;
        int textY;

        // Title
        String text = "Controls:";
        textX = getCentreX(text);
        textY = frameY + game.tileSize + (game.tileSize / 4);
        graphics2D.drawString(text, textX, textY);

        // Control Titles
        textX = frameX + game.tileSize / 2;
        textY = frameY + game.tileSize * 2 + (game.tileSize / 4);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN, 28f));

        graphics2D.drawString("Move", textX, textY); textY += game.tileSize;
        graphics2D.drawString("Confirm", textX, textY); textY += game.tileSize;
        graphics2D.drawString("Exit", textX, textY); textY += game.tileSize;
        graphics2D.drawString("Attack", textX, textY); textY += game.tileSize;
        graphics2D.drawString("Interact", textX, textY); textY += game.tileSize;
        graphics2D.drawString("Inventory", textX, textY); textY += game.tileSize;

        // Control Keys
        textX = frameX + game.tileSize * 5 + (game.tileSize / 2);
        textY = frameY + game.tileSize * 2 + (game.tileSize / 4);
        graphics2D.drawString("WASD", textX, textY); textY += game.tileSize;
        graphics2D.drawString("Space", textX, textY); textY += game.tileSize;
        graphics2D.drawString("    Esc", textX, textY); textY += game.tileSize;
        graphics2D.drawString("Space", textX, textY); textY += game.tileSize;
        graphics2D.drawString("       E", textX, textY); textY += game.tileSize;
        graphics2D.drawString("       E", textX, textY); textY += game.tileSize;

        // Close
        textY = game.tileSize * 10 + (game.tileSize / 2);
        textX = frameX + game.tileSize;
        graphics2D.drawString("Close", textX, textY);
        if (commandNumber == 0) {
            graphics2D.drawString(">", textX - 30, textY);
            if (game.inputHandler.spacePressed) {
                subUIState = "Main Pause";
                commandNumber = 0;
            }
        }
    }
    public void drawDialogueScreen() {
        int width = game.screenWidth - (game.tileSize * 9);
        int height = game.tileSize * 4;
        int x = game.screenWidth / 2 - (width / 2);
        int y = game.tileSize / 2 + (game.tileSize * 7);
        drawSubWindow(x, y, width, height);

        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN, 30f));
        x += game.tileSize / 2;
        y += game.tileSize;

        for (String line : currentDialogue.split("\n")) {
            graphics2D.drawString(line, x, y);
            y += 40;
        }
    }
    public void setCurrentDialogue(String dialogue) {
        currentDialogue = dialogue;
    }

    public void drawInteractScreen() {
        // Settings
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD, 35f));
        String text = "Press E to interact";
        int x = getCentreX(text);
        int y = (game.tileSize / 2) + (game.tileSize * 10);

        // Stroke
        graphics2D.setColor(Color.black);
        graphics2D.drawString(text, x + 3, y + 3);

        // Main Text
        graphics2D.setColor(Color.white);
        graphics2D.drawString(text, x, y);
    }
    public void drawCharacterScreen() {
        // Inventory
        drawInventory(game.player, true);

        // Make a frame
        final int frameX = game.tileSize / 2;
        final int frameY = game.tileSize / 2;
        final int frameWidth = game.tileSize * 6;
        final int frameHeight = game.tileSize * 11;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // Text
        graphics2D.setColor(Color.white);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN, 30f));

        int textX = frameX + 20;
        int textY = frameY + game.tileSize;
        final int lineHeight = 35;

        // Titles
        graphics2D.drawString("Level", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("Next Level", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("Exp", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("--------", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("Strength", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("Dexterity", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("Attack", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("Defence", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("--------", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("Health", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("Coins", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("--------", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("Weapon", textX, textY);
        textY += lineHeight;
        graphics2D.drawString("Shield", textX, textY);

        // Values
        int tailX = (frameX + frameWidth) - 30;
        textY = frameY + game.tileSize;
        String value;

        value = String.valueOf(game.player.level);
        textX = alignXToRight(value, tailX);
        graphics2D.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(game.player.nextLevelExp);
        textX = alignXToRight(value, tailX);
        graphics2D.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(game.player.exp);
        textX = alignXToRight(value, tailX);
        graphics2D.drawString(value, textX, textY);
        textY += lineHeight * 2;

        value = String.valueOf(game.player.strength);
        textX = alignXToRight(value, tailX);
        graphics2D.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(game.player.dexterity);
        textX = alignXToRight(value, tailX);
        graphics2D.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(game.player.attack);
        textX = alignXToRight(value, tailX);
        graphics2D.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(game.player.defence);
        textX = alignXToRight(value, tailX);
        graphics2D.drawString(value, textX, textY);
        textY += lineHeight * 2;

        value = game.player.health + "/" + game.player.maxHealth;
        textX = alignXToRight(value, tailX);
        graphics2D.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(game.player.coins);
        textX = alignXToRight(value, tailX);
        graphics2D.drawString(value, textX, textY);
        textY += lineHeight * 2 - (lineHeight / 4);

       graphics2D.drawImage(game.player.currentWeapon.down1, tailX - game.tileSize, textY - 37, null);
       textY += game.tileSize;
       graphics2D.drawImage(game.player.currentShield.down1, tailX - game.tileSize, textY - 37, null);
    }
    public void drawInventory(Entity entity, boolean cursor) {
        // Frame
        int frameX;
        int frameY;
        int frameWidth;
        int frameHeight;
        int slotCol;
        int slotRow;
        if (entity == game.player) {
            frameX = game.tileSize / 2 + game.tileSize * 13;
            frameY = game.tileSize / 2;
            frameWidth = game.tileSize * 6;
            frameHeight = game.tileSize * 6;
            slotCol = playerSlotCol;
            slotRow = playerSlotRow;
        } else {
            frameX = game.tileSize / 2;
            frameY = game.tileSize / 2;
            frameWidth = game.tileSize * 6;
            frameHeight = game.tileSize * 6;
            slotCol = entitySlotCol;
            slotRow = entitySlotRow;
        }
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // Slots
        final int slotXStart = frameX + 20;
        final int slotYStart = frameY + 20;
        int slotX = slotXStart;
        int slotY = slotYStart;
        int slotSize = game.tileSize + 3;

        // Draw Items
        for (int i = 0; i < entity.inventory.size(); i++) {
            // Equip Cursor
            if (entity.inventory.get(i) == entity.currentWeapon ||
                    entity.inventory.get(i) == entity.currentShield ||
                    entity.inventory.get(i) == entity.currentLight) {
                graphics2D.setColor(new Color(240, 190, 90));
                graphics2D.fillRoundRect(slotX, slotY, game.tileSize, game.tileSize, 10, 10);
            }

            graphics2D.drawImage(entity.inventory.get(i).down1, slotX, slotY, null);

            // Draw Amount
            if (entity.inventory.get(i).amount > 1) {
                graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD, 28f));
                int amountX;
                int amountY;

                String string;
                if (Objects.equals(entity.inventory.get(i).name, "Coins")) {
                    string = "" + entity.coins;
                } else {
                    string = "" + entity.inventory.get(i).amount;
                }
                amountX = alignXToRight(string, slotX + 44);
                amountY = slotY + game.tileSize;

                // Shadow
                graphics2D.setColor(new Color(60, 60, 60));
                graphics2D.drawString(string, amountX, amountY);

                // Text
                graphics2D.setColor(Color.white);
                graphics2D.drawString(string, amountX - 3, amountY - 3);
            }

            slotX += slotSize;

            if (i == 4 || i == 9 || i == 14 || i == 19) {
                slotX = slotXStart;
                slotY += slotSize;
            }
        }

        // Cursor
        if (cursor) {
            int cursorX = slotXStart + (slotSize * slotCol);
            int cursorY = slotYStart + (slotSize * slotRow);
            int cursorWidth = game.tileSize;
            int cursorHeight = game.tileSize;

            // Draw Cursor
            graphics2D.setColor(Color.white);
            graphics2D.setStroke(new BasicStroke(3));
            graphics2D.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);

            // Description Frame
            int dFrameY = frameY + frameHeight;
            int dFrameHeight = game.tileSize * 4 - (game.tileSize / 2);

            // Description Text
            int textX = frameX + 20;
            int textY = dFrameY + game.tileSize;

            int itemIndex = getItemIndex(slotCol, slotRow);
            if (itemIndex < entity.inventory.size()) {
                // Window + Title
                drawSubWindow(frameX, dFrameY, frameWidth, dFrameHeight);
                graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD, 30f));
                graphics2D.drawString(entity.inventory.get(itemIndex).name, textX, textY);
                textY += 40;

                // Description
                graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN, 20f));
                for (String line : entity.inventory.get(itemIndex).description.split("\n")) {
                    graphics2D.drawString(line, textX, textY);
                    textY += 30;
                }
            }
        }
    }
    public void drawGameOverScreen() {
        graphics2D.setColor(new Color(0.1f, 0, 0, 0.94f));
        graphics2D.fillRect(0, 0, game.screenWidth, game.screenHeight);

        int x;
        int y;
        String text;
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD, 110f));
        text = "Game Over";

        // Shadow
        graphics2D.setColor(new Color(147, 37, 37));
        x = getCentreX(text);
        y = game.tileSize * 4;
        graphics2D.drawString(text, x, y);

        // Main
        graphics2D.setColor(new Color(209, 25, 25));
        graphics2D.drawString(text, x - 4, y -4);


        // Restart
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN, 50f));
        graphics2D.setColor(Color.white);
        text = "Restart";
        x = getCentreX(text);
        y += game.tileSize * 4;
        graphics2D.drawString(text, x, y);
        if (commandNumber == 0) {
            graphics2D.drawString(">", x - 40, y);
        }

        // Respawn
        if (game.gameMode.equals("Easy")) {
            text = "Respawn";
            x = getCentreX(text);
            y += 55;
            graphics2D.drawString(text, x, y);
            if (commandNumber == 1) {
                graphics2D.drawString(">", x - 40, y);
            }
        }

        // Quit
        text = "Quit";
        x = getCentreX(text);
        y += 55;
        graphics2D.drawString(text, x, y);
        if (commandNumber == game.inputHandler.maxCommandNumber) {
            graphics2D.drawString(">", x - 40, y);
        }
    }
    public void drawExceptionScreen() {
        // Background
        graphics2D.setColor(new Color(0.1f, 0, 0, 0.94f));
        graphics2D.fillRect(0, 0, game.screenWidth, game.screenHeight);

        // Sub-Window
        int frameX = game.tileSize * 6;
        int frameY = game.tileSize * 3;
        int frameWidth = game.tileSize * 8;
        int frameHeight = game.tileSize * 6;

        if (game.exceptionState == States.ExceptionStates.IGNORABLE_QUITABLE) {
            frameHeight += 40;
        }

        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // Title
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD, 48f));
        String text = "Exception Thrown!";
        int textX = getCentreX(text);
        int textY = frameY + game.tileSize + (game.tileSize / 4);
        graphics2D.drawString(text, textX, textY);

        // Text
        textX = frameX + game.tileSize / 2;
        textY = frameY + game.tileSize * 2 + (game.tileSize / 4);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN,28f));

        graphics2D.drawString("This is likely not your fault, please", textX, textY);
        textY += 40;
        graphics2D.drawString("make an issue about this on GitHub", textX, textY);
        textY += 40;
        graphics2D.drawString("with the stack trace. Thank You!", textX, textY);

        // Copy Stack Trace
        textY = frameY = game.tileSize * 8 - (game.tileSize / 4);
        textX += game.tileSize / 2;
        graphics2D.drawString("Copy Stack Trace", textX, textY);
        if (commandNumber == 0) {
            graphics2D.drawString(">", textX - 30, textY);
            if (game.inputHandler.spacePressed) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(game.exceptionStackTrace), null);
            }
        }

        // Ignore
        if (game.exceptionState == States.ExceptionStates.IGNORABLE_QUITABLE ||
                game.exceptionState == States.ExceptionStates.ONLY_IGNORABLE) {
            textY += 40;
            graphics2D.drawString("Ignore", textX, textY);
            if (commandNumber == 1) {
                graphics2D.drawString(">", textX - 30, textY);
                if (game.inputHandler.spacePressed) {
                    game.gameState = States.GameStates.PLAY;
                    commandNumber = 0;
                    game.player.attacking = false;

                    // Switch to the following exception state to avoid an infinite loop
                    game.exceptionState = switch (game.exceptionState) {
                        case NOTHING -> States.ExceptionStates.ONLY_IGNORABLE;
                        case ONLY_IGNORABLE -> States.ExceptionStates.IGNORABLE_QUITABLE;
                        case IGNORABLE_QUITABLE -> States.ExceptionStates.ONLY_QUITABLE;
                        case ONLY_QUITABLE -> States.ExceptionStates.INSTANT_QUIT;
                        case INSTANT_QUIT -> States.ExceptionStates.NOTHING;
                    };
                }
            }
        }
        // Quit
        if (game.exceptionState == States.ExceptionStates.IGNORABLE_QUITABLE ||
                game.exceptionState == States.ExceptionStates.ONLY_QUITABLE) {
            textY += 40;
            graphics2D.drawString("Quit", textX, textY);
            if (commandNumber == 2 || (commandNumber == 1 && game.exceptionState == States.ExceptionStates.ONLY_QUITABLE)) {
                graphics2D.drawString(">", textX - 30, textY);
                if (game.inputHandler.spacePressed) {
                    System.exit(0);
                }
            }
        }
        
        game.inputHandler.spacePressed = false;
    }
    public void drawTransitionScreen() {
        // Update transition counter
        if (!fadeBack) {
            transitionCounter = Math.min(transitionCounter + transitionOpenSpeed, 1f);
        } else {
            transitionCounter = Math.max(transitionCounter - transitionCloseSpeed, 0f);
        }

        // Handle state transitions
        if (transitionCounter == 1f && !fadeBack) {
            invokeActionMethod();
            fadeBack = true;
            if (game.environmentManager.lighting.darknessState == States.DarknessStates.DUSK ||
                    game.environmentManager.lighting.darknessState == States.DarknessStates.NEW_DUSK) {
                Sound.playMapMusic(game.environmentManager.lighting.darknessState);
            }
        } else if (transitionCounter == 0f && fadeBack) {
            transitioning = false;
            fadeBack = false;
        }

        // Draw transition effect
        float alpha = transitionCounter;
        graphics2D.setColor(new Color(
                transitionColor.getRed() / 255f,
                transitionColor.getGreen() / 255f,
                transitionColor.getBlue() / 255f,
                alpha
        ));
        graphics2D.fillRect(0, 0, game.screenWidth, game.screenHeight);
    }
    public void setTransitionSettings(Color color, float openSpeed, float closeSpeed) {
        transitionColor = color;
        transitionOpenSpeed = openSpeed;
        transitionCloseSpeed = closeSpeed;
    }
    public boolean isFadingBack() {
        return fadeBack;
    }
    public void drawTradeScreen() {
        switch (subUIState) {
            case "Select": drawTradeSelectScreen(); break;
            case "Buy": drawTradeBuyScreen(); break;
            case "Sell": drawTradeSellScreen(); break;
        }
        game.inputHandler.spacePressed = false;
    }
    public void drawTradeSelectScreen() {
        drawDialogueScreen();

        // Draw Window
        int x = game.tileSize * 15 + game.tileSize / 2;
        int y = (game.tileSize * 8) - (game.tileSize / 4);
        int width = game.tileSize * 3;
        int height = (game.tileSize * 3) + (game.tileSize / 2);
        drawSubWindow(x, y, width, height);

        // Buy Text
        x += game.tileSize;
        y += game.tileSize;
        graphics2D.drawString("Buy", x, y);
        if (commandNumber == 0) {
            graphics2D.drawString(">", x - 24, y);
            if (game.inputHandler.spacePressed) {
                subUIState = "Buy";
            }
        }

        // Sell Text
        y += game.tileSize;
        graphics2D.drawString("Sell", x, y);
        if (commandNumber == 1) {
            graphics2D.drawString(">", x - 24, y);
            if (game.inputHandler.spacePressed) {
                subUIState = "Sell";
            }
        }

        // Leave Text
        y += game.tileSize;
        graphics2D.drawString("Leave", x, y);
        if (commandNumber == 2) {
            graphics2D.drawString(">", x - 24, y);
            if (game.inputHandler.spacePressed) {
                game.ui.uiState = States.UIStates.JUST_DEFAULT;
                game.ui.commandNumber = 0;
            }
        }
    }
    public void drawTradeBuyScreen() {
        // Inventories
        drawInventory(game.player, false);
        drawInventory(npc, true);

        // Hint Window
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN, 30f));
        int x = (game.tileSize * 13) + (game.tileSize / 2);
        int y = (game.tileSize * 9) + (game.tileSize / 2);
        int width = game.tileSize * 6;
        int height = game.tileSize * 2;
        drawSubWindow(x, y, width, height);
        graphics2D.drawString("[ESC] to Exit", x + 24, y + 60);

        // Price Window
        graphics2D.setFont(graphics2D.getFont().deriveFont(27f));
        int itemIndex = getItemIndex(entitySlotCol, entitySlotRow);
        if (itemIndex < npc.inventory.size()) {
            x = game.tileSize * 5;
            y = game.tileSize * 6;
            width = (game.tileSize * 2) + (game.tileSize / 2);
            height = game.tileSize;
            drawSubWindow(x, y, width, height);
            graphics2D.drawImage(coin, x + 10, y + 8, 32, 32, null);

            int price = npc.inventory.get(itemIndex).price;
            String text = String.valueOf(price);
            x = alignXToRight(text, game.tileSize * 7 - 20);
            graphics2D.drawString(text, x, y + 34);

            // Buy an item
            if (game.inputHandler.spacePressed) {
                if (npc.inventory.get(itemIndex).price > game.player.coins) {
                    setCurrentDialogue("Sorry partner, your wallet declined :(");
                    uiState = States.UIStates.DIALOGUE;
                    game.ui.commandNumber = 0;
                } else if (game.player.canObtainItem(npc.inventory.get(itemIndex))) {
                    game.player.coins -= price;
                } else {
                    setCurrentDialogue("Sorry partner, I don't think you can \ncarry this :(");
                    uiState = States.UIStates.DIALOGUE;
                    game.ui.commandNumber = 0;
                }
            }
        }
    }
    public void drawTradeSellScreen() {
        // Inventory
        drawInventory(game.player, true);

        // Hint Window
        graphics2D.setFont(graphics2D.getFont().deriveFont(Font.PLAIN, 30f));
        int x = game.tileSize / 2;
        int y = (game.tileSize * 9) + (game.tileSize / 2);
        int width = game.tileSize * 6;
        int height = game.tileSize * 2;
        drawSubWindow(x, y, width, height);
        graphics2D.drawString("[ESC] to Exit", x + 24, y + 60);

        // Price Window
        graphics2D.setFont(graphics2D.getFont().deriveFont(27f));
        int itemIndex = getItemIndex(playerSlotCol, playerSlotRow);
        if (itemIndex < game.player.inventory.size()) {
            x = game.tileSize * 13;
            y = game.tileSize * 6;
            width = (game.tileSize * 2) + (game.tileSize / 2);
            height = game.tileSize;
            drawSubWindow(x, y, width, height);
            graphics2D.drawImage(coin, x + 10, y + 8, 32, 32, null);

            int price = game.player.inventory.get(itemIndex).price / 2;
            String text = String.valueOf(price);
            x = alignXToRight(text, game.tileSize * 15 - 20);
            graphics2D.drawString(text, x, y + 34);

            // Sell an item
            if (game.inputHandler.spacePressed) {
                if (game.player.inventory.get(itemIndex) == game.player.currentWeapon ||
                        game.player.inventory.get(itemIndex) == game.player.currentShield ||
                        game.player.inventory.get(itemIndex) == game.player.currentLight) {
                    setCurrentDialogue("Sorry partner, I can't buy equipped \nitems :(");
                    uiState = States.UIStates.DIALOGUE;
                    game.ui.commandNumber = 0;
                } else if (game.player.inventory.get(itemIndex).tags.contains(EntityTags.TAG_NON_SELLABLE)) {
                    setCurrentDialogue("Sorry partner, I can't buy this item :(");
                    uiState = States.UIStates.DIALOGUE;
                    game.ui.commandNumber = 0;
                } else {
                    game.player.coins += price;
                    if (game.player.inventory.get(itemIndex).amount > 1) {
                        game.player.inventory.get(itemIndex).amount--;
                    } else {
                        game.player.inventory.remove(itemIndex);
                    }
                }
            }
        }
    }
    public void drawMapScreen() {
        int width = 500;
        int height = 500;
        int x = game.screenWidth / 2 - width / 2;
        int y = game.screenHeight / 2 - height / 2;
        drawSubWindow(x - 20, y - 20, width + 40, height + 40);
        graphics2D.drawImage(MapHandler.worldMap.get(game.currentMap), x, y, width, height, null);
    }

    // Action
    @SuppressWarnings("unused")
    public void yesGameEnd() {
        game.gameState = States.GameStates.TITLE;
        uiState = States.UIStates.JUST_DEFAULT;
        subUIState = "Main Title";
        Sound.music.stop();
        Sound.playMusic("Tech Geek");
        game.restart();
    }
    @SuppressWarnings("unused")
    public void yesFullScreen() {
        game.fullScreen = !game.fullScreen;
        subUIState = "Notification";
        commandNumber = 0;
        setCurrentDialogue("Full Screen will only be \nenabled/disabled when \nrelaunching the game.");
    }
    @SuppressWarnings("unused")
    public void yesBRendering() {
        game.BRendering = !game.BRendering;
        commandNumber = 0;

        if (game.BRendering) {
            subUIState = "Notification";
            setCurrentDialogue("You can emergency \ndisable BRendering by \npressing F3 and R.");
        } else {
            subUIState = "Settings Main";
        }
    }
    @SuppressWarnings("unused")
    public void yesPathfinding() {
        game.pathFinding = !game.pathFinding;
        subUIState = "Settings Main";
        commandNumber = 0;
    }
    @SuppressWarnings("unused")
    public void transitionTeleport() {
        game.currentMap = EVT_Teleport.nextMap;

        // Set player position
        if (EVT_Teleport.nextCol == Integer.MIN_VALUE || EVT_Teleport.nextRow == Integer.MIN_VALUE) {
            game.player.setDefaultPosition();
        } else {
            game.player.worldX = EVT_Teleport.nextCol;
            game.player.worldY = EVT_Teleport.nextRow;
        }
        game.player.direction = EVT_Teleport.nextDirection;
        game.environmentManager.lightUpdated = true;

        // Play map music
        Sound.playMapMusic();

        // Load entities and events if not already loaded
        if (game.object.getOrDefault(game.currentMap, null) == null) {
            AssetSetter.setObjects(false);
        }
        if (game.npc.getOrDefault(game.currentMap, null) == null) {
            AssetSetter.setNPCs(false);
        }
        if (game.monster.getOrDefault(game.currentMap, null) == null) {
            AssetSetter.setMonsters(false);
        }
        AssetSetter.setEvents();
    }
    @SuppressWarnings("unused")
    public void transitionDarkness() {
        game.environmentManager.lightUpdated = true;
    }

    // Helpers
    public int getItemIndex(int slotCol, int slotRow) {
        return slotCol + (slotRow * 5);
    }
    /// Draws a sub-window of any size and at any position
    public void drawSubWindow(int x, int y, int width, int height) {
        Color color = new Color(0,0,0, 210);
        graphics2D.setColor(color);
        graphics2D.fillRoundRect(x, y, width, height, 35, 35);

        color = Color.WHITE;
        graphics2D.setColor(color);
        graphics2D.setStroke(new BasicStroke(5));
        graphics2D.drawRoundRect(x + 5, y + 5, width - 10, height - 10 ,25, 25);
    }
    /// Invokes the method defined in the "action method" variable
    public void invokeActionMethod() {
        try {
            Method method = this.getClass().getMethod(actionMethod);
            method.invoke(this);
        } catch (NoSuchMethodException exception) {
            System.err.println("There is no such method as '" + actionMethod + "' in the UI class.");
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    public int getCentreX(String text) {
        int length = (int) graphics2D.getFontMetrics().getStringBounds(text, graphics2D).getWidth();
        return game.screenWidth / 2 - length / 2;

    }
    public int alignXToRight(String text, int tailX) {
        int length = (int) graphics2D.getFontMetrics().getStringBounds(text, graphics2D).getWidth();
        return tailX - length;
    }
}
