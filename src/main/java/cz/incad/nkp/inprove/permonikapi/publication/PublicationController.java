package cz.incad.nkp.inprove.permonikapi.publication;

import cz.incad.nkp.inprove.permonikapi.publication.dto.PublicationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Tag(name = "Publication API", description = "API for managing publications")
@RestController
@RequestMapping("/api/publication")
public class PublicationController {

    private final PublicationService publicationService;

    @Autowired
    public PublicationController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @Operation(summary = "Lists all publications")
    @GetMapping("/list/all")
    public List<PublicationDTO> getPublications() throws SolrServerException, IOException {
        return publicationService.getPublications();
    }

}
