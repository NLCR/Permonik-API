package cz.incad.nkp.inprove.permonikapi.publication.mapper;


import cz.incad.nkp.inprove.permonikapi.publication.Publication;
import cz.incad.nkp.inprove.permonikapi.publication.dto.CreatablePublicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CreatablePublicationMapper {

    default String generateUUID() {
        return UUID.randomUUID().toString();
    }

    @Mapping(target = "id", expression = "java(generateUUID())")
    @Mapping(target = "isDefault", defaultValue = "false")
    void createPublication(CreatablePublicationDTO creatablePublicationDTO, @MappingTarget Publication target);
}
