package net.dinglezz.torgrays_trials.main;

import net.dinglezz.torgrays_trials.entity.Entity;
import net.dinglezz.torgrays_trials.tile.TileManager;
import net.dinglezz.torgrays_trials.tile.TilePoint;

import java.util.ArrayList;
import java.util.HashMap;

public class CollisionChecker {
    public static void checkTile(Entity entity) {
        if (TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, 0, 0)) == null) {
            return;
        }

        int entityLeftWordX = entity.worldX + entity.solidArea.x;
        int entityRightWordX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWordY = entity.worldY + entity.solidArea.y;
        int entityBottomWordY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWordX / Main.game.tileSize;
        int entityRightCol = entityRightWordX / Main.game.tileSize;
        int entityTopRow = entityTopWordY / Main.game.tileSize;
        int entityBottomRow = entityBottomWordY / Main.game.tileSize;

        int tileNumber1, tileNumber2;

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWordY - entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityTopRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityTopRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                break;
            case  "up left":
                entityTopRow = (entityTopWordY - entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityTopRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityTopRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                entityLeftCol = (entityLeftWordX - entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityTopRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityBottomRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                break;
            case "up right":
                entityTopRow = (entityTopWordY - entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityTopRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityTopRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                entityRightCol = (entityRightWordX + entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityTopRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityBottomRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomWordY + entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityBottomRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityBottomRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                break;
            case "down left":
                entityBottomRow = (entityBottomWordY + entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityBottomRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityBottomRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                entityLeftCol = (entityLeftWordX - entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityTopRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityBottomRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                break;
            case  "down right":
                entityBottomRow = (entityBottomWordY + entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityBottomRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityBottomRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                entityRightCol = (entityRightWordX + entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityBottomRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityBottomRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entityLeftWordX - entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityTopRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityBottomRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entityRightCol = (entityRightWordX + entity.speed) / Main.game.tileSize;
                tileNumber1 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityLeftCol, entityTopRow));
                tileNumber2 = TileManager.mapTileNumbers.get("foreground").get(new TilePoint(Main.game.currentMap, entityRightCol, entityBottomRow));
                if (TileManager.tile.get(tileNumber1).collision || TileManager.tile.get(tileNumber2).collision) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

public static int checkObject(Entity entity, boolean player) {
    int index = 999;

    for (int i = 0; i < Main.game.object.get(Main.game.currentMap).size(); i++) {
        Entity object = Main.game.object.get(Main.game.currentMap).get(i);
        if (object != null) {
            entity.solidArea.x = entity.worldX + entity.solidAreaDefaultX;
            entity.solidArea.y = entity.worldY + entity.solidAreaDefaultY;
            object.solidArea.x = object.worldX + object.solidAreaDefaultX;
            object.solidArea.y = object.worldY + object.solidAreaDefaultY;

            switch (entity.direction) {
                case "up": entity.solidArea.y -= entity.speed; break;
                case "down": entity.solidArea.y += entity.speed; break;
                case "left": entity.solidArea.x -= entity.speed; break;
                case "right": entity.solidArea.x += entity.speed; break;
            }
            if (entity.solidArea.intersects(object.solidArea)) {
                if (object.collision) {
                    entity.collisionOn = true;
                }
                if (player) {
                    index = i;
                }
            }

            entity.solidArea.x = entity.solidAreaDefaultX;
            entity.solidArea.y = entity.solidAreaDefaultY;
            object.solidArea.x = object.solidAreaDefaultX;
            object.solidArea.y = object.solidAreaDefaultY;
        }
    }

    return index;
}

public static int checkEntity(Entity entity, HashMap<String, ArrayList<Entity>> target) {
    int index = 999;
    if (target.get(Main.game.currentMap) != null) {
        for (int i = 0; i < target.get(Main.game.currentMap).size(); i++) {
            Entity checkedEntity = target.get(Main.game.currentMap).get(i);
            if (checkedEntity != null) {
                entity.solidArea.x = entity.worldX + entity.solidAreaDefaultX;
                entity.solidArea.y = entity.worldY + entity.solidAreaDefaultY;
                checkedEntity.solidArea.x = checkedEntity.worldX + checkedEntity.solidAreaDefaultX;
                checkedEntity.solidArea.y = checkedEntity.worldY + checkedEntity.solidAreaDefaultY;

                switch (entity.direction) {
                    case "up": entity.solidArea.y -= entity.speed; break;
                    case "down": entity.solidArea.y += entity.speed; break;
                    case "left": entity.solidArea.x -= entity.speed; break;
                    case "right": entity.solidArea.x += entity.speed; break;
                }
                if (entity.solidArea.intersects(checkedEntity.solidArea)) {
                    if (checkedEntity != entity) {
                        entity.collisionOn = true;
                        index = i;
                    }
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                checkedEntity.solidArea.x = checkedEntity.solidAreaDefaultX;
                checkedEntity.solidArea.y = checkedEntity.solidAreaDefaultY;
            }
        }
    }

    return index;
}
    public static boolean checkPlayer(Entity entity) {
        boolean contactPlayer = false;

        entity.solidArea.x = entity.worldX + entity.solidArea.x;
        entity.solidArea.y = entity.worldY + entity.solidArea.y;
        Main.game.player.solidArea.x = Main.game.player.worldX + Main.game.player.solidArea.x;
        Main.game.player.solidArea.y = Main.game.player.worldY + Main.game.player.solidArea.y;

        switch (entity.direction) {
            case "up": entity.solidArea.y -= entity.speed; break;
            case "down": entity.solidArea.y += entity.speed; break;
            case "left": entity.solidArea.x -= entity.speed; break;
            case "right": entity.solidArea.x += entity.speed; break;
        }
        if (entity.solidArea.intersects(Main.game.player.solidArea)) {
            entity.collisionOn = true;
            contactPlayer = true;
        }
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        Main.game.player.solidArea.x = Main.game.player.solidAreaDefaultX;
        Main.game.player.solidArea.y = Main.game.player.solidAreaDefaultY;
        return contactPlayer;
    }
}
