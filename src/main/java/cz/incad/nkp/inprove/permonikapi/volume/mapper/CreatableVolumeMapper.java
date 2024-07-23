package cz.incad.nkp.inprove.permonikapi.volume.mapper;

import cz.incad.nkp.inprove.permonikapi.volume.Volume;
import cz.incad.nkp.inprove.permonikapi.volume.dto.CreatableVolumeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CreatableVolumeMapper {

    default String generateUUID() {
        return UUID.randomUUID().toString();
    }


    @Mapping(target = "id", expression = "java(generateUUID())")
    void createVolume(CreatableVolumeDTO creatableVolumeDTO, @MappingTarget Volume target);
}
