package cz.incad.nkp.inprove.permonikapi.volume.dto;

import java.util.List;

public record VolumeDTO(
        String id,
        String barCode,
        String dateFrom,
        String dateTo,
        String metaTitleId,
        String mutationId,
        List<VolumePeriodicityDTO> periodicity,
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
