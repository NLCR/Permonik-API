package cz.incad.nkp.inprove.permonikapi.volume.dto;

import cz.incad.nkp.inprove.permonikapi.specimen.Specimen;

import java.util.List;

public record CreatableVolumeWithSpecimensDTO(CreatableVolumeDTO creatableVolumeDTO,
                                              List<Specimen> specimenList) {
}
