package cz.incad.nkp.inprove.permonikapi.volume;

import lombok.*;
import org.apache.solr.client.solrj.beans.Field;


@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
@Getter
public class Volume {

    @Field
    private String id; // UUID
    @Field
    private String barCode;
    @Field
    private String dateFrom;
    @Field
    private String dateTo;
    @Field
    private String metaTitleId; // UUID of metaTitle
    @Field
    private String mutationId; // UUID of mutation
    /*
    periodicity as string
    {
      "day": "Monday",
      "numExists": true,
      "publicationId": "fd041788-b3c3-4fe9-b824-899aaad62ca3",
      "pagesCount": 0,
      "name": "Mladá fronta (TESTOVACÍ DATA)",
      "subName": "",
      "isAttachment": false
    },
    */
    @Field
    private String periodicity;
    @Field
    private Integer firstNumber;
    @Field
    private Integer lastNumber;
    @Field
    private String note;
    @Field
    private Boolean showAttachmentsAtTheEnd;
    @Field
    private String signature;
    @Field
    private String ownerId; // UUID of owner
    @Field
    private Integer year;
    @Field
    private String publicationMark;

}
