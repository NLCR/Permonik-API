package cz.incad.nkp.inprove.permonikapi.mutation;


import cz.incad.nkp.inprove.permonikapi.mutation.dto.MutationDTO;
import cz.incad.nkp.inprove.permonikapi.mutation.mapper.MutationDTOMapper;
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
public class MutationService implements MutationDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(MutationService.class);

    private final MutationDTOMapper mutationDTOMapper;
    private final SolrClient solrClient;

    @Autowired
    public MutationService(MutationDTOMapper mutationDTOMapper, SolrClient solrClient) {
        this.mutationDTOMapper = mutationDTOMapper;
        this.solrClient = solrClient;
    }


    public List<MutationDTO> getMutations() throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(100000);

        QueryResponse response = solrClient.query(MUTATION_CORE_NAME, solrQuery);

        List<Mutation> mutationList = response.getBeans(Mutation.class);

        return mutationList.stream().map(mutationDTOMapper).toList();
    }
}
