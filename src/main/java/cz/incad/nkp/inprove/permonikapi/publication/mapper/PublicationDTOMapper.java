package cz.incad.nkp.inprove.permonikapi.publication.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.incad.nkp.inprove.permonikapi.publication.Publication;
import cz.incad.nkp.inprove.permonikapi.publication.dto.PublicationDTO;
import cz.incad.nkp.inprove.permonikapi.publication.dto.PublicationNameDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PublicationDTOMapper implements Function<Publication, PublicationDTO> {

    private PublicationNameDTO getNames(String names) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(names, PublicationNameDTO.class);

    }


    @Override
    public PublicationDTO apply(Publication publication) {
        try {
            return new PublicationDTO(
                    publication.getId(),
                    getNames(publication.getName()),
                    publication.getIsDefault(),
                    publication.getIsAttachment()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
