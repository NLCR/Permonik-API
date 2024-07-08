package cz.incad.nkp.inprove.permonikapi.publication;


import cz.incad.nkp.inprove.permonikapi.publication.dto.CreatablePublicationDTO;
import cz.incad.nkp.inprove.permonikapi.publication.dto.PublicationDTO;
import cz.incad.nkp.inprove.permonikapi.publication.mapper.CreatablePublicationMapper;
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

    private static final Logger logger = LoggerFactory.getLogger(PublicationService.class);

    private final PublicationDTOMapper publicationDTOMapper;
    private final SolrClient solrClient;
    private final CreatablePublicationMapper creatablePublicationMapper;

    @Autowired
    public PublicationService(PublicationDTOMapper publicationDTOMapper, SolrClient solrClient, CreatablePublicationMapper creatablePublicationMapper) {
        this.publicationDTOMapper = publicationDTOMapper;
        this.solrClient = solrClient;
        this.creatablePublicationMapper = creatablePublicationMapper;
    }


    public List<PublicationDTO> getPublications() throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(100000);

        QueryResponse response = solrClient.query(PUBLICATION_CORE_NAME, solrQuery);

        List<Publication> publicationList = response.getBeans(Publication.class);

        return publicationList.stream().map(publicationDTOMapper).toList();
    }

    public void updatePublication(String publicationId, Publication publication) throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(ID_FIELD + ":\"" + publicationId + "\"");
        solrQuery.setRows(1);

        QueryResponse response = solrClient.query(PUBLICATION_CORE_NAME, solrQuery);

        List<Publication> publicationList = response.getBeans(Publication.class);

        if (publicationList.isEmpty()) {
            throw new RuntimeException("Publication not found");
        }

        try {
            solrClient.addBean(PUBLICATION_CORE_NAME, publication);
            solrClient.commit(PUBLICATION_CORE_NAME);
            logger.info("Publication {} successfully updated", publication.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update publication", e);
        }


    }

    public void createPublication(CreatablePublicationDTO publication) throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(NAME_FIELD + ":\"" + publication.name() + "\"");
        solrQuery.setRows(1);

        QueryResponse response = solrClient.query(PUBLICATION_CORE_NAME, solrQuery);

        List<Publication> publicationList = response.getBeans(Publication.class);

        if (!publicationList.isEmpty()) {
            throw new RuntimeException("Publication with this name already exists");
        }

        Publication newPublication = new Publication();
        creatablePublicationMapper.createPublication(publication, newPublication);


        try {
            solrClient.addBean(PUBLICATION_CORE_NAME, newPublication);
            solrClient.commit(PUBLICATION_CORE_NAME);
            logger.info("Publication {} successfully created", publication);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create publication", e);
        }

    }

}
