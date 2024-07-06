package cz.incad.nkp.inprove.permonikapi.specimen.dto;

import cz.incad.nkp.inprove.permonikapi.specimen.Specimen;

import java.util.List;

public record SpecimensForVolumeDetailDTO(
        List<Specimen> specimenList,
        SpecimensPublicationRangeDTO specimensDateRange
) {
}
