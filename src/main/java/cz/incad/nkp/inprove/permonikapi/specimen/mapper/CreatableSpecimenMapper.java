package cz.incad.nkp.inprove.permonikapi.specimen.mapper;


import cz.incad.nkp.inprove.permonikapi.specimen.Specimen;
import cz.incad.nkp.inprove.permonikapi.volume.Volume;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

import static cz.incad.nkp.inprove.permonikapi.specimen.SpecimenDefinition.*;

@Mapper(componentModel = "spring")
public interface CreatableSpecimenMapper {

    default String generateUUID() {
        return UUID.randomUUID().toString();
    }

    // copy fields from volume
    @Mapping(source = "volume." + ID_FIELD, target = "target." + VOLUME_ID_FIELD)
    @Mapping(source = "volume." + META_TITLE_ID_FIELD, target = "target." + META_TITLE_ID_FIELD)
    @Mapping(source = "volume." + OWNER_ID_FIELD, target = "target." + OWNER_ID_FIELD)
    @Mapping(source = "volume." + BAR_CODE_FIELD, target = "target." + BAR_CODE_FIELD)
    // copy fields from specimenDTO
    @Mapping(source = "specimen." + NOTE_FIELD, target = "target." + NOTE_FIELD)
    @Mapping(source = "specimen." + PUBLICATION_MARK_FIELD, target = "target." + PUBLICATION_MARK_FIELD)
    @Mapping(source = "specimen." + MUTATION_ID_FIELD, target = "target." + MUTATION_ID_FIELD)
    @Mapping(target = ID_FIELD, expression = "java(generateUUID())")
    void createSpecimen(Specimen specimen, Volume volume, @MappingTarget Specimen target);
}
