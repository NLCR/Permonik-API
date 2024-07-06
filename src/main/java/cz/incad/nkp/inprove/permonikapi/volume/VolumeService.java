package cz.incad.nkp.inprove.permonikapi.volume;

import cz.incad.nkp.inprove.permonikapi.metaTitle.MetaTitleService;
import cz.incad.nkp.inprove.permonikapi.specimen.SpecimenService;
import cz.incad.nkp.inprove.permonikapi.specimen.dto.SpecimensForVolumeDetailDTO;
import cz.incad.nkp.inprove.permonikapi.specimen.dto.SpecimensForVolumeOverviewDTO;
import cz.incad.nkp.inprove.permonikapi.volume.dto.VolumeDetailDTO;
import cz.incad.nkp.inprove.permonikapi.volume.dto.VolumeOverviewStatsDTO;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(VolumeService.class);

    private final MetaTitleService metaTitleService;
    private final SpecimenService specimenService;
    private final SolrClient solrClient;

    @Autowired
    public VolumeService(MetaTitleService metaTitleService, SpecimenService specimenService, SolrClient solrClient) {
        this.metaTitleService = metaTitleService;
        this.specimenService = specimenService;
        this.solrClient = solrClient;
    }

    public Optional<Volume> getVolumeById(String volumeId) throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(ID_FIELD + ":\"" + volumeId + "\"");
        solrQuery.setFields("*", PERIODICITY_FIELD + ":[json]");
        solrQuery.setRows(1);

        QueryResponse response = solrClient.query(VOLUME_CORE_NAME, solrQuery);

        List<Volume> volumeList = response.getBeans(Volume.class);


        return volumeList.isEmpty() ? Optional.empty() : Optional.of(volumeList.get(0));
    }

    public Optional<VolumeDetailDTO> getVolumeDetailById(String volumeId) throws SolrServerException, IOException {
        return getVolumeById(volumeId)
                .flatMap(volume -> {
                            try {
                                return metaTitleService.getMetaTitleById(volume.getMetaTitleId())
                                        .flatMap(metaTitle -> {
                                            try {
                                                SpecimensForVolumeDetailDTO specimensForVolumeDetailDTO = specimenService.getSpecimensForVolumeDetail(volume.getId(), volume.getDateFrom(), volume.getDateTo());

                                                return Optional.of(new VolumeDetailDTO(
                                                        volume,
                                                        metaTitle,
                                                        specimensForVolumeDetailDTO
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

    public Optional<VolumeOverviewStatsDTO> getVolumeOverviewStats(String volumeId) throws SolrServerException, IOException {
        return getVolumeById(volumeId)
                .flatMap(volume -> {
                            try {
                                return metaTitleService.getMetaTitleById(volume.getMetaTitleId())
                                        .flatMap(metaTitle -> {
                                            try {
                                                SpecimensForVolumeOverviewDTO specimensForVolumeOverview = specimenService.getSpecimensForVolumeOverview(volumeId);

                                                return Optional.of(new VolumeOverviewStatsDTO(
                                                        metaTitle.getName(),
                                                        volume.getOwnerId(),
                                                        volume.getSignature(),
                                                        volume.getBarCode(),
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
}
