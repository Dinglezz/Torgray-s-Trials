package net.dinglezz.torgrays_trials.main;

import net.dinglezz.torgrays_trials.entity.Entity;
import net.dinglezz.torgrays_trials.entity.LootTableHandler;
import net.dinglezz.torgrays_trials.entity.Player;
import net.dinglezz.torgrays_trials.environment.EnvironmentManager;
import net.dinglezz.torgrays_trials.pathfinding.Pathfinder;
import net.dinglezz.torgrays_trials.tile.MapHandler;
import net.dinglezz.torgrays_trials.tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.util.*;

public class Game extends JPanel implements Runnable {
    // Screen settings
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public int screenWidth = tileSize * maxScreenCol; // 960 pixels
    public int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // Word Settings
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // Full Screen
    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    Graphics2D graphics2D;
    
    // Settings
    public boolean fullScreen;
    public boolean BRendering;
    public boolean pathFinding;

    // Debug
    public boolean debug = false;
    public boolean debugPathfinding = false;
    public boolean debugHitBoxes = false;

    // FPS
    int FPS = 60;
    public long drawStart;

    // System
    public UI ui = new UI(this);
    public Config config = new Config(this);
    public Pathfinder pathFinder = new Pathfinder(this);
    public InputHandler inputHandler = new InputHandler(this);
    public EnvironmentManager environmentManager = new EnvironmentManager(this);
    Thread gameThread;

    // Entities and Objects
    public Player player = new Player(this, inputHandler);
    public HashMap<String, HashMap<Integer, Entity>> npc = new HashMap<>();
    public HashMap<String, HashMap<Integer, Entity>> object = new HashMap<>();
    public HashMap<String, HashMap<Integer, Entity>> monster = new HashMap<>();
    public ArrayList<Entity> particleList = new ArrayList<>();
    public ArrayList<Entity> entityList = new ArrayList<>();

    public States.GameStates gameState = States.GameStates.TITLE;
    public States.ExceptionStates exceptionState = States.ExceptionStates.ONLY_IGNORABLE;
    public String exceptionStackTrace = "";
    public String currentMap = "Main Island";
    public String gameMode;

