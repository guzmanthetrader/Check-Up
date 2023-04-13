import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSiteScanner {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Taranacak URL: ");
        String url = scanner.nextLine();

        System.out.println("Tarama başlıyor...");

        // XSS saldırısı için tarama
        xssTarama(url);

        // SQL Injection saldırısı için tarama
        sqlInjectionTarama(url);

        // HTML Injection saldırısı için tarama
        htmlInjectionTarama(url);
    }

    // XSS saldırısı için tarama
    private static void xssTarama(String urlStr) throws IOException {

        System.out.println("XSS taraması yapılıyor...");

        List<String> taramaListesi = new ArrayList<>();
        taramaListesi.add("<script>alert('XSS saldırısı')</script>");
        taramaListesi.add("<script>alert('XSS saldırısı');</script>");
        taramaListesi.add("<script>alert('XSS saldırısı')//</script>");
        taramaListesi.add("<script>alert('XSS saldırısı')//--></script>");
        taramaListesi.add("'';!--\"<XSS>=&{()}");

        for (String tarama : taramaListesi) {
            String yeniUrlStr = urlStr.replaceAll("(?i)<script.*?>.*?</script.*?>", "").concat(tarama);
            HttpURLConnection conn = getHttpURLConnection(yeniUrlStr);
            if (conn.getResponseCode() == 200) {
                System.out.println("XSS saldırısı yapılabiliyor: " + yeniUrlStr);
            }
            conn.disconnect();
        }
    }

    // SQL Injection saldırısı için tarama
    private static void sqlInjectionTarama(String urlStr) throws IOException {

        System.out.println("SQL Injection taraması yapılıyor...");

        List<String> taramaListesi = new ArrayList<>();
        taramaListesi.add("'");
        taramaListesi.add("'");
        taramaListesi.add("\"");
        taramaListesi.add(";");
        taramaListesi.add("/*");
        taramaListesi.add("*/");
        taramaListesi.add("--");
        taramaListesi.add("'");
        taramaListesi.add("\"");

        for (String tarama : taramaListesi) {
            String yeniUrlStr = urlStr + tarama;
            HttpURLConnection conn = getHttpURLConnection(yeniUrlStr);
            if (conn.getResponseCode() == 500) {
                System.out.println("SQL Injection saldırısı yapılabiliyor: " + yeniUrlStr);
            }
            conn.disconnect();
        }
    }

    // HTML
    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.util.Scanner;
    
    public class WebSiteScanner {
        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Tarayacağınız web sitesi adresini girin: ");
            String urlStr = scanner.nextLine();
            System.out.println("Tarayıcı çalıştırılıyor...");
            scanWebsite(urlStr);
        }
    
        public static void scanWebsite(String urlStr) {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    
                // Robots.txt dosyası var mı kontrol ediliyor.
                checkRobotsTxt(url);
    
                // Açık portlar taraması yapılıyor.
                checkOpenPorts(url);
    
                // XSS açığı var mı kontrol ediliyor.
                checkXSS(url);
    
                // SQL injection açığı var mı kontrol ediliyor.
                checkSQLInjection(url);
    
                // Tarayıcı kapatılıyor.
                conn.disconnect();
            } catch (IOException ex) {
                System.out.println("Web sitesine bağlanırken hata oluştu: " + ex.getMessage());
            }
        }
    
        public static void checkRobotsTxt(URL url) {
            String robotsUrlStr = url.getProtocol() + "://" + url.getHost() + "/robots.txt";
            try {
                URL robotsUrl = new URL(robotsUrlStr);
                HttpURLConnection conn = (HttpURLConnection) robotsUrl.openConnection();
                conn.setRequestMethod("GET");
    
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Robots.txt dosyası mevcut.");
                } else {
                    System.out.println("Robots.txt dosyası mevcut değil.");
                }
                conn.disconnect();
            } catch (IOException ex) {
                System.out.println("Robots.txt dosyası kontrol edilirken hata oluştu: " + ex.getMessage());
            }
        }
    
        public static void checkOpenPorts(URL url) {
            String host = url.getHost();
            int[] ports = {21, 22, 23, 25, 53, 80, 110, 143, 443, 465, 993, 995};
            System.out.println("Açık portlar taranıyor...");
            for (int port : ports) {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(host, port), 2000);
                    System.out.println("Port " + port + " açık.");
                    socket.close();
                } catch (IOException ex) {
                    // Port kapalı
                }
            }
        }
    
        public static void checkXSS(URL url) {
            String urlStr = url.toString();
            String testScript = "<script>alert('XSS Test');</script>";
            try {
                String modifiedUrlStr = urlStr.replaceFirst("\\?", "?" + testScript + "&");
                URL modifiedUrl = new URL(modifiedUrlStr);
                HttpURLConnection conn = (HttpURLConnection) modifiedUrl.openConnection();
                conn.setRequestMethod("GET");
        
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine); // response nesnesine inputLine eklendi.
                    }
                    in.close(); // BufferedReader kapatıldı.
                    System.out.println("XSS açığı yok.");
                } else {
                    System.out.println("XSS açığı var.");
                }
                conn.disconnect(); // HttpURLConnection kapatıldı.
            } catch (IOException ex) {
                System.out.println("XSS açığı kontrol edilirken hata oluştu: " + ex.getMessage());
            }
        }
        
    