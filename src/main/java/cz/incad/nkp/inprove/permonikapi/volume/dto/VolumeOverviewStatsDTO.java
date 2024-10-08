package cz.incad.nkp.inprove.permonikapi.volume.dto;

import cz.incad.nkp.inprove.permonikapi.specimen.Specimen;
import cz.incad.nkp.inprove.permonikapi.specimen.dto.FacetFieldDTO;

import java.util.List;

public record VolumeOverviewStatsDTO(
        String metaTitleName,
        String ownerId,
        String signature,
        String barCode,
        Object publicationDayMin,
        Object publicationDayMax,
        Object numberMin,
        Object numberMax,
        Object pagesCount,
        List<FacetFieldDTO> mutationIds,
        List<FacetFieldDTO> publicationMark,
        List<FacetFieldDTO> publicationIds,
        List<FacetFieldDTO> damageTypes,
        List<FacetFieldDTO> publicationDayRanges,
        List<Specimen> specimens
) {
}
