package cz.incad.nkp.inprove.permonikapi.specimen.dto;

import java.util.List;

public record CreatableSpecimenDTO(
        String id,
        Boolean numExists,
        Boolean numMissing,
        String metaTitleId,
        String volumeId,
        String ownerId,
        String barCode,
        List<String> damageTypes,
        List<Integer> damagedPages,
        List<Integer> missingPages,
        String note,
        String name,
        String subName,
        String publicationId,
        String mutationId,
        String publicationMark,
        String publicationDate,
        String publicationDateString,
        String number,
        Integer pagesCount,
        Boolean isAttachment
) {
}
