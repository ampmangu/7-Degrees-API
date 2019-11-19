package com.ampmangu.degrees.service;

import com.ampmangu.degrees.domain.Media;

import java.util.List;
import java.util.Optional;

public interface MediaService {
    Media save(Media media);

    List<Media> findAll();

    Optional<Media> findOne(Long id);

    void delete(Long id);

    Optional<Media> findByName(String name);

    boolean existsByRemoteId(Integer remoteId);

    Optional<Media> findByRemoteId(Integer remoteId);
}
