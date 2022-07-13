package miner82.bananosuite.io;

import java.io.*;
import java.net.URL;
import java.util.UUID;

public class FileManager {

    public static void downloadFile(String urlPath, String destination) throws IOException {

        URL url = new URL(urlPath);

        try (InputStream in = url.openStream();
             BufferedInputStream bis = new BufferedInputStream(in);
             FileOutputStream fos = new FileOutputStream(destination)) {

            byte[] data = new byte[1024];
            int count;
            while ((count = bis.read(data, 0, 1024)) != -1) {
                fos.write(data, 0, count);
            }
        }

    }

    public static void MakeDirectories(String path) {
        try {
            File file = new File(path);

            file.mkdirs();
        }
        catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    public static String getUniqueFilename(String directoryPath, String ext) {
        String fileName;
        File file;

        if(!directoryPath.endsWith(File.separator)) {
            directoryPath += File.separator;
        }

        do {
            fileName = UUID.randomUUID().toString().replace("-", "");
            file = new File(directoryPath + fileName + "." + ext);

        } while(file.exists());

        return fileName + "." + ext;
    }
}
