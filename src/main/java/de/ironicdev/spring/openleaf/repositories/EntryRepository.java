package de.ironicdev.spring.openleaf.repositories;

import de.ironicdev.spring.openleaf.models.Entry;
import de.ironicdev.spring.openleaf.models.EntryCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EntryRepository extends CrudRepository<Entry, String>, CustomEntryRepository {
    List<Entry> findByNameLike(String name);

    List<Entry> findByCategory(EntryCategory category);
}
