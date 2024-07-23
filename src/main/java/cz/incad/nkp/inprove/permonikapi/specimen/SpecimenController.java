package cz.incad.nkp.inprove.permonikapi.specimen;

import cz.incad.nkp.inprove.permonikapi.specimen.dto.FacetsDTO;
import cz.incad.nkp.inprove.permonikapi.specimen.dto.SearchedSpecimensDTO;
import cz.incad.nkp.inprove.permonikapi.volume.dto.VolumeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "Specimen API", description = "API for managing specimens")
@RestController
@RequestMapping("/api/specimen")
public class SpecimenController {

    private final SpecimenService specimenService;

    @Autowired
    public SpecimenController(SpecimenService specimenService) {
        this.specimenService = specimenService;
    }

    @Operation(summary = "Generates new specimens for volume")
    @PostMapping("/regenerate")
    public List<Specimen> generateSpecimens(@RequestBody VolumeDTO volumeDTO) {
        return specimenService.generateSpecimens(volumeDTO);
    }

    @Operation(summary = "Gets searched specimens for result table")
    @PostMapping("/{id}/list")
    public SearchedSpecimensDTO getSearchedSpecimens(
            @PathVariable String id,
            @RequestParam Integer offset,
            @RequestParam Integer rows,
            @RequestParam String facets,
            @RequestParam String view
    ) throws IOException, SolrServerException {
        return specimenService.getSearchedSpecimens(id, offset, rows, facets, view);
    }

    @Operation(summary = "Gets facets for searched specimens")
    @PostMapping("{id}/list/facets")
    public FacetsDTO getSpecimensFacets(
            @PathVariable String id,
            @RequestParam String facets
    ) throws IOException, SolrServerException {
        return specimenService.getSpecimensFacets(id, facets);
    }

    @Operation(summary = "Gets start date for calendar view")
    @GetMapping("{id}/start-date")
    public Object getSpecimensStartDate(@PathVariable String id) throws SolrServerException, IOException {
        return specimenService.getSpecimensStartDate(id);
    }


}
