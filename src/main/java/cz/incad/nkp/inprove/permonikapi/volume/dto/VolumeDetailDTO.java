package cz.incad.nkp.inprove.permonikapi.volume.dto;

import cz.incad.nkp.inprove.permonikapi.metaTitle.MetaTitle;
import cz.incad.nkp.inprove.permonikapi.specimen.dto.SpecimensForVolumeDetailDTO;
import cz.incad.nkp.inprove.permonikapi.volume.Volume;


public record VolumeDetailDTO(
        Volume volume,
        MetaTitle metaTitle,
        SpecimensForVolumeDetailDTO specimens
) {
}

