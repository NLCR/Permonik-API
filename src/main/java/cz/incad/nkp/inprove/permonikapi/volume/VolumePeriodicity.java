package cz.incad.nkp.inprove.permonikapi.volume;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
@Getter
public class VolumePeriodicity {
    private Boolean active;
    private String publication;
    private String day;
    private Integer pagesCount;
    private String name;
    private String subName;
}
