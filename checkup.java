import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

public class CheckUpApp {public static void main(String[] args) {

    String ipAdresi = "192.168.1.";
    String hedefURL = "https://www.hedef.com";
    String[] testler = {"/admin", "/test", "/cgi-bin"};
    String[] istekYontemleri = {"GET", "POST", "PUT", "DELETE"};

    for (int i = 1; i <= 255; i++) {
        String hedefIP = ipAdresi + i;
        for (String istek : istekYontemleri) {
            tarayici(hedefIP, istek);
        }
    }

    for (String test : testler) {
        for (String istek : istekYontemleri) {
            String url = hedefURL + test;
            String yanit = httpIstek(url, istek);
            if (yanit.contains("404")) {
                System.out.println(url + " " + istek + " 404 Not Found");
            } else if (yanit.contains("401")) {
                System.out.println(url + " " + istek + " 401 Unauthorized");
            } else {
                System.out.println(url + " " + istek + " Açık");
            }
        }
    }
}

public static void tarayici(String hedefIP, String istekYontemi) {

    int timeout = 200;
    int port = 80;

    try {
        URL url = new URL("http://" + hedefIP);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(istekYontemi);
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println(hedefIP + " " + istekYontemi + " Açık");
        }
        conn.disconnect();
    } catch (IOException ex) {
        // port kapalı
    }
}

public static String httpIstek(String urlStr, String istekYontemi) {

    String yanit = "";

    try {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(istekYontemi);
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String satir;
        while ((satir = rd.readLine()) != null) {
            yanit += satir;
        }
        rd.close();
    } catch (IOException ex) {
        ex.printStackTrace();
    }

    return yanit;
}
}