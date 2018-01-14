package net.lubet.immocrawl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LeboncoinList {

    private ArrayList<URL> annoncesUrls = new ArrayList<URL>();
    private URL            nextUrl      = null;

    public LeboncoinList parse(File file) throws Exception {
        return parse(Jsoup.parse(file, "UTF-8", "http://www.seloger.com/"));
    }

    public LeboncoinList parse(Document doc) throws Exception {
        //list_item clearfix trackable
        //Elements links = doc.select("section.list.mainList.tabs a.list_item");
        Elements links = doc.select("a.list_item");
        for (Element e : links) {
            //System.out.println(e.attr("title"));
            //forme : //www.leboncoin.fr/ventes_immobilieres/1330902899.htm?ca=12_s
            this.annoncesUrls.add(new URL("https:" + e.attr("href")));
        }

        if (!doc.select("a#next").isEmpty()) {
            this.nextUrl = new URL("https:" + doc.select("#next").attr("href"));
        }
        return this;
    }


    public ArrayList<URL> getAnnoncesUrls() {
        return this.annoncesUrls;
    }

    public URL getNextURL() {
        return this.nextUrl;
    }

    public boolean hasNextUrl() {
        return nextUrl != null;
    }
}
