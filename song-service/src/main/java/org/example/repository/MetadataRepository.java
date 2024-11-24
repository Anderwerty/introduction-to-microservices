package org.example.repository;

import org.example.entity.SongMetadata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepository extends CrudRepository<SongMetadata, Integer> {


}
