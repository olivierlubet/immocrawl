package net.lubet.immocrawl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONObject;

public class Annonce {
    Date initDate = new Date();

    public enum Attribute {
        DATE,
        ID,
        PROVIDER,
        TYPE_BIEN,
        TYPE_ANNONCE,
        PRIX,
        SURFACE,
        NB_PIECES,
        URL,
        REGION,
        DEPARTEMENT,
        VILLE,
        QUARTIER,
        CODE_POSTAL,
        DESCRIPTION,
        HONORAIRES_INCLUS,
        /*
        NB_CHAMBRES,
        NB_BALCONS,
        NB_ASCENCEURS,
        NB_PARKINGS,
        NB_ETAGES,
        NB_CAVES,
        NB_BOX,
*/
    }


    HashMap<Attribute, String> attributes = new HashMap<Attribute, String>();

    public HashMap<Attribute, String> getAttributes() {
        return attributes;
    }

    public Annonce() {
        this.setAttribute(Attribute.DATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    public Annonce setAttribute(Annonce.Attribute a, String s) {
        this.attributes.put(a, s);
        return this;
    }
    
    public String getAttribute(Annonce.Attribute a) {
        return this.attributes.get(a);
    }

    public String toString() {
        String ret = "";
        for (Attribute a : Attribute.values()) {
            if (this.attributes.containsKey(a)) {
                ret += this.attributes.get(a).replace(";", " ").replace("\n", " ");
            }
            ret += ";";
        }
        return ret;

        //return String.join(";", this.attributes.values()); //PB : peut varier dans l'ordre sur un même exemple
    }

    public JSONObject toJSON() {
        JSONObject ret = new JSONObject();
        for (Attribute a : Attribute.values()) {
            ret.put(a.toString().toLowerCase(), this.attributes.get(a));
        }
        return ret;
    }

}
