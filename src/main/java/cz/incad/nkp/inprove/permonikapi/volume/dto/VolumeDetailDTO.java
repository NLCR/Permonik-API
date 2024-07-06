package cz.incad.nkp.inprove.permonikapi.volume.dto;

import cz.incad.nkp.inprove.permonikapi.metaTitle.MetaTitle;
import cz.incad.nkp.inprove.permonikapi.specimen.dto.SpecimensForVolumeDetailDTO;


public record VolumeDetailDTO(
        VolumeDTO volume,
        MetaTitle metaTitle,
        SpecimensForVolumeDetailDTO specimens
) {
}

