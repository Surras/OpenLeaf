package de.ironicdev.spring.openleaf.repositories;

import de.ironicdev.spring.openleaf.models.Entry;

import java.util.List;
import java.util.Map;

public interface CustomEntryRepository {
    List<Entry> findByAttributeLike(String attribute, String value);
    List<Entry> findByFilter(Map<String, String> filter, int page, int pageSize,
                             List<String> orderByAttr, boolean orderDesc);
}
