package net.lubet.immocrawl;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LeboncoinAnnonce {
    private Annonce annonce = new Annonce();

    public Annonce getAnnonce() {
        return this.annonce;
    }

    public LeboncoinAnnonce parse(File file) throws Exception {
        return parse(Jsoup.parse(file, "UTF-8", "http://www.seloger.com/"));
    }

    public LeboncoinAnnonce parse(Document doc) throws Exception {

        this.annonce.setAttribute(Annonce.Attribute.PROVIDER, "LEBONCOIN");
        this.annonce.setAttribute(Annonce.Attribute.PRIX, doc.select("h2.item_price").attr("content"));
        this.annonce.setAttribute(Annonce.Attribute.URL, "https:" + doc.select("meta[property=og:url]").attr("content"));
        this.annonce.setAttribute(Annonce.Attribute.ID, formatId(doc.select("meta[property=og:url]").attr("content")));
        this.annonce.setAttribute(Annonce.Attribute.DATE, doc.select("p[itemprop=availabilityStarts]").attr("content"));

        this.annonce.setAttribute(Annonce.Attribute.DESCRIPTION, doc.select("div.properties_description>p.value").text());

        for (Element e : doc.select("div.line>h2")) {
            switch (e.select("span.property").text()) {
                case "Ville":
                    this.annonce.setAttribute(Annonce.Attribute.VILLE, formatVille(e.select("span.value").text()));
                    this.annonce.setAttribute(Annonce.Attribute.CODE_POSTAL, formatCodePostal(e.select("span.value").text()));
                    break;
                case "Type de bien":
                    this.annonce.setAttribute(Annonce.Attribute.TYPE_BIEN, e.select("span.value").text());
                    break;
                case "Pièces":
                    this.annonce.setAttribute(Annonce.Attribute.NB_PIECES, e.select("span.value").text());
                    break;
                case "Surface":
                    this.annonce.setAttribute(Annonce.Attribute.SURFACE, e.select("span.value").text().replace(" m2", ""));
                    break;
                case "Honoraires":
                    this.annonce.setAttribute(Annonce.Attribute.HONORAIRES_INCLUS, e.select("span.value").text());
                    break;
                case "Prix":// cf plus haut
                case "Loyer mensuel":
                case "Classe énergie":
                case "GES":
                case "Meublé / Non meublé":
                case "Frais d'agence inclus":
                case "Référence":
                    break;
                default:
                    System.out.println("Propriété non gérée : [" + e.select("span.property").text() + "][" + e.select("span.value").text()
                                       + "]");
                    break;
            }
        }

        //breadcrumbsNav 
        //nav.breadcrumbsNav>ul>li
        Elements nav = doc.select("nav.breadcrumbsNav>ul>li");
        this.annonce.setAttribute(Annonce.Attribute.DEPARTEMENT, nav.eq(1).text());
        this.annonce.setAttribute(Annonce.Attribute.REGION, formatRegion(nav.eq(1).select("a").attr("href")));
        switch (nav.eq(2).text()) {
            case "Ventes immobilières":
                this.annonce.setAttribute(Annonce.Attribute.TYPE_ANNONCE, "VENTE");
                break;
            case "Locations":
                this.annonce.setAttribute(Annonce.Attribute.TYPE_ANNONCE, "LOCATION");
                break;
            default:
                throw new Exception("Type d'annonce non géré : " + nav.eq(2).text());
        }
        return this;
    }

    /**
     * @param input "Sevran 93270"
     * @return "Sevran"
     */
    public static String formatVille(String input) {
        if (input.contains(" ")) {
            return input.trim().substring(0, input.lastIndexOf(' '));
        }
        return input.trim();
    }

    /**
     * @param input "Sevran 93270"
     * @return "93270"
     */
    public static String formatCodePostal(String input) {
        if (input.contains(" ")) {
            return input.trim().substring(input.lastIndexOf(' ') + 1);
        }
        return input.trim();
    }


    /**
     * @param input "//www.leboncoin.fr/annonces/offres/ile_de_france/seine_saint_denis/"
     * @return "ile de france"
     */
    public static String formatRegion(String input) throws Exception {
        try {
            return input.split("/")[5].replace("_", " ");
        } catch (Exception e) {
            throw new Exception("Pas de région dans : " + input);
        }
    }

    /**
     * @param input "//www.leboncoin.fr/ventes_immobilieres/1158593945.htm?ca=12_s" ou
     * https://www.leboncoin.fr/rd?id=1307897011&event=gallery&ca=2_s&beta=1
     * @return "1158593945"
     */
    public static String formatId(String input) throws Exception {
        if (input.lastIndexOf('/') < input.lastIndexOf('.')) {
            return input = input.substring(input.lastIndexOf('/') + 1, input.lastIndexOf('.'));
        }
        if (input.lastIndexOf("id=") < input.indexOf('&')) {
            return input = input.substring(input.lastIndexOf("id=") + 3, input.indexOf('&'));
        }
        throw new Exception("Pas d'identifiant dans : " + input);
    }

}
