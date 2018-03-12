package de.ironicdev.spring.openleaf.repositories;

import de.ironicdev.spring.openleaf.models.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class EntryRepositoryImpl implements CustomEntryRepository {

    @Autowired
    MongoTemplate db;

    @Override
    public List<Entry> findByAttributeLike(String attribute, String value) {
        return null;
    }

    @Override
    public List<Entry> findByFilter(Map<String, String> filter, int page, int pageSize,
                                    List<String> orderByAttr, boolean orderDesc) {
        List<Entry> entryList = new ArrayList<>();

        Query query = new Query();
        List<AggregationOperation> operationList = new ArrayList<>();

        // add name filter, if exists
        if (filter.containsKey("name"))
            operationList.add(Aggregation.match(Criteria.where("name").regex(filter.get("name"))));

        // add description filter, if exists
        if (filter.containsKey("description"))
            operationList.add(Aggregation.match(Criteria.where("description").regex(filter.get("description"))));

        for (String FILTER : filter.keySet()) {
            if (FILTER.equals("name") || FILTER.equals("description")) continue; // escape already filtered properties

            // filter all attributes
            operationList.add(Aggregation.match(Criteria.where("attributes.name").is(FILTER)
                    .and("attributes.value").regex(filter.get(FILTER))));


            // TODO: Filter Locations, comments, etc. too
        }

        // add pagination, if desired
        long skipSize = page * pageSize;
        operationList.add(Aggregation.skip(skipSize));
        operationList.add(Aggregation.limit(pageSize));

        // add sortation, if desired
        if (orderByAttr != null && orderByAttr.size() > 0) {
            SortOperation sortBy;
            if (orderDesc)
                sortBy = sort(new Sort(Sort.Direction.DESC, orderByAttr));
            else
                sortBy = sort(new Sort(Sort.Direction.ASC, orderByAttr));
            operationList.add(sortBy);
        }


        Aggregation aggregation = Aggregation.newAggregation(operationList);

        AggregationResults<Entry> aggregatedEntries = db.aggregate(aggregation, "Entries", Entry.class);
        entryList = aggregatedEntries.getMappedResults();
        return entryList;
    }
}
