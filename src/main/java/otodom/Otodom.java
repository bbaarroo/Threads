package otodom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Otodom {

    public static void main(String[] args) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        long start = System.currentTimeMillis();


        final String OTODOM_URL = "https://www.otodom.pl/sprzedaz/mieszkanie/poznan/piatkowo/";

        Document doc = Jsoup.connect(OTODOM_URL).get();
        Elements allLinks = doc.select("a[href]");

        Set<String> links = new HashSet<>();
        for (Element element : allLinks) {
            String href = element.attr("href");
            if (href.contains("https://www.otodom.pl/oferta/"))
                links.add(href);
        }


        for (int i = 0; i < links.size(); i++) {
            int finalI = i;
            executorService.submit(()-> {
                try {
                    savePages(links.toArray()[finalI].toString(), finalI + ".html");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();

        links.forEach(System.out::println);
        long end = System.currentTimeMillis();
        System.out.println("ilość pobranych linkow" + links.size());
        System.out.println("Czas wykonania: " + (end - start));

    }

    public static void savePages(String link, String filename) throws IOException {
        Document doc = Jsoup.connect(link).get();
        FileWriter fileWriter = new FileWriter(filename, false);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(doc.toString());
        bufferedWriter.close();
    }

}
