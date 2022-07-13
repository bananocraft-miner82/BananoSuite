package miner82.bananosuite.renderers;

import miner82.bananosuite.DB;
import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.classes.MonkeyMap;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class monKeyRenderer extends MapRenderer {

    private ConfigEngine configEngine;

    private final String missingFileImage = "missing.png";

    public monKeyRenderer(ConfigEngine configEngine) {
        this.configEngine = configEngine;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {

        MonkeyMap map = DB.getMapRecord(this.configEngine, mapView);

        if(map != null) {

            try {

                File imageFile = new File(map.getImageFullFilePath());

                if (!imageFile.exists()) {

                    imageFile = new File(map.getImageFullFilePath());

                }

                BufferedImage image = ImageIO.read(imageFile);
                mapCanvas.drawImage(0, 0, image);

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }

}

