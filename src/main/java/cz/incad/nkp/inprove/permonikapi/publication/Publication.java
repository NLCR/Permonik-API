package cz.incad.nkp.inprove.permonikapi.publication;


import lombok.*;
import org.apache.solr.client.solrj.beans.Field;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
@Getter
public class Publication {

    @Field
    private String id; // UUID

    // as string
    //    {
//        cs: "Ranní",
//        sk: "Ranné",
//        en: "Morning"
//    }
    @Field
    private String name;
}
