package net.dinglezz.torgrays_trials.environment;

import net.dinglezz.torgrays_trials.main.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Lighting {
    Game game;
    BufferedImage darknessFilter;

    public Lighting(Game game) {
        this.game = game;
        setLightSource();
    }

    public void setLightSource() {
        // Make buffered image for darkness filter
        darknessFilter = new BufferedImage(game.screenWidth, game.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)darknessFilter.getGraphics();

        // Calculate the centre of the player
        int centreX = game.player.screenX + (game.tileSize) / 2;
        int centreY = game.player.screenY + (game.tileSize) / 2;

        // Create a radial gradient paint
        Color[] color = new Color[12];
        color[0] = new Color(0, 0, 0, 0.1f);
        color[1] = new Color(0, 0, 0, 0.42f);
        color[2] = new Color(0, 0, 0, 0.52f);
        color[3] = new Color(0, 0, 0, 0.61f);
        color[4] = new Color(0, 0, 0, 0.69f);
        color[5] = new Color(0, 0, 0, 0.76f);
        color[6] = new Color(0, 0, 0, 0.82f);
        color[7] = new Color(0, 0, 0, 0.87f);
        color[8] = new Color(0, 0, 0, 0.91f);
        color[9] = new Color(0, 0, 0, 0.94f);
        color[10] = new Color(0, 0, 0, 0.96f);
        color[11] = new Color(0, 0, 0, 0.98f);

        float[] fraction = new float[12];
        fraction[0] = 0f;
        fraction[1] = 0.4f;
        fraction[2] = 0.5f;
        fraction[3] = 0.6f;
        fraction[4] = 0.65f;
        fraction[5] = 0.7f;
        fraction[6] = 0.75f;
        fraction[7] = 0.8f;
        fraction[8] = 0.85f;
        fraction[9] = 0.9f;
        fraction[10] = 0.95f;
        fraction[11] = 1f;

        if (game.currentMap.equals("Coiner's Shop")) {
            RadialGradientPaint gPaint = new RadialGradientPaint(centreX, centreY, 500, fraction, color);
            g2.setPaint(gPaint);
        } else if (game.player.currentLight == null) {
            RadialGradientPaint gPaint = new RadialGradientPaint(centreX, centreY, 70, fraction, color);
            g2.setPaint(gPaint);
        } else {
            RadialGradientPaint gPaint = new RadialGradientPaint(centreX, centreY, game.player.currentLight.lightRadius, fraction, color);
            g2.setPaint(gPaint);
        }

        // Fill the buffered image with the radial gradient paint
        g2.fillRect(0, 0, game.screenWidth, game.screenHeight);
        g2.dispose();
    }
    public void update() {
        if (game.player.lightUpdated) {
            setLightSource();
            game.player.lightUpdated = false;
        }
    }
    public void draw(Graphics2D g2) {
        g2.drawImage(darknessFilter, 0,0, null);
    }
}
