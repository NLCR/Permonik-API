package cz.incad.nkp.inprove.permonikapi.publication.dto;

public record PublicationDTO(String id, PublicationNameDTO name, Boolean isDefault, Boolean isAttachment,
                             Boolean isPeriodicAttachment) {
}
