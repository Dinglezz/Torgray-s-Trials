package net.dinglezz.torgrays_trials.object.weapon;

import net.dinglezz.torgrays_trials.entity.Entity;
import net.dinglezz.torgrays_trials.entity.EntityTags;
import net.dinglezz.torgrays_trials.entity.EntityTypes;
import net.dinglezz.torgrays_trials.main.Game;

public class Sword_Iron extends Entity {
    public Sword_Iron(Game game) {
        super(game);

        name = "Iron Sword";
        type = EntityTypes.TYPE_OBJECT;
        tags.add(EntityTags.TAG_WEAPON);
        down1 = registerEntitySprite("/object/iron_sword");
        attackValue = 1;
        knockBackPower = 1;
        attackArea.width = 36;
        attackArea.height = 36;
        description = "A shiny iron sword \nAttack: +" + attackValue + "\nKnockback: +" + knockBackPower;
        price = 4;
    }
}
