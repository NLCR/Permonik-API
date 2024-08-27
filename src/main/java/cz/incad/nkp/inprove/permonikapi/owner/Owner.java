package cz.incad.nkp.inprove.permonikapi.owner;


import lombok.*;
import org.apache.solr.client.solrj.beans.Field;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
@Getter
public class Owner {

    @Field
    private String id; // UUID

    @Field
    private String name;

    @Field
    private String sigla;
}
