package miner82.bananosuite.renderers;

import miner82.bananosuite.configuration.ConfigEngine;
import miner82.bananosuite.classes.MonkeyMap;
import miner82.bananosuite.interfaces.IDBConnection;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class monKeyRenderer extends MapRenderer {

    private final IDBConnection db;
    private final ConfigEngine configEngine;

    private final String missingFileImage = "missing.png";

    public monKeyRenderer(IDBConnection db, ConfigEngine configEngine) {
        this.db = db;
        this.configEngine = configEngine;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {

        MonkeyMap map = db.getMapRecord(this.configEngine, mapView);

        if(map != null) {

            try {

                File imageFile = new File(map.getFullFilePath());

                if (!imageFile.exists()) {

                    imageFile = new File(map.getFullFilePath());

                }

                BufferedImage image = ImageIO.read(imageFile);
                mapCanvas.drawImage(0, 0, image);

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }

}

