package cz.incad.nkp.inprove.permonikapi.volume;

import cz.incad.nkp.inprove.permonikapi.volume.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Creates new volume with specimens")
    @PostMapping()
    public String createVolumeWithSpecimens(@RequestBody CreatableVolumeWithSpecimensDTO creatableVolumeWithSpecimensDTO) throws SolrServerException, IOException {
        return volumeService.createVolumeWithSpecimens(creatableVolumeWithSpecimensDTO);
    }

    @Operation(summary = "Updates existing volume with specimens")
    @PutMapping("/{id}")
    public void updateVolumeWithSpecimens(@PathVariable String id, @RequestBody UpdatableVolumeWithSpecimensDTO updatableVolumeWithSpecimensDTO) throws SolrServerException, IOException {
        volumeService.updateVolumeWithSpecimens(id, updatableVolumeWithSpecimensDTO);
    }

    @Operation(summary = "Updates existing volume with regenerated specimens")
    @PutMapping("/{id}/regenerated")
    public void updateRegeneratedVolumeWithSpecimens(@PathVariable String id, @RequestBody UpdatableVolumeWithSpecimensDTO updatableVolumeWithSpecimensDTO) throws SolrServerException, IOException {
        volumeService.updateRegeneratedVolumeWithSpecimens(id, updatableVolumeWithSpecimensDTO);
    }

    @Operation(summary = "Gets volume by given id")
    @GetMapping("/{id}")
    public Optional<VolumeDTO> getVolumeById(@PathVariable String id) throws SolrServerException, IOException {
        return volumeService.getVolumeById(id);
    }

    @Operation(summary = "Gets managed volume detail with specimens by given id")
    @GetMapping("/{id}/detail")
    public Optional<VolumeDetailDTO> getMangedVolumeDetailById(@PathVariable String id) throws SolrServerException, IOException {
        return volumeService.getVolumeDetailById(id, false);
    }

    @Operation(summary = "Gets public volume detail with specimens by given id")
    @GetMapping("/{id}/detail/public")
    public Optional<VolumeDetailDTO> getPublicVolumeDetailById(@PathVariable String id) throws SolrServerException, IOException {
        return volumeService.getVolumeDetailById(id, true);
    }

    @Operation(summary = "Gets volume stats by given id")
    @GetMapping("/{id}/stats")
    public Optional<VolumeOverviewStatsDTO> getVolumeOverviewStats(@PathVariable String id) throws SolrServerException, IOException {
        return volumeService.getVolumeOverviewStats(id);
    }
}
