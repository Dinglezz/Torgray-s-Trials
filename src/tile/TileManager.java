package tile;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class TileManager {
    GamePanel gamePanel;
    public HashMap<Integer, Tile> tile;
    public int[][] mapTileNum;

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        tile = new HashMap<>();
        mapTileNum = new int[gamePanel.maxWorldCol][gamePanel.maxWorldRow];
        getTileImage();
        loadMap("/maps/world02.txt");
    }

    public void getTileImage() {

        // Grass
        registerTile(10, "grass_1", false);
        registerTile(11, "grass_2", false);

        // Water
        registerTile(12, "water", true);
        registerTile(13, "white_line_water", true);
        registerTile(14, "water_corner_1", true);
        registerTile(15, "water_edge_3", true);
        registerTile(16, "water_corner_3", true);
        registerTile(17, "water_edge_4", true);
        registerTile(18, "water_edge_2", true);
        registerTile(19, "water_corner_2", true);
        registerTile(20, "water_edge_1", true);
        registerTile(21, "water_corner_4", true);
        registerTile(22, "water_outer_corner_1", true);
        registerTile(23, "water_outer_corner_2", true);
        registerTile(24, "water_outer_corner_3", true);
        registerTile(25, "water_outer_corner_4", true);

        // Path
        registerTile(26, "path", false);
        registerTile(27, "path_corner_1", false);
        registerTile(28, "path_edge_1", false);
        registerTile(29, "path_corner_2", false);
        registerTile(30, "path_edge_4", false);
        registerTile(31, "path_edge_2", false);
        registerTile(32, "path_corner_3", false);
        registerTile(33, "path_edge_3", false);
        registerTile(34, "path_corner_4", false);
        registerTile(35, "path_outer_corner_1", false);
        registerTile(36, "path_outer_corner_2", false);
        registerTile(37, "path_outer_corner_3", false);
        registerTile(38, "path_outer_corner_4", false);

        // Dirt
        registerTile(39, "dirt", false);

        // Wall
        registerTile(40, "wall", true);

        // Tree
        registerTile(41, "tree", true);

        // Event Ties
        registerTile(42, "path_pit", false);
        registerTile(43, "grass_pit", false);
        registerTile(44, "grass_healing", false);
    }
    public void registerTile(int i, String imageName, boolean collision) {
        UtilityTool uTool = new UtilityTool();

        try {
            tile.put(i, new Tile());
            tile.get(i).image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png"));
            tile.get(i).image = uTool.scaleImage(tile.get(i).image, gamePanel.tileSize, gamePanel.tileSize);
            tile.get(i).collision = collision;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;

            while (col < gamePanel.maxWorldCol && row < gamePanel.maxWorldRow) {
                String line = br.readLine();
                while (col < gamePanel.maxWorldCol) {
                    String[] numbers = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                if (col == gamePanel.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gamePanel.maxWorldCol && worldRow < gamePanel.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow];
            int worldX = worldCol * gamePanel.tileSize;
            int worldY = worldRow * gamePanel.tileSize;
            int screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
            int screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;

            if (worldX + gamePanel.tileSize > gamePanel.player.worldX - gamePanel.player.screenX &&
                    worldX - gamePanel.tileSize < gamePanel.player.worldX + gamePanel.player.screenX &&
                    worldY + gamePanel.tileSize > gamePanel.player.worldY - gamePanel.player.screenY &&
                    worldY - gamePanel.tileSize < gamePanel.player.worldY + gamePanel.player.screenY) {
                g2.drawImage(tile.get(tileNum).image, screenX, screenY, null);
            }
            worldCol++;
            if (worldCol == gamePanel.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}
