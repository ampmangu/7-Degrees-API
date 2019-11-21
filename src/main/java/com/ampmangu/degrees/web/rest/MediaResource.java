package com.ampmangu.degrees.web.rest;

import com.ampmangu.degrees.domain.Media;
import com.ampmangu.degrees.domain.Person;
import com.ampmangu.degrees.remote.MovieDBService;
import com.ampmangu.degrees.remote.models.media.*;
import com.ampmangu.degrees.service.ActorDataService;
import com.ampmangu.degrees.service.MediaService;
import com.ampmangu.degrees.service.PersonRelationService;
import com.ampmangu.degrees.service.PersonService;
import com.ampmangu.degrees.utils.DegreeResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ampmangu.degrees.remote.MovieDBUtils.saveMovie;
import static com.ampmangu.degrees.remote.MovieDBUtils.saveTv;
import static com.ampmangu.degrees.security.SecurityUtils.checkToken;
import static org.apache.commons.lang3.StringUtils.stripAccents;

@RestController
@RequestMapping("/api/media")
public class MediaResource {
    private final Logger log = LoggerFactory.getLogger(MediaResource.class);

    private final PersonService personService;

    private final ActorDataService actorDataService;

    private final PersonRelationService personRelationService;

    private final MovieDBService movieDBService;

    private final MediaService mediaService;
    private final Jedis jedisClient;

    private final ObjectMapper mapper = new ObjectMapper();

    MediaResource(PersonService personService, MovieDBService dbService, ActorDataService actorDataService, Jedis jedisClient, JavaTimeModule javaTimeModule, PersonRelationService personRelationService, MediaService mediaService) {
        this.personService = personService;
        this.movieDBService = dbService;
        this.actorDataService = actorDataService;
        this.jedisClient = jedisClient;
        this.mediaService = mediaService;
        this.personRelationService = personRelationService;
        mapper.findAndRegisterModules();
        mapper.registerModule(javaTimeModule);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @GetMapping("/{from}/{to}")
    public ResponseEntity<DegreeResponse> getDegreeMovie(@PathVariable String from, @PathVariable String to, @RequestHeader("auth-token") String token) throws InvalidKeyException {
        checkToken(token);
        log.debug("Got request from {} to {}", from, to);
        getMedia(from, token);
        throw new UnsupportedOperationException("Not implemented");

    }

    @GetMapping("/{name}")
    private ResponseEntity<Media> getMedia(@PathVariable String name, @RequestHeader("auth-token") String token) throws InvalidKeyException {
        checkToken(token);
        String mediaCached = jedisClient.get(name.toUpperCase());
        if (mediaCached != null && mediaCached.isEmpty()) {
            Media media;
            try {
                media = mapper.readValue(mediaCached, Media.class);
            } catch (IOException e) {
                log.error("Couldn't find media {}, going to provider", name);
                media = getMediaFromProvider(name);
            }
            return ResponseEntity.ok().body(media);

        } else {
            Media media = getMediaFromProvider(name);
            return ResponseEntity.ok().body(media);
        }
    }
    private void setAndExpire(Media media, String key) throws JsonProcessingException {
        jedisClient.set(key, mapper.writeValueAsString(media));
        jedisClient.expire(key, 3600);
    }

    private Media getMediaFromProvider(String name) {
        Optional<Media> optionalMedia = mediaService.findByName(name);
        if (!optionalMedia.isPresent()) {
            final List<MovieResult> [] movieListResult = new List[]{null};
            Observable<MovieList> movieListObservable = movieDBService.getMovieList(name);
            movieListObservable.subscribe(movieList -> movieListResult[0] = movieList.getResults().stream().filter(movieResult -> movieResult.getPopularity() != null).collect(Collectors.toList()));
            MovieResult result = decideMovieResult(movieListResult[0], name);
            Observable<TvList> tvListObservable = movieDBService.getTvList(name);
            final TvResult[] tvInfoResult = {new TvResult()};
            tvListObservable.subscribe(tvList -> tvList.getResults().stream().filter(tvResult -> tvResult.getPopularity() != null).max(Comparator.comparing(TvResult::getPopularity)).ifPresent(tvResult -> tvInfoResult[0] = tvResult));
            double moviePopularity = 0.0d;
            double tvPopularity = 0.0d;
            if (result != null && result.getPopularity() != null) {
                moviePopularity = result.getPopularity();
            }
            if (tvInfoResult[0] != null && tvInfoResult[0].getPopularity() != null) {
                tvPopularity = tvInfoResult[0].getPopularity();
            }
            if (moviePopularity > tvPopularity && result != null) {
                return getMovieMedia(name, result);
            } else if (tvInfoResult[0] != null){
                return getTvMedia(name, tvInfoResult[0]);
            }
        }
        return optionalMedia.get();
    }

    private Media getTvMedia(String name, TvResult result) {
        Observable<TvInfo> tvCastObservable = movieDBService.getTvCast(result.getId());
        final TvInfo[] castResult = {new TvInfo()};
        tvCastObservable.subscribe(tvInfo -> castResult[0] = tvInfo);
        TvInfo tvResult = castResult[0];
        String mediaName = result.getName();
        if (mediaName == null || mediaName.equalsIgnoreCase("")) {
            mediaName = result.getOriginalName();
        }
        Media media = saveTv(tvResult, mediaName, mediaService, personService);
        return saveCachedMedia(name, mediaName, media);
    }



    private Media getMovieMedia(String name, MovieResult result) {
        Observable<MovieCast> movieResultObservable = movieDBService.getMovieCast(result.getId());
        final MovieCast[] castResult = {new MovieCast()};
        movieResultObservable.subscribe(movieCast -> castResult[0] = movieCast);
        String mediaName = result.getTitle();
        if (mediaName == null || mediaName.equalsIgnoreCase("")) {
            mediaName = result.getOriginalTitle();
        }
        Media media = saveMovie(castResult[0], mediaName, mediaService, personService);
        return saveCachedMedia(name, mediaName, media);
    }

    private Media saveCachedMedia(String name, String mediaName, Media media) {
        try {
            String savedName = stripAccents(mediaName.toUpperCase());
            log.info("Saving in cache {} ", savedName);
            setAndExpire(media, savedName);
            setAndExpire(media, name.toUpperCase());
        }catch (JsonProcessingException ex) {
            log.warn("Couldn't save in redis user of id " + media.getId(), ex);
        }
        return media;
    }

    private MovieResult decideMovieResult(List<MovieResult> movieResults, String name) {
        Optional<MovieResult> optionalMax = movieResults.stream().max(Comparator.comparing(MovieResult::getPopularity));
        List<MovieResult> listSimilarNames = movieResults.stream().filter(movieResult -> movieResult.getTitle() != null && movieResult.getTitle().toUpperCase().contains(name.toUpperCase())).collect(Collectors.toList());
        Optional<MovieResult> optionalMaxByName = Optional.empty();
        if (!listSimilarNames.isEmpty()) {
            optionalMaxByName = listSimilarNames.stream().max(Comparator.comparing(MovieResult::getPopularity));
        }
        return optionalMaxByName.orElseGet(() -> optionalMax.orElseGet(MovieResult::new));
    }

}
