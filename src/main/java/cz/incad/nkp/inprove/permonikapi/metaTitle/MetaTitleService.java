package cz.incad.nkp.inprove.permonikapi.metaTitle;

import cz.incad.nkp.inprove.permonikapi.metaTitle.dto.MetaTitleOverViewDTO;
import cz.incad.nkp.inprove.permonikapi.specimen.SpecimenService;
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
public class MetaTitleService implements MetaTitleDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpecimenService.class);

    private final SpecimenService specimenService;
    private final SolrClient solrClient;

    @Autowired
    public MetaTitleService(SpecimenService specimenService, SolrClient solrClient) {
        this.specimenService = specimenService;
        this.solrClient = solrClient;
    }

    public Optional<MetaTitle> getMetaTitleById(String metaTitleId) throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(IS_PUBLIC_FIELD + ":true", ID_FIELD + ":\"" + metaTitleId + "\"");
        solrQuery.setRows(1);

        QueryResponse response = solrClient.query(META_TITLE_CORE_NAME, solrQuery);
        List<MetaTitle> metaTitleList = response.getBeans(MetaTitle.class);

        return metaTitleList.isEmpty() ? Optional.empty() : Optional.of(metaTitleList.get(0));
    }

    public List<MetaTitle> getAllMetaTitles() throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(IS_PUBLIC_FIELD + ":true");
        solrQuery.setRows(100000);

        QueryResponse response = solrClient.query(META_TITLE_CORE_NAME, solrQuery);

        return response.getBeans(MetaTitle.class);
    }

    public List<MetaTitleOverViewDTO> getMetaTitleOverview() throws SolrServerException, IOException {
        List<MetaTitle> metaTitles = getAllMetaTitles();
        return metaTitles
                .stream()
                .map(metaTitle -> {
                    try {
                        return new MetaTitleOverViewDTO(
                                metaTitle.getId(),
                                metaTitle.getName(),
                                specimenService.getStatsForMetaTitleOverview(metaTitle.getId())
                        );
                    } catch (SolrServerException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

}
