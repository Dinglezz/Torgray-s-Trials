package net.dinglezz.torgrays_trials.mob;

import net.dinglezz.torgrays_trials.entity.Entity;
import net.dinglezz.torgrays_trials.entity.EntityTypes;
import net.dinglezz.torgrays_trials.main.Game;
import net.dinglezz.torgrays_trials.object.OBJ_Coin;
import net.dinglezz.torgrays_trials.object.OBJ_Torgray_Soup;

import java.awt.*;
import java.util.Random;

public class MOB_Dracore extends Entity {
    Game game;

    public MOB_Dracore(Game game) {
        super(game);
        this.game = game;

        name = "Dracore";
        type = EntityTypes.TYPE_MOB;
        speed = 1;
        maxHealth = 5;
        health = maxHealth;
        attack = 4;
        defence = 0;
        exp = 2;
        
        getImage();
    }
    public void getImage() {
        up1 = registerEntitySprite("/drawable/mob/dracore_1", game.tileSize, game.tileSize);
        up2 = registerEntitySprite("/drawable/mob/dracore_2", game.tileSize, game.tileSize);
        up3 = registerEntitySprite("/drawable/mob/dracore_3", game.tileSize, game.tileSize);
        
        down1 = registerEntitySprite("/drawable/mob/dracore_1", game.tileSize, game.tileSize);
        down2 = registerEntitySprite("/drawable/mob/dracore_2", game.tileSize, game.tileSize);
        down3 = registerEntitySprite("/drawable/mob/dracore_3", game.tileSize, game.tileSize);
        
        left1 = registerEntitySprite("/drawable/mob/dracore_1", game.tileSize, game.tileSize);
        left2 = registerEntitySprite("/drawable/mob/dracore_2", game.tileSize, game.tileSize);
        left3 = registerEntitySprite("/drawable/mob/dracore_3", game.tileSize, game.tileSize);
        
        right1 = registerEntitySprite("/drawable/mob/dracore_1", game.tileSize, game.tileSize);
        right2 = registerEntitySprite("/drawable/mob/dracore_2", game.tileSize, game.tileSize);
        right3 = registerEntitySprite("/drawable/mob/dracore_3", game.tileSize, game.tileSize);
    }
    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter == 120) {
            Random random = new Random();
            int i = random.nextInt(100) + 1; // Pick a number from 1 to 100

            if (i <= 25) {
                direction = "up";
            }
            if (i > 25 && i <= 50) {
                direction = "down";
            }
            if (i > 50 && i <= 75) {
                direction = "left";
            }
            if (i > 75 && i <= 100) {
                direction = "right";
            }
            actionLockCounter = 0;
        }
    }
    public void damageReaction() {
        actionLockCounter = 0;

        switch (game.player.direction) {
            case "up": direction = "down"; break;
            case "down": direction = "up"; break;
            case "left": direction = "right"; break;
            case "right": direction = "left"; break;
        }
    }
    public void checkDrop() {
        int i = new Random().nextInt(3) + 1;

        // Set mob drop
        if (i == 1) {
            dropItem(new OBJ_Torgray_Soup(game));
        }
        if (i == 2) {
            dropItem(new OBJ_Coin(game));
        }
    }


    // Particles
    public Color getParticleColor() {return new Color(63, 6, 5);}
    public int getParticleSize() {return 6;} // 6 pixels
    public int getParticleSpeed() {return 1;}
    public int getParticleMaxHealth() {return 20;}
}
