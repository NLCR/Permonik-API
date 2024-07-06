package cz.incad.nkp.inprove.permonikapi.metaTitle;


import cz.incad.nkp.inprove.permonikapi.metaTitle.dto.MetaTitleOverViewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Tag(name = "MetaTitle API", description = "API for managing metaTitles")
@RestController
@RequestMapping("/api/metatitle")
public class MetaTitleController {

    private final MetaTitleService metaTitleService;

    @Autowired
    public MetaTitleController(MetaTitleService metaTitleService) {
        this.metaTitleService = metaTitleService;
    }


    @Operation(summary = "Gets metaTitle with given id")
    @GetMapping("/{id}")
    /* Endpoint used for specimens result table */
    public Optional<MetaTitle> getMetaTitleById(@PathVariable String id) throws SolrServerException, IOException {
        return metaTitleService.getMetaTitleById(id);
    }


    @Operation(summary = "List all metaTitles")
    @GetMapping("/list/all")
    public List<MetaTitleOverViewDTO> getMetaTitleOverview() throws SolrServerException, IOException {
        return metaTitleService.getMetaTitleOverview();
    }
}
