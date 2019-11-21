package com.ampmangu.degrees.service.impl;

import com.ampmangu.degrees.domain.Media;
import com.ampmangu.degrees.repository.MediaRepository;
import com.ampmangu.degrees.service.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Media}
 */
@Service
@Transactional
public class MediaServiceImpl implements MediaService {
    private final Logger log = LoggerFactory.getLogger(MediaServiceImpl.class);

    private final MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }
    @Override
    public Media save(Media media) {
        log.debug("Request to save media : {}", media);
        Media media1 = mediaRepository.save(media);
        return media1;
    }

    @Override
    public List<Media> findAll() {
        return mediaRepository.findAll();
    }

    @Override
    public Optional<Media> findOne(Long id) {
        return mediaRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        mediaRepository.deleteById(id);
    }

    @Override
    public Optional<Media> findByName(String name) {
        List<Media> mediaList = mediaRepository.findAll().stream().filter(media -> media.getName() != null && media.getName().toUpperCase().contains(name.toUpperCase())).collect(Collectors.toList());
        if (mediaList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mediaList.get(0));
    }

    @Override
    public boolean existsByRemoteId(Integer remoteId) {
        return mediaRepository.findAll().stream().anyMatch(media -> media.getRemoteDbId() != null && media.getRemoteDbId().equals(remoteId));
    }

    @Override
    public Optional<Media> findByRemoteId(Integer remoteId) {
        return Optional.of(mediaRepository.findAll().stream().filter(media -> media.getRemoteDbId() != null && media.getRemoteDbId().equals(remoteId)).collect(Collectors.toList()).get(0));
    }
}
