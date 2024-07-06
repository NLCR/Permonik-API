package cz.incad.nkp.inprove.permonikapi.volume;

import cz.incad.nkp.inprove.permonikapi.volume.dto.VolumeDTO;
import cz.incad.nkp.inprove.permonikapi.volume.dto.VolumeDetailDTO;
import cz.incad.nkp.inprove.permonikapi.volume.dto.VolumeOverviewStatsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@Tag(name = "Volume API", description = "API for managing volumes")
@RestController
@RequestMapping("/api/volume")
public class VolumeController {

    private final VolumeService volumeService;

    @Autowired
    public VolumeController(VolumeService volumeService) {
        this.volumeService = volumeService;
    }

    @Operation(summary = "Gets volume by given id")
    @GetMapping("/{id}")
    public Optional<VolumeDTO> getVolumeById(@PathVariable String id) throws SolrServerException, IOException {
        return volumeService.getVolumeById(id);
    }

    @Operation(summary = "Gets volume detail with specimens by given id")
    @GetMapping("/{id}/detail")
    public Optional<VolumeDetailDTO> getVolumeDetailById(@PathVariable String id) throws SolrServerException, IOException {
        return volumeService.getVolumeDetailById(id);
    }

    @Operation(summary = "Gets volume stats by given id")
    @GetMapping("/{id}/stats")
    public Optional<VolumeOverviewStatsDTO> getVolumeOverviewStats(@PathVariable String id) throws SolrServerException, IOException {
        return volumeService.getVolumeOverviewStats(id);
    }
}
