package cz.incad.nkp.inprove.permonikapi.volume;

import cz.incad.nkp.inprove.permonikapi.metaTitle.MetaTitleService;
import cz.incad.nkp.inprove.permonikapi.specimen.Specimen;
import cz.incad.nkp.inprove.permonikapi.specimen.SpecimenService;
import cz.incad.nkp.inprove.permonikapi.specimen.dto.SpecimensForVolumeOverviewDTO;
import cz.incad.nkp.inprove.permonikapi.volume.dto.EditableVolumeWithSpecimensDTO;
import cz.incad.nkp.inprove.permonikapi.volume.dto.VolumeDTO;
import cz.incad.nkp.inprove.permonikapi.volume.dto.VolumeDetailDTO;
import cz.incad.nkp.inprove.permonikapi.volume.dto.VolumeOverviewStatsDTO;
import cz.incad.nkp.inprove.permonikapi.volume.mapper.VolumeDTOMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class VolumeService implements VolumeDefinition {

    private static final Logger logger = LoggerFactory.getLogger(VolumeService.class);

    private final MetaTitleService metaTitleService;
    private final SpecimenService specimenService;
    private final SolrClient solrClient;
    private final VolumeDTOMapper volumeDTOMapper;

    @Autowired
    public VolumeService(MetaTitleService metaTitleService, SpecimenService specimenService, SolrClient solrClient, VolumeDTOMapper volumeDTOMapper) {
        this.metaTitleService = metaTitleService;
        this.specimenService = specimenService;
        this.solrClient = solrClient;
        this.volumeDTOMapper = volumeDTOMapper;
    }

    public Optional<VolumeDTO> getVolumeById(String volumeId) throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(ID_FIELD + ":\"" + volumeId + "\"");
        solrQuery.setRows(1);

        QueryResponse response = solrClient.query(VOLUME_CORE_NAME, solrQuery);

        List<Volume> volumeList = response.getBeans(Volume.class);


        return volumeList.isEmpty() ? Optional.empty() : Optional.of(volumeDTOMapper.apply(volumeList.get(0)));
    }

    public Optional<VolumeDetailDTO> getVolumeDetailById(String volumeId, Boolean onlyPublic) throws SolrServerException, IOException {
        return getVolumeById(volumeId)
                .flatMap(volume -> {
                            try {
                                List<Specimen> specimenList = specimenService.getSpecimensForVolumeDetail(volume.id(), onlyPublic);

                                return Optional.of(new VolumeDetailDTO(
                                        volume,
                                        specimenList
                                ));
                            } catch (SolrServerException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    public Optional<VolumeOverviewStatsDTO> getVolumeOverviewStats(String volumeId) throws SolrServerException, IOException {
        return getVolumeById(volumeId)
                .flatMap(volume -> {
                            try {
                                return metaTitleService.getMetaTitleById(volume.metaTitleId())
                                        .flatMap(metaTitle -> {
                                            try {
                                                SpecimensForVolumeOverviewDTO specimensForVolumeOverview = specimenService.getSpecimensForVolumeOverview(volumeId);

                                                return Optional.of(new VolumeOverviewStatsDTO(
                                                        metaTitle.getName(),
                                                        volume.ownerId(),
                                                        volume.signature(),
                                                        volume.barCode(),
                                                        specimensForVolumeOverview.publicationDayMin(),
                                                        specimensForVolumeOverview.publicationDayMax(),
                                                        specimensForVolumeOverview.numberMin(),
                                                        specimensForVolumeOverview.numberMax(),
                                                        specimensForVolumeOverview.pagesCount(),
                                                        specimensForVolumeOverview.mutationIds(),
                                                        specimensForVolumeOverview.publicationMark(),
                                                        specimensForVolumeOverview.publicationIds(),
                                                        specimensForVolumeOverview.damageTypes(),
                                                        specimensForVolumeOverview.publicationDayRanges(),
                                                        specimensForVolumeOverview.specimens()
                                                ));
                                            } catch (SolrServerException | IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
                            } catch (SolrServerException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );

    }

    private void createVolume(Volume volume) {
        try {
            solrClient.addBean(VOLUME_CORE_NAME, volume);
            solrClient.commit(VOLUME_CORE_NAME);
            logger.info("volume {} successfully created", volume.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create volume", e);
        }
    }

    private void updateVolume(Volume volume) {
        try {
            solrClient.addBean(VOLUME_CORE_NAME, volume);
            solrClient.commit(VOLUME_CORE_NAME);
            logger.info("volume {} successfully updated", volume.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update volume", e);
        }
    }

    private void deleteVolume(String id) {
        try {
            solrClient.deleteById(VOLUME_CORE_NAME, id);
            solrClient.commit(VOLUME_CORE_NAME);
            logger.info("volume {} successfully delete", id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete volume", e);
        }
    }

    public String createVolumeWithSpecimens(EditableVolumeWithSpecimensDTO editableVolumeWithSpecimensDTO) throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(BAR_CODE_FIELD + ":\"" + editableVolumeWithSpecimensDTO.volume().getBarCode() + "\"");
        solrQuery.setRows(1);

        QueryResponse response = solrClient.query(VOLUME_CORE_NAME, solrQuery);

        List<Volume> volumeList = response.getBeans(Volume.class);

        if (!volumeList.isEmpty()) {
            throw new RuntimeException("Volume with barcode" + editableVolumeWithSpecimensDTO.volume().getBarCode() + "already exists");
        }

        this.createVolume(editableVolumeWithSpecimensDTO.volume());

        specimenService.createSpecimens(editableVolumeWithSpecimensDTO.specimens());

        return "\"" + editableVolumeWithSpecimensDTO.volume().getId() + "\"";

    }


    public void updateVolumeWithSpecimens(String volumeId, EditableVolumeWithSpecimensDTO editableVolumeWithSpecimensDTO) throws SolrServerException, IOException {
        if (getVolumeById(volumeId).isEmpty()) {
            throw new RuntimeException("Volume " + volumeId + " not found");
        }

        this.updateVolume(editableVolumeWithSpecimensDTO.volume());

        specimenService.updateSpecimens(editableVolumeWithSpecimensDTO.specimens());

    }

    public void deleteVolumeWithSpecimens(String volumeId) throws SolrServerException, IOException {
        if (getVolumeById(volumeId).isEmpty()) {
            throw new RuntimeException("Volume " + volumeId + " not found");
        }

        List<Specimen> specimens = specimenService.getSpecimensForVolumeDetail(volumeId, false);
        specimenService.deleteSpecimens(specimens);

        this.deleteVolume(volumeId);

    }

    public void updateRegeneratedVolumeWithSpecimens(String volumeId, EditableVolumeWithSpecimensDTO editableVolumeWithSpecimensDTO) throws SolrServerException, IOException {
        if (getVolumeById(volumeId).isEmpty()) {
            throw new RuntimeException("Volume " + volumeId + " not found");
        }

        // delete old specimens
        List<Specimen> oldSpecimens = specimenService.getSpecimensForVolumeDetail(volumeId, false);
        specimenService.deleteSpecimens(oldSpecimens);

        this.updateVolume(editableVolumeWithSpecimensDTO.volume());

        specimenService.createSpecimens(editableVolumeWithSpecimensDTO.specimens());

    }
}
