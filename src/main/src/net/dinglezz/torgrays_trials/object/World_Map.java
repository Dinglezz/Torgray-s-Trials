package net.dinglezz.torgrays_trials.object;

import net.dinglezz.torgrays_trials.entity.Entity;
import net.dinglezz.torgrays_trials.entity.EntityTags;
import net.dinglezz.torgrays_trials.entity.EntityTypes;
import net.dinglezz.torgrays_trials.main.Game;
import net.dinglezz.torgrays_trials.main.States;

public class World_Map extends Entity {
    Game game;

    public World_Map(Game game) {
        super(game);
        this.game = game;

        name = "World Map";
        down1 = registerEntitySprite("/object/map");
        type = EntityTypes.TYPE_OBJECT;
        tags.add(EntityTags.TAG_CONSUMABLE);
        description = "A handy map of the world, \nwithout that annoying \ndarkness.";
        price = 8;
    }
    @Override
    public boolean use(Entity entity) {
        game.ui.uiState = States.UIStates.MAP;
        game.inputHandler.upPressed = false;
        game.inputHandler.downPressed = false;
        game.inputHandler.leftPressed = false;
        game.inputHandler.rightPressed = false;
        return false;
    }
}
