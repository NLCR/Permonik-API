package cz.incad.nkp.inprove.permonikapi.volume.dto;

public record CreatableVolumeDTO(
        String barCode,
        String dateFrom,
        String dateTo,
        String metaTitleId,
        String mutationId,
        String periodicity,
        Integer firstNumber,
        Integer lastNumber,
        String note,
        Boolean showAttachmentsAtTheEnd,
        String signature,
        String ownerId,
        Integer year,
        String publicationMark
) {
}
