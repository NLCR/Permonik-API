package cz.incad.nkp.inprove.permonikapi.specimen;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.incad.nkp.inprove.permonikapi.specimen.dto.*;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.params.GroupParams;
import org.apache.solr.common.params.StatsParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SpecimenService implements SpecimenDefinition {

    private static final Logger logger = LoggerFactory.getLogger(SpecimenService.class);

    private final SolrClient solrClient;

    @Autowired
    public SpecimenService(SolrClient solrClient) {
        this.solrClient = solrClient;
    }


    public StatsForMetaTitleOverviewDTO getStatsForMetaTitleOverview(String metaTitleId) throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setFilterQueries(META_TITLE_ID_FIELD + ":\"" + metaTitleId + "\"", NUM_EXISTS_FIELD + ":true");
        solrQuery.setParam(StatsParams.STATS, true);
        solrQuery.setParam(StatsParams.STATS_FIELD, MUTATION_ID_FIELD, PUBLICATION_DATE_STRING_FIELD, OWNER_ID_FIELD);
        solrQuery.setParam(StatsParams.STATS_CALC_DISTINCT, true);
        solrQuery.setParam(GroupParams.GROUP, true);
        solrQuery.setParam(GroupParams.GROUP_FIELD, META_TITLE_ID_FIELD);
        solrQuery.setParam(GroupParams.GROUP_LIMIT, "1");
        solrQuery.setParam(GroupParams.GROUP_TOTAL_COUNT, true);
        solrQuery.setRows(0);

        QueryResponse response = solrClient.query(SPECIMEN_CORE_NAME, solrQuery);

        Map<String, FieldStatsInfo> statsInfo = response.getFieldStatsInfo();

        FieldStatsInfo mutationsStats = statsInfo.get(MUTATION_ID_FIELD);
        FieldStatsInfo publicationDayStats = statsInfo.get(PUBLICATION_DATE_STRING_FIELD);
        FieldStatsInfo ownersStats = statsInfo.get(OWNER_ID_FIELD);

        Long mutationsCount = mutationsStats.getCountDistinct();
        Object publicationDayMin = publicationDayStats.getMin();
        Object publicationDayMax = publicationDayStats.getMax();
        Long ownersCount = ownersStats.getCountDistinct();

        GroupResponse groupResponse = response.getGroupResponse();
        GroupCommand groupCommand = groupResponse.getValues().get(0);
        Integer matchedSpecimens = groupCommand.getMatches();


        return new StatsForMetaTitleOverviewDTO(publicationDayMin, publicationDayMax, mutationsCount, ownersCount, matchedSpecimens);

    }


    public SearchedSpecimensDTO getSearchedSpecimens(String metaTitleId, Integer offset, Integer rows, String facets, String view) throws IOException, SolrServerException {

        ObjectMapper objectMapper = new ObjectMapper();
        SpecimenFacets specimenFacets = objectMapper.readValue(facets, SpecimenFacets.class);

        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setFilterQueries(META_TITLE_ID_FIELD + ":\"" + metaTitleId + "\"", NUM_EXISTS_FIELD + ":true");

        if (!specimenFacets.getNames().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getNamesQueryString());
        }

        if (!specimenFacets.getSubNames().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getSubNamesQueryString());
        }

        if (!specimenFacets.getMutationIds().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getMutationsQueryString());
        }

        if (!specimenFacets.getPublicationIds().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getPublicationsQueryString());
        }

        if (!specimenFacets.getPublicationMarks().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getPublicationMarkQueryString());
        }


        if (!specimenFacets.getOwnerIds().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getOwnersQueryString());
        }

        if (!specimenFacets.getDamageTypes().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getDamageTypesQueryString());
        }

        if (!specimenFacets.getBarCode().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getBarCodeQueryString());
        }

        // Add filtering based on year interval
        if (specimenFacets.getDateStart() > 0 && specimenFacets.getDateEnd() > 0) {
            solrQuery.addFilterQuery(PUBLICATION_DATE_STRING_FIELD + ":[" + specimenFacets.getDateStart() + "0101 TO *]");
            solrQuery.addFilterQuery(PUBLICATION_DATE_STRING_FIELD + ":[* TO " + specimenFacets.getDateEnd() + "1231]");
        } else if (Objects.equals(view, "calendar") && !specimenFacets.getCalendarDateStart().isEmpty()) {
            solrQuery.addFilterQuery(PUBLICATION_DATE_FIELD + ":[" + specimenFacets.getCalendarDateStart() + " TO *]");
            solrQuery.addFilterQuery(PUBLICATION_DATE_FIELD + ":[* TO " + specimenFacets.getCalendarDateEnd() + "]");
        }

        // doing it this way, because we need same filters
        SolrQuery groupQuery;
        groupQuery = solrQuery;

        solrQuery.setParam(FacetParams.FACET, true);
        solrQuery.addFacetField(NAME_FIELD, SUB_NAME_FIELD, MUTATION_ID_FIELD, PUBLICATION_ID_FIELD, PUBLICATION_MARK_FIELD, OWNER_ID_FIELD, DAMAGE_TYPES_FIELD);
        solrQuery.setFacetMinCount(1);
        solrQuery.setRows(rows);
        solrQuery.setStart(offset);
        solrQuery.setSort(PUBLICATION_DATE_STRING_FIELD, SolrQuery.ORDER.asc);
        // TODO: join is now working, it always returns unknown field volumeId
