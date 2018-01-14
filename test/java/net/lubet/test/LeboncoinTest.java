package net.lubet.test;

import static org.junit.Assert.*;
import net.lubet.immocrawl.Annonce;
import net.lubet.immocrawl.LeboncoinAnnonce;
import net.lubet.immocrawl.LeboncoinCrawler;
import net.lubet.immocrawl.LeboncoinList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

/**
 * Unit test for simple App.
 */
@RunWith(JUnit4.class)
public class LeboncoinTest {


    @Test
    public void testList() {
        try {
            LeboncoinList list = new LeboncoinList().parse(new File("src/test/resources/leboncoin/list.html"));

            assertEquals(38, list.getAnnoncesUrls().size());
            assertEquals("https://www.leboncoin.fr/ventes_immobilieres/offres/ile_de_france/?o=2", list.getNextURL().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testAnnonce() {
        try {
            LeboncoinAnnonce annonce = new LeboncoinAnnonce().parse(new File("src/test/resources/leboncoin/annonce.html"));
            //System.out.println(annonce.getAnnonce().toJSON());
            assertTrue(true);
            assertEquals("2017-11-03", annonce.getAnnonce().getAttribute(Annonce.Attribute.DATE));
            assertEquals("blabla blabla", LeboncoinAnnonce.formatVille("blabla blabla 98000"));
            assertEquals("ile de france",
                         LeboncoinAnnonce.formatRegion("//www.leboncoin.fr/annonces/offres/ile_de_france/seine_saint_denis/"));
            assertEquals("1158593945", LeboncoinAnnonce.formatId("//www.leboncoin.fr/ventes_immobilieres/1158593945.htm?ca=12_s"));
            assertEquals("1158593945", LeboncoinAnnonce.formatId("http://www.leboncoin.fr/ventes_immobilieres/1158593945.htm?ca=12_s"));
            assertEquals("1307897011", LeboncoinAnnonce.formatId("https://www.leboncoin.fr/rd?id=1307897011&event=gallery&ca=2_s&beta=1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     @Test
     public void testCrawler() {
     try {
     LeboncoinCrawler c = (LeboncoinCrawler) new LeboncoinCrawler()
     .setMaxIteration(1)
     .setDataFile(new File("src/test/resources/data.lbc.csv"))
     .setUrlInputFile(new File("src/test/resources/urls.lbc.in"))
     .setUrlOutputFile(new File("src/test/resources/urls.lbc.out"))
     .loadUrls();
     assertEquals(2,c.getNextUrls().size());
    
     c.run();

     } catch (Exception e) {
     e.printStackTrace();
     }
     }*/
}
