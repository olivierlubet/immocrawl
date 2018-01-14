package net.lubet.immocrawl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;

public abstract class Crawler {

    final static Charset            ENCODING      = StandardCharsets.UTF_8;

    private Mode                    mode          = Mode.INIT;
    private LinkedList<URL>         nextUrls      = new LinkedList<URL>();
    private File                    dataFile      = new File("data.csv");
    private File                    urlInputFile  = new File("urls.in");
    private File                    urlOutputFile = new File("urls.out");
    private HashMap<String, String> annonceIds    = new HashMap<String, String>();

    public enum Mode {
        INIT,
        RESUME,
    }


    public Crawler setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public Crawler setDataFile(File file) throws IOException {
        this.dataFile = file;
        return this;
    }

    public Crawler setUrlInputFile(File file) throws IOException {
        this.urlInputFile = file;
        return this;
    }

    public Crawler setUrlOutputFile(File file) throws IOException {
        this.urlOutputFile = file;
        return this;
    }


    public Crawler loadUrls() throws Exception {
        switch (this.mode) {
            case INIT:
                return loadUrls(this.urlInputFile);
            case RESUME:
                return loadUrls(this.urlOutputFile);
            default:
                throw new Exception("Mode non géré : " + this.mode);
        }
    }

    public Crawler loadUrls(File file) throws IOException {

        for (String url : Files.readAllLines(file.toPath(), ENCODING)) {
            if (url.length() > 0) {// Ecarter les lignes vides
                if (url.charAt(0) != '#') { // Ecarter les commentaires
                    this.addUrl(url);
                }
            }
        }
        return this;
    }


    public Crawler recordUrls() throws Exception {
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.urlOutputFile.getAbsolutePath()), ENCODING));
        for (URL url : nextUrls) {
            writer.write(url.toString() + "\n");
        }
        writer.close();
        return this;
    }


    public boolean hasNextUrl() {
        return !this.nextUrls.isEmpty();
    }

    public URL nextUrl() {
        return this.nextUrls.removeFirst();
    }

    public LinkedList<URL> getNextUrls() {
        return this.nextUrls;
    }


    public Crawler addUrl(URL url) {
        this.nextUrls.addLast(url);
        return this;
    }

    public Crawler addUrl(String url) {
        try {
            return this.addUrl(new URL(url));
        } catch (MalformedURLException e) {
            System.out.println("Mauvaise URL : " + url);
        }

        return this;
    }

    private Writer outputWriter = null;

    public Crawler recordAnnonce(Annonce annonce) throws Exception {
        if (outputWriter == null) {
            outputWriter =
                           new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.dataFile.getAbsolutePath(), true), ENCODING));
        }
        this.annonceIds.put(annonce.getAttribute(Annonce.Attribute.ID), "");
        outputWriter.write(annonce.toString() + "\n");
        outputWriter.flush();

        return this;
    }

    /**
     * Pour éviter de charger deux fois une annonce identique dans le fichier
     */
    public Crawler loadAnnonces() throws IOException {
        return this.loadAnnonces(this.dataFile);
    }

    public Crawler loadAnnonces(File file) throws IOException {
        System.out.println("Chargement des annonces existantes");
        int nbLines = 0;
        if (file.exists()) {
            for (String line : Files.readAllLines(file.toPath(), ENCODING)) {
            //for (String line : FileUtiles.readLines(file.toPath(), ENCODING)) {
                System.out.print(".");
                nbLines++;
                if (line.length() > 0) {// Ecarter les lignes vides
                    if (line.charAt(0) != '#') { // Ecarter les commentaires
                        String[] parts = line.split(";");
                        if (parts.length > 2) { // Ecarter les lignes douteuses
                            this.annonceIds.put(parts[1], "");
                        }
                    }
                }
            }
        }
        System.out.println("Fin du chargement des annonces existantes : " + nbLines + " lignes");
        return this;
    }

    public boolean hasAnnonce(String id) {
        return this.annonceIds.containsKey(id);
    }

    public abstract void run() throws Exception;
}
