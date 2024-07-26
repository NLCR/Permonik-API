package cz.incad.nkp.inprove.permonikapi.specimen;

import lombok.*;
import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
@Getter
public class Specimen {

    @Field
    private String id; // UUID
    @Field
    private String metaTitleId; // UUID of metaTitle
    @Field
    private String volumeId;
    @Field
    private String barCode;
    @Field
    private Boolean numExists;
    @Field
    private Boolean numMissing;
    @Field
    private String ownerId; // UUID of owner
    @Field
    private List<String> damageTypes;
    @Field
    private List<Integer> damagedPages; // stored by real pages, so first page = 1, second page = 2 etc. Starting from 1, not 0
    @Field
    private List<Integer> missingPages; // stored by real pages, so first page = 1, second page = 2 etc. Starting from 1, not 0
    @Field
    private String note;
    @Field
    private String name;
    @Field
    private String subName;
    @Field
    private String publicationId; // UUID of publication
    @Field
    private String mutationId; // UUID of mutation
    @Field
    private String publicationMark;
    @Field
    private String publicationDate;
    @Field
    private String publicationDateString;
    @Field
    private String number; // filled if specimen is not attachment
    @Field
    private String attachmentNumber; // filled if specimen is attachment
    @Field
    private Integer pagesCount;
    @Field
    private Boolean isAttachment;

}
