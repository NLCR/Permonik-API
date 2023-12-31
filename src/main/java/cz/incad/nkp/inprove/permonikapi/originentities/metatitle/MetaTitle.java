package cz.incad.nkp.inprove.permonikapi.originentities.metatitle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.Objects;

import static cz.incad.nkp.inprove.permonikapi.originentities.metatitle.MetaTitleDefinition.META_TITLE_CORE_NAME;

@Getter
@Setter
@NoArgsConstructor
@SolrDocument(collection = META_TITLE_CORE_NAME)
public class MetaTitle implements MetaTitleDefinition {

    @Id @Indexed(value = ID_FIELD, required = true)
    private String id;

    @Field(NAME_FIELD)
    private String name;

    @Indexed(PERIODICITY_FIELD)
    private String periodicity;

    @Field(NOTE_FIELD)
    private String note;

    @Indexed(SHOW_TO_NOT_LOGGED_USERS_FIELD)
    private Boolean showToNotLoggedUsers;


    public MetaTitle(String id, String name, String periodicity, String note, Boolean showToNotLoggedUsers) {
        this.id = id;
        this.name = name;
        this.periodicity = periodicity;
        this.note = note;
        this.showToNotLoggedUsers = showToNotLoggedUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaTitle metaTitle = (MetaTitle) o;
        return Objects.equals(id, metaTitle.id) && Objects.equals(name, metaTitle.name) && Objects.equals(periodicity, metaTitle.periodicity) && Objects.equals(note, metaTitle.note) && Objects.equals(showToNotLoggedUsers, metaTitle.showToNotLoggedUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, periodicity, note, showToNotLoggedUsers);
    }

    @Override
    public String toString() {
        return "MetaTitle{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", periodicity='" + periodicity + '\'' +
                ", note='" + note + '\'' +
                ", showToNotLoggedUsers=" + showToNotLoggedUsers +
                '}';
    }
}
