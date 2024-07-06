package cz.incad.nkp.inprove.permonikapi.user;

import lombok.*;
import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
@Getter
public class User implements UserDefinition {

    @Field()
    private String id;

    @Field()
    private String email;

    @Field()
    private String userName;

    @Field()
    private String firstName;

    @Field()
    private String lastName;

    @Field()
    private String role;

    @Field()
    private Boolean active;

    @Field()
    private List<String> owners;

}
