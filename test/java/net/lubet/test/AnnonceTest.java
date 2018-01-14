package net.lubet.test;

import static org.junit.Assert.*;
import net.lubet.immocrawl.Annonce;
import net.lubet.immocrawl.Annonces;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit test for simple App.
 */
@RunWith(JUnit4.class)
public class AnnonceTest {

    @Test
    public void testConstructor() {
        new Annonce();
        new Annonces();
        assertTrue(true);
    }
    
    
    @Test
    public void testAnnonce() {
        Annonce a = new Annonce()
        .setAttribute(Annonce.Attribute.VILLE, "Arcueil");
        
        assertEquals(2,a.getAttributes().size()); // DATE + VILLE
    }
}
