package org.ganesh.urlshortener.util;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GeoResolver {
    public static String resolve(String ip) {
        try {
            URL url = new URL("https://ipinfo.io/" + ip + "/country?token=YOUR_TOKEN");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            Scanner scanner = new Scanner(new InputStreamReader(conn.getInputStream()));
            String country = scanner.nextLine();
            scanner.close();
            return country;
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