    public Game() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(inputHandler);
        this.setFocusable(true);
    }

    public void setupGame() {
        setupExceptionHandling();
        TileManager.setup();
        MapHandler.loadMaps();
        LootTableHandler.loadLootTables();
        environmentManager.setup();

        // Set Assets
        AssetSetter.setObjects(true);
        AssetSetter.setNPCs(true);
        AssetSetter.setMonsters(true);

        Sound.playMusic("Tech Geek");
        gameState = States.GameStates.TITLE;
        player.setDefaultPosition();

        // Load Config
        if (fullScreen) {
            setFullScreen();
        }
        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        graphics2D = (Graphics2D)tempScreen.getGraphics();
    }
    private void setupExceptionHandling() {
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            if (exceptionState != States.ExceptionStates.NOTHING) {
                // Console stuff (If anyone actually sees this)
                System.err.println("Torgray's Trials has encountered an error!");
                System.err.println("This is likely not your fault, please create an issue on GitHub with the error below.");
                System.err.println("---------------------------------------------------------------------------------------");
                exception.printStackTrace();
                exceptionStackTrace = "Error Message: '" + exception.getMessage() + "' Stack Trace: " + Arrays.toString(exception.getStackTrace());

                if (exceptionState != States.ExceptionStates.INSTANT_QUIT) {
                    // Display the error message
                    startGameThread();
                    gameState = States.GameStates.EXCEPTION;
                    ui.commandNumber = 0;
                } else {
                    // Welp, guess we're quitting
                    System.exit(1);
                }
            } else {
                // If the state is nothing, just forget the exception ever happened and restarted the game thread
                startGameThread();
            }
        });
    }
    public void respawn() {
        player.restoreHealth();
        currentMap = "Main Island";
        player.setDefaultPosition();
        environmentManager.lightUpdated = true;
    }
    public void restart() {
        player.setDefaultValues();
        player.setItems();
        AssetSetter.setObjects(true);
        AssetSetter.setNPCs(true);
        AssetSetter.setMonsters(true);
        currentMap = "Main Island";
        player.setDefaultPosition();

        // Darkness reset
        environmentManager.lightUpdated = true;
        environmentManager.lighting.darknessCounter = 0;
        environmentManager.lighting.darknessState = States.DarknessStates.NIGHT;
        environmentManager.lighting.nextGloom = environmentManager.lighting.chooseNextGloom();
    }
    public void setFullScreen() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        graphicsDevice.setFullScreenWindow(Main.window);

        screenWidth2 = Main.window.getWidth();
        screenHeight2 = Main.window.getHeight();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        final double drawInterval = 1_000_000_000.0 / FPS;
        long lastTime = System.nanoTime();
        long timer = 0;
        double delta = 0;

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            long elapsedTime = currentTime - lastTime;
            lastTime = currentTime;

            delta += elapsedTime / drawInterval;
            timer += elapsedTime;

            while (delta >= 1) {
                update();
                if (BRendering && gameState != States.GameStates.TITLE) {
                    drawToTempScreen();
                    drawToScreen();
                } else {
                    repaint();
                }
                delta--;
            }

            if (timer >= 1_000_000_000) {
                timer = 0;
            }
        }
    }
    public void update() {
        if (gameState == States.GameStates.PLAY ||
                gameState == States.GameStates.CHARACTER ||
                gameState == States.GameStates.DIALOGUE ||
                gameState == States.GameStates.TRADE ||
                gameState == States.GameStates.MAP) {

            // Player
            if (gameState != States.GameStates.TRADE) {
                player.update();
            }

            // NPCs
            npc.getOrDefault(currentMap, new HashMap<>()).values().stream()
                    .filter(Objects::nonNull)
                    .forEach(Entity::update);

            // Monsters
            monster.getOrDefault(currentMap, new HashMap<>()).forEach((key, entity) -> {
                if (entity != null) {
                    if (entity.alive && !entity.dying) {
                        entity.update();
                    } else if (!entity.alive) {
                        entity.checkDrop();
                        monster.get(currentMap).put(key, null);

                        // Respawn if all monsters are dead
                        if (monster.get(currentMap).values().stream().allMatch(Objects::isNull)) {
                            AssetSetter.setMonsters(false);
                        }
                    }
                }
            });

            // Particles
            particleList.removeIf(particle -> particle == null || !particle.alive);
            particleList.forEach(Entity::update);

            environmentManager.update();
        }
    }

    public void drawToTempScreen() {
        // Debug
        drawStart = 0;
        if (debug) {
            drawStart = System.nanoTime();
        }

        // Title Screen
        if (gameState == States.GameStates.TRADE) {
            ui.draw(graphics2D);
        } else {
            // Draw :)
            TileManager.draw(graphics2D);

            // Add entities to the list
            entityList.clear(); // Clear once at the start
            if (gameState != States.GameStates.GAME_OVER) {
                entityList.add(player);
            }
            npc.getOrDefault(currentMap, new HashMap<>()).values().stream().filter(Objects::nonNull).forEach(entityList::add);
            object.getOrDefault(currentMap, new HashMap<>()).values().stream().filter(Objects::nonNull).forEach(entityList::add);
            monster.getOrDefault(currentMap, new HashMap<>()).values().stream().filter(Objects::nonNull).forEach(entityList::add);
            entityList.addAll(particleList);

            // Sort and draw entities
            entityList.stream()
                    .sorted(Comparator.comparingInt(e -> e.worldY))
                    .forEach(entity -> entity.draw(graphics2D));

            // Empty Entity List
            entityList.clear();

            // More  drawing :D
            environmentManager.draw(graphics2D);
            ui.draw(graphics2D);
        }
    }

    public void drawToScreen() {
        Graphics graphics = getGraphics();
        graphics.drawImage(tempScreen,0,0,screenWidth2, screenHeight2, null);
        graphics.dispose();
    }
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        if (debug) {
            drawStart = System.nanoTime();
        }

        if (gameState == States.GameStates.TITLE) {
            ui.draw(graphics2D);
        } else {
            TileManager.draw(graphics2D);

            // Add entities to the list
            if (gameState != States.GameStates.GAME_OVER) {
                entityList.add(player);
            }
            npc.getOrDefault(currentMap, new HashMap<>()).values().stream().filter(Objects::nonNull).forEach(entityList::add);
            object.getOrDefault(currentMap, new HashMap<>()).values().stream().filter(Objects::nonNull).forEach(entityList::add);
            monster.getOrDefault(currentMap, new HashMap<>()).values().stream().filter(Objects::nonNull).forEach(entityList::add);
            entityList.addAll(particleList);

            // Sort and draw entities
            entityList.stream()
                    .sorted(Comparator.comparingInt(e -> e.worldY))
                    .forEach(entity -> entity.draw(graphics2D));

            entityList.clear();

            environmentManager.draw(graphics2D);
            ui.draw(graphics2D);
        }
        graphics2D.dispose();
    }
 }
