package cz.incad.nkp.inprove.permonikapi.old;

import com.alibaba.fastjson.JSON;
import cz.incad.nkp.inprove.permonikapi.old.utils.MD5;
import org.apache.solr.client.solrj.beans.Field;
import org.json.JSONObject;

import java.util.logging.Logger;

/**
 *
 * @author alberto
 */
public class Titul {


    final static Logger LOGGER = Logger.getLogger(Titul.class.getName());
    @Field
    public String id;
    @Field
    public String meta_nazev;
    @Field
    public String poznamka;
    @Field
    public String periodicita;
    @Field
    public Boolean show_to_not_logged_users;

    // Odstraneno podle #138
//  @Field
//  public int pocet_stran;


    public static Titul fromJSON(JSONObject json) {
        Titul obj = JSON.parseObject(json.toString(), Titul.class);
        if (obj.id == null || obj.id.trim().isEmpty()) {
            obj.id = MD5.generate(new String[]{obj.meta_nazev});
        }
        return obj;
    }

}
