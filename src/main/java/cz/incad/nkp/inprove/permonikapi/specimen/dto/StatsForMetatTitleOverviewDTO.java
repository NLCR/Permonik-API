package cz.incad.nkp.inprove.permonikapi.specimen.dto;

public record StatsForMetatTitleOverviewDTO(
        Object publicationDayMin,
        Object publicationDayMax,
        Long mutationsCount,
        Long ownersCount,
        Integer matchedSpecimens
) {
}
