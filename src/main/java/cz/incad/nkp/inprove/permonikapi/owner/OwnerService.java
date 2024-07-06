package cz.incad.nkp.inprove.permonikapi.owner;


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

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerService.class);

    private final SolrClient solrClient;
 
    @Autowired
    public OwnerService(SolrClient solrClient) {
        this.solrClient = solrClient;
    }


    public List<Owner> getOwners() throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(100000);

        QueryResponse response = solrClient.query(OWNER_CORE_NAME, solrQuery);

        return response.getBeans(Owner.class);

    }
}
