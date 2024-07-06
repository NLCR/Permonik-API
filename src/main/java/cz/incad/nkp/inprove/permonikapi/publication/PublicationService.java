package cz.incad.nkp.inprove.permonikapi.publication;


import cz.incad.nkp.inprove.permonikapi.publication.dto.PublicationDTO;
import cz.incad.nkp.inprove.permonikapi.publication.mapper.PublicationDTOMapper;
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

@Service
public class PublicationService implements PublicationDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicationService.class);

    private final PublicationDTOMapper publicationDTOMapper;
    private final SolrClient solrClient;

    @Autowired
    public PublicationService(PublicationDTOMapper publicationDTOMapper, SolrClient solrClient) {
        this.publicationDTOMapper = publicationDTOMapper;
        this.solrClient = solrClient;
    }


    public List<PublicationDTO> getPublications() throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(100000);

        QueryResponse response = solrClient.query(PUBLICATION_CORE_NAME, solrQuery);

        List<Publication> publicationList = response.getBeans(Publication.class);

        return publicationList.stream().map(publicationDTOMapper).toList();
    }
}
