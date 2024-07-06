package cz.incad.nkp.inprove.permonikapi.mutation;

import cz.incad.nkp.inprove.permonikapi.mutation.dto.MutationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Tag(name = "Mutation API", description = "API for managing mutations")
@RestController
@RequestMapping("/api/mutation")
public class MutationController {

    private final MutationService mutationService;

    @Autowired
    public MutationController(MutationService mutationService) {
        this.mutationService = mutationService;
    }

    @Operation(summary = "Lists all mutations")
    @GetMapping("/list/all")
    public List<MutationDTO> getMutations() throws SolrServerException, IOException {
        return mutationService.getMutations();
    }

}
