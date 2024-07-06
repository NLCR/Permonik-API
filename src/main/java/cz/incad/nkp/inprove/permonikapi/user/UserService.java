package cz.incad.nkp.inprove.permonikapi.user;

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
public class UserService implements UserDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final SolrClient solrClient;

    @Autowired
    public UserService(SolrClient solrClient) {
        this.solrClient = solrClient;
    }


    public List<User> getUsers() throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setQuery("*:*");
        solrQuery.setSort(USERNAME_FIELD, SolrQuery.ORDER.asc);

        QueryResponse response = solrClient.query(USER_CORE_NAME, solrQuery);

        return response.getBeans(User.class);

    }

    public Boolean updateUser(String userId, User user) throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery("id:\"" + userId + "\"");
        solrQuery.setRows(1);

        QueryResponse response = solrClient.query(USER_CORE_NAME, solrQuery);

        List<User> userList = response.getBeans(User.class);

        if (!userList.isEmpty()) {

            User userResponse = userList.get(0);

            userResponse.setEmail(user.getEmail());
            userResponse.setUserName(user.getUserName());
            userResponse.setFirstName(user.getFirstName());
            userResponse.setLastName(user.getLastName());
            userResponse.setRole(user.getRole());
            userResponse.setActive(user.getActive());
            userResponse.setOwners(user.getOwners());

            try {
                solrClient.addBean(USER_CORE_NAME, userResponse);
                solrClient.commit(USER_CORE_NAME);
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            return false;
        }

    }
}
