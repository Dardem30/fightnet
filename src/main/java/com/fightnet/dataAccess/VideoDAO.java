package com.fightnet.dataAccess;

import com.fightnet.models.Video;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface VideoDAO extends CrudRepository<Video, Long> {
    Set<Video> findTop10ByApprovedAndLoaded(boolean approved, boolean loaded);
}