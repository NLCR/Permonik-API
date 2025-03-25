package cz.incad.nkp.inprove.permonikapi.publication;


import cz.incad.nkp.inprove.permonikapi.audit.Auditable;
import lombok.*;
import org.apache.solr.client.solrj.beans.Field;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Setter
@Getter
public class Publication extends Auditable {

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

    @Field
    private Boolean isDefault;

    @Field
    private Boolean isAttachment;

    @Field
    private Boolean isPeriodicAttachment;
}
