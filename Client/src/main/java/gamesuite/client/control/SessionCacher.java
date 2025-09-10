package gamesuite.client.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;


public class SessionCacher {

    public static void saveSession(String sessionId, String gameId) {
        try {
            File jarDir = new File(SessionCacher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            File file = new File(jarDir, "session.json");

            JSONObject obj = new JSONObject();
            obj.put("sessionId", sessionId);
            obj.put("gameId", gameId);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(obj.toString(2)); 
            }

            System.out.println("Session saved to: " + file.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Failed to save session: " + e.getMessage());
        }
    }

    public static JSONObject loadSession() {
        try {
            File jarDir = new File(SessionCacher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            File file = new File(jarDir, "session.json");

            if (!file.exists()) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new FileReader("session.json"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            //String content = sb.toString();
            //String content = new String(Files.readAllBytes(file.toPath()));
            return new JSONObject(line);

        } catch (Exception e) {
            System.err.println("Failed to load session: " + e.getMessage());
            return null;
        }
    }
}

