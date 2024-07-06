package cz.incad.nkp.inprove.permonikapi.metaTitle.dto;

import cz.incad.nkp.inprove.permonikapi.specimen.dto.StatsForMetatTitleOverviewDTO;

public record MetaTitleOverViewDTO(
        String id,
        String name,
        StatsForMetatTitleOverviewDTO specimens
) {
}
