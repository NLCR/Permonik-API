package cz.incad.nkp.inprove.permonikapi.specimen.dto;


import cz.incad.nkp.inprove.permonikapi.specimen.Specimen;

import java.util.List;

public record SpecimensForVolumeOverviewStatsDTO(
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
