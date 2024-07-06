package cz.incad.nkp.inprove.permonikapi.specimen.dto;

import java.util.List;

public record FacetsDTO(
        List<FacetFieldDTO> names,
        List<FacetFieldDTO> subNames,
        List<FacetFieldDTO> mutationIds,
        List<FacetFieldDTO> publicationIds,
        List<FacetFieldDTO> publicationMarks,
        List<FacetFieldDTO> ownerIds,
        List<FacetFieldDTO> damageTypes
) {
}

