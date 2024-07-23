package cz.incad.nkp.inprove.permonikapi.volume.dto;

import cz.incad.nkp.inprove.permonikapi.specimen.Specimen;
import cz.incad.nkp.inprove.permonikapi.volume.Volume;

import java.util.List;

public record UpdatableVolumeWithSpecimensDTO(Volume volume, List<Specimen> specimens) {
}