//        solrQuery.add("join", "{!join from=volumeId to=id fromIndex=volume}barCode:barCode");
        // TODO this will be sorting based on UUID, that's wrong
        solrQuery.addSort(PUBLICATION_ID_FIELD, SolrQuery.ORDER.desc);
//        solrQuery.addSort(MUTATION_ID_FIELD, SolrQuery.ORDER.asc);

        QueryResponse response = solrClient.query(SPECIMEN_CORE_NAME, solrQuery);
        List<Specimen> specimenList = response.getBeans(Specimen.class);

        SolrQuery statsQuery = new SolrQuery("*:*");
        statsQuery.setFilterQueries(META_TITLE_ID_FIELD + ":\"" + metaTitleId + "\"", NUM_EXISTS_FIELD + ":true");
        statsQuery.setRows(0);
        statsQuery.setParam(StatsParams.STATS, true);
        statsQuery.setParam(StatsParams.STATS_FIELD, PUBLICATION_DATE_STRING_FIELD);

        QueryResponse statsResponse = solrClient.query(SPECIMEN_CORE_NAME, statsQuery);

        Map<String, FieldStatsInfo> statsInfo = statsResponse.getFieldStatsInfo();
        FieldStatsInfo publicationDayStats = statsInfo.get(PUBLICATION_DATE_STRING_FIELD);

        Object publicationDayMin = publicationDayStats.getMin();
        Object publicationDayMax = publicationDayStats.getMax();

        groupQuery.setRows(rows);
        groupQuery.setStart(offset);
        groupQuery.setParam(GroupParams.GROUP, true);
        groupQuery.setParam(GroupParams.GROUP_FIELD, VOLUME_ID_FIELD);
        groupQuery.setParam(GroupParams.GROUP_LIMIT, "20");
        groupQuery.setParam(GroupParams.GROUP_TOTAL_COUNT, true);

        GroupResponse groupResponse = solrClient.query(SPECIMEN_CORE_NAME, solrQuery).getGroupResponse();

        GroupCommand groupCommand = groupResponse.getValues().get(0);
        Integer groupedSpecimens = groupCommand.getMatches();

        return new SearchedSpecimensDTO(
                specimenList,
                publicationDayMax,
                publicationDayMin,
                groupedSpecimens
        );

    }

    public FacetsDTO getSpecimensFacets(String metaTitleId, String facets) throws IOException, SolrServerException {

        ObjectMapper objectMapper = new ObjectMapper();
        SpecimenFacets specimenFacets = objectMapper.readValue(facets, SpecimenFacets.class);

        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setFilterQueries(META_TITLE_ID_FIELD + ":\"" + metaTitleId + "\"", NUM_EXISTS_FIELD + ":true");
        solrQuery.setRows(0);
        solrQuery.setStart(0);
//        solrQuery.setSort(PUBLICATION_DATE_STRING_FIELD, SolrQuery.ORDER.asc);
        // TODO this will be sorting based on UUID, that's wrong
        solrQuery.addSort(PUBLICATION_ID_FIELD, SolrQuery.ORDER.desc);
//        solrQuery.addSort(MUTATION_ID_FIELD, SolrQuery.ORDER.asc);
        solrQuery.setFacet(true);
        solrQuery.addFacetField(NAME_FIELD, SUB_NAME_FIELD, MUTATION_ID_FIELD, PUBLICATION_ID_FIELD, PUBLICATION_MARK_FIELD, OWNER_ID_FIELD, DAMAGE_TYPES_FIELD);
        solrQuery.setFacetMinCount(1);

        if (!specimenFacets.getNames().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getNamesQueryString());
        }

        if (!specimenFacets.getSubNames().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getSubNamesQueryString());
        }

        if (!specimenFacets.getMutationIds().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getMutationsQueryString());
        }

        if (!specimenFacets.getPublicationIds().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getPublicationsQueryString());
        }

        if (!specimenFacets.getPublicationMarks().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getPublicationMarkQueryString());
        }

        if (!specimenFacets.getOwnerIds().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getOwnersQueryString());
        }

        if (!specimenFacets.getDamageTypes().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getDamageTypesQueryString());
        }

        if (!specimenFacets.getBarCode().isEmpty()) {
            solrQuery.addFilterQuery(specimenFacets.getBarCodeQueryString());
        }

        // Add filtering based on year interval
        if (specimenFacets.getDateStart() > 0 && specimenFacets.getDateEnd() > 0) {
            solrQuery.addFilterQuery(PUBLICATION_DATE_STRING_FIELD + ":[" + specimenFacets.getDateStart() + "0101 TO *]");
            solrQuery.addFilterQuery(PUBLICATION_DATE_STRING_FIELD + ":[* TO " + specimenFacets.getDateEnd() + "1231]");
        }

        QueryResponse response = solrClient.query(SPECIMEN_CORE_NAME, solrQuery);

        return new FacetsDTO(
                response.getFacetField(NAME_FIELD).getValues().stream().map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getName(), facetFieldEntry.getCount())).toList(),
                response.getFacetField(SUB_NAME_FIELD).getValues().stream().map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getName(), facetFieldEntry.getCount())).toList(),
                response.getFacetField(MUTATION_ID_FIELD).getValues().stream().map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getName(), facetFieldEntry.getCount())).toList(),
                response.getFacetField(PUBLICATION_ID_FIELD).getValues().stream().map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getName(), facetFieldEntry.getCount())).toList(),
                response.getFacetField(PUBLICATION_MARK_FIELD).getValues().stream().map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getName(), facetFieldEntry.getCount())).toList(),
                response.getFacetField(OWNER_ID_FIELD).getValues().stream().map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getName(), facetFieldEntry.getCount())).toList(),
                response.getFacetField(DAMAGE_TYPES_FIELD).getValues().stream().map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getName(), facetFieldEntry.getCount())).toList()
        );

    }


    //    public List<Specimen> getSpecimensForVolumeDetail(String volumeId, String dateFrom, String dateTo) throws SolrServerException, IOException {
    public List<Specimen> getSpecimensForVolumeDetail(String volumeId, Boolean onlyPublic) throws SolrServerException, IOException {

        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(VOLUME_ID_FIELD + ":\"" + volumeId + "\"");
        if (onlyPublic) {
            solrQuery.addFilterQuery(NUM_EXISTS_FIELD + ":true OR " + NUM_MISSING_FIELD + ":true");
        }
//        solrQuery.addFilterQuery(PUBLICATION_DATE_FIELD + ":[" + dateFrom + " TO " + dateTo + "]");
        solrQuery.setSort(PUBLICATION_DATE_STRING_FIELD, SolrQuery.ORDER.asc);
//        solrQuery.setParam(StatsParams.STATS, true);
//        solrQuery.setParam(StatsParams.STATS_FIELD, PUBLICATION_DATE_STRING_FIELD);
        solrQuery.setRows(100000);

        QueryResponse response = solrClient.query(SPECIMEN_CORE_NAME, solrQuery);

        return response.getBeans(Specimen.class);

//        Map<String, FieldStatsInfo> statsInfo = response.getFieldStatsInfo();

//        Object publicationDayMin = statsInfo.get(PUBLICATION_DATE_STRING_FIELD).getMin();
//        Object publicationDayMax = statsInfo.get(PUBLICATION_DATE_STRING_FIELD).getMax();

//        return new SpecimensForVolumeDetailDTO(
//                response.getBeans(Specimen.class),
//                new SpecimensPublicationRangeDTO(
//                        publicationDayMin,
//                        publicationDayMax
//                )
//        );

    }

    public Object getSpecimensStartDate(String metaTitleId) throws SolrServerException, IOException {

        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(META_TITLE_ID_FIELD + ":\"" + metaTitleId + "\"");
        solrQuery.addFilterQuery(NUM_EXISTS_FIELD + ":true");
        solrQuery.setParam(StatsParams.STATS, true);
        solrQuery.setParam(StatsParams.STATS_FIELD, PUBLICATION_DATE_STRING_FIELD);
        solrQuery.setRows(0);

        QueryResponse response = solrClient.query(SPECIMEN_CORE_NAME, solrQuery);

        Map<String, FieldStatsInfo> statsInfo = response.getFieldStatsInfo();

        return statsInfo.get(PUBLICATION_DATE_STRING_FIELD).getMin();

    }


    public SpecimensForVolumeOverviewDTO getSpecimensForVolumeOverview(String volumeId) throws SolrServerException, IOException {

        Calendar date = new GregorianCalendar();

        Calendar start = new GregorianCalendar(1700, Calendar.JANUARY, 1);
        Calendar end = new GregorianCalendar(date.get(Calendar.YEAR), Calendar.JANUARY, 1);

        Date startDate = start.getTime();
        Date endDate = end.getTime();

        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery(VOLUME_ID_FIELD + ":\"" + volumeId + "\"");
        solrQuery.addFilterQuery(NUM_EXISTS_FIELD + ":true");
        solrQuery.setParam(StatsParams.STATS, true);
        solrQuery.setParam(StatsParams.STATS_FIELD, NUMBER_FIELD, PUBLICATION_DATE_STRING_FIELD, PAGES_COUNT_FIELD);
        solrQuery.setRows(0);
        solrQuery.setFacet(true);
        solrQuery.addFacetField(MUTATION_ID_FIELD, PUBLICATION_MARK_FIELD, PUBLICATION_ID_FIELD, DAMAGE_TYPES_FIELD);
        solrQuery.addDateRangeFacet(PUBLICATION_DATE_FIELD, startDate, endDate, "+1YEAR");
        solrQuery.setFacetMinCount(1);

        QueryResponse response = solrClient.query(SPECIMEN_CORE_NAME, solrQuery);

        Map<String, FieldStatsInfo> statsInfo = response.getFieldStatsInfo();

        Object publicationDayMin = statsInfo.get(PUBLICATION_DATE_STRING_FIELD).getMin();
        Object publicationDayMax = statsInfo.get(PUBLICATION_DATE_STRING_FIELD).getMax();
        Object numberMin = statsInfo.get(NUMBER_FIELD).getMin();
        Object numberMax = statsInfo.get(NUMBER_FIELD).getMax();
        Object pagesCount = statsInfo.get(PAGES_COUNT_FIELD).getSum();

        SolrQuery solrQuery2 = new SolrQuery("*:*");
        solrQuery2.addFilterQuery(VOLUME_ID_FIELD + ":\"" + volumeId + "\"");
        solrQuery2.addFilterQuery(NUM_EXISTS_FIELD + ":true");
        solrQuery2.setRows(100000);

        QueryResponse response2 = solrClient.query(SPECIMEN_CORE_NAME, solrQuery2);
        List<Specimen> specimens = response2.getBeans(Specimen.class);


        List<RangeFacet.Count> rangeFacetCounts = response.getFacetRanges().stream()
                .filter(rangeFacet -> rangeFacet.getName().equals(PUBLICATION_DATE_FIELD))
                .findFirst()
                .map(RangeFacet::getCounts)
                .orElse(null);

        List<FacetFieldDTO> publicationDateList = rangeFacetCounts.stream()
                .map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getValue(), (long) facetFieldEntry.getCount()))
                .toList();

        return new SpecimensForVolumeOverviewDTO(
                publicationDayMin,
                publicationDayMax,
                numberMin,
                numberMax,
                pagesCount,
                response.getFacetField(MUTATION_ID_FIELD).getValues().stream().map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getName(), facetFieldEntry.getCount())).toList(),
                response.getFacetField(PUBLICATION_MARK_FIELD).getValues().stream().map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getName(), facetFieldEntry.getCount())).toList(),
                response.getFacetField(PUBLICATION_ID_FIELD).getValues().stream().map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getName(), facetFieldEntry.getCount())).toList(),
                response.getFacetField(DAMAGE_TYPES_FIELD).getValues().stream().map(facetFieldEntry -> new FacetFieldDTO(facetFieldEntry.getName(), facetFieldEntry.getCount())).toList(),
                publicationDateList,
                specimens
        );

    }


    public void createSpecimens(List<Specimen> specimen) {
        try {
            solrClient.addBeans(SPECIMEN_CORE_NAME, specimen);
            solrClient.commit(SPECIMEN_CORE_NAME);
            logger.info("specimens successfully created");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create specimens", e);
        }
    }

    public void updateSpecimens(List<Specimen> specimen) {
        try {
            solrClient.addBeans(SPECIMEN_CORE_NAME, specimen);
            solrClient.commit(SPECIMEN_CORE_NAME);
            logger.info("specimens successfully updated");
        } catch (Exception e) {
            throw new RuntimeException("Failed to update specimens", e);
        }
    }

    public void deleteSpecimens(List<Specimen> specimen) {
        try {
            solrClient.deleteById(SPECIMEN_CORE_NAME, specimen.stream().map(Specimen::getId).toList());
            solrClient.commit(SPECIMEN_CORE_NAME);
            logger.info("specimens successfully deleted");
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete specimens", e);
        }
    }

}
