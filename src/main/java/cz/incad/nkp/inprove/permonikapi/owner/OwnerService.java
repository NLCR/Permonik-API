package cz.incad.nkp.inprove.permonikapi.owner;


import cz.incad.nkp.inprove.permonikapi.owner.dto.CreatableOwnerDTO;
import cz.incad.nkp.inprove.permonikapi.owner.mapper.CreatableOwnerMapper;
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
public class OwnerService implements OwnerDefinition {

    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);

    private final SolrClient solrClient;
    private final CreatableOwnerMapper creatableOwnerMapper;

    @Autowired
    public OwnerService(SolrClient solrClient, CreatableOwnerMapper creatableOwnerMapper) {
        this.solrClient = solrClient;
        this.creatableOwnerMapper = creatableOwnerMapper;
    }


    public List<Owner> getOwners() throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(100000);

        QueryResponse response = solrClient.query(OWNER_CORE_NAME, solrQuery);

        return response.getBeans(Owner.class);

    }

    public void updateOwner(String ownerId, Owner owner) throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(ID_FIELD + ":\"" + ownerId + "\"");
        solrQuery.setRows(1);

        QueryResponse response = solrClient.query(OWNER_CORE_NAME, solrQuery);

        List<Owner> ownerList = response.getBeans(Owner.class);

        if (ownerList.isEmpty()) {
            throw new RuntimeException("Owner not found");
        }

        try {
            solrClient.addBean(OWNER_CORE_NAME, owner);
            solrClient.commit(OWNER_CORE_NAME);
            logger.info("Owner {} successfully updated", owner.getName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update owner", e);
        }


    }

    public void createOwner(CreatableOwnerDTO owner) throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(NAME_FIELD + ":\"" + owner.name() + "\"");
        solrQuery.setRows(1);

        QueryResponse response = solrClient.query(OWNER_CORE_NAME, solrQuery);

        List<Owner> ownerList = response.getBeans(Owner.class);

        if (!ownerList.isEmpty()) {
            throw new RuntimeException("Owner with this name already exists");
        }

        Owner newOwner = new Owner();
        creatableOwnerMapper.createOwner(owner, newOwner);


        try {
            solrClient.addBean(OWNER_CORE_NAME, newOwner);
            solrClient.commit(OWNER_CORE_NAME);
            logger.info("Owner {} successfully created", owner);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create owner", e);
        }

    }

}
