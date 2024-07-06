package cz.incad.nkp.inprove.permonikapi.metaTitle;

import lombok.*;
import org.apache.solr.client.solrj.beans.Field;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
@Getter
public class MetaTitle {

    @Field
    private String id; // UUID
    @Field
    private String name;
    @Field
    private String note;
    @Field
    private Boolean isPublic;

}
