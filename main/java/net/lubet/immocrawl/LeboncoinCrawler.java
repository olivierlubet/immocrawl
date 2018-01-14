package net.lubet.immocrawl;

import java.io.File;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jsoup.Jsoup;


public class LeboncoinCrawler extends Crawler {

    private int maxIteration = Integer.MAX_VALUE;

    public LeboncoinCrawler setMaxIteration(int max) {
        this.maxIteration = max;
        return this;
    }

    public LeboncoinCrawler() {
    }

    public static void main(String... argvs) {

        LeboncoinCrawler c = (LeboncoinCrawler)new LeboncoinCrawler();

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("i", "url-in", true, "input urls");
        options.addOption("o", "url-out", true, "output urls");
        options.addOption("d", "data", true, "output data file");

        try {
            CommandLine cmd = parser.parse(options, argvs);

            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("LeboncoinCrawler", options);
                return;
            }

            if (cmd.hasOption("url-in")) {
                c.setUrlInputFile(new File(cmd.getOptionValue("url-in")));
            }
            if (cmd.hasOption("url-out")) {
                c.setUrlOutputFile(new File(cmd.getOptionValue("url-out")));
            }
            if (cmd.hasOption("data")) {
                c.setDataFile(new File(cmd.getOptionValue("data")));
            }

            c.loadUrls().loadAnnonces().run();
        } catch (ParseException e) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void run() throws Exception {
        System.out.println("Running...");
        int nbIteration = 0;

        while (this.hasNextUrl() && nbIteration < this.maxIteration) {
            nbIteration++;
            LeboncoinList liste = new LeboncoinList();

            String nextUrl = this.nextUrl().toString();
            System.out.print(nextUrl + "\t");
            liste.parse(Jsoup.connect(nextUrl).get());
            System.out.print(liste.getAnnoncesUrls().size()+" annonces : [");

            if (liste.hasNextUrl()) {
                this.addUrl(liste.getNextURL());
                //this.recordUrls();// Optimisation : on arrête d'enregistrer l'URL en cours
            }

            for (URL url : liste.getAnnoncesUrls()) {
                if (this.hasAnnonce(LeboncoinAnnonce.formatId(url.toString()))) { // Ecarter les annonces déjà vues
                    System.out.print("X");
                } else {
                    try {
                        LeboncoinAnnonce annonce = new LeboncoinAnnonce();
                        System.out.print(".");
                        this.recordAnnonce(annonce.parse(Jsoup.connect(url.toString()).get()).getAnnonce());
                    } catch (Exception e) {
                        System.out.println("URL:" + url);
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("] OK");
        }
    }
}
