package com.ampmangu.degrees.remote;

import com.ampmangu.degrees.domain.*;
import com.ampmangu.degrees.remote.models.Cast;
import com.ampmangu.degrees.remote.models.PeopleDetail;
import com.ampmangu.degrees.remote.models.PeopleDetailTv;
import com.ampmangu.degrees.remote.models.media.MovieCast;
import com.ampmangu.degrees.remote.models.media.TvInfo;
import com.ampmangu.degrees.service.ActorDataService;
import com.ampmangu.degrees.service.MediaService;
import com.ampmangu.degrees.service.PersonService;
import io.reactivex.Observable;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public class MovieDBUtils {
    private MovieDBUtils() {

    }

    public static PeopleDetail processPersonRequest(Integer id, MovieDBService movieDBService) {
        final PeopleDetail[] result = {new PeopleDetail()};
        Observable<PeopleDetail> idObs = movieDBService.getActorDetail(id);
        Observable<PeopleDetailTv> actorDetailObs = movieDBService.getActorTvDetail(id);
        Observable.zip(idObs, actorDetailObs, MovieDBUtils::joinCredits).subscribe(act -> result[0] = act);
        return result[0];
    }

    private static PeopleDetail joinCredits(PeopleDetail credits1, PeopleDetailTv credits2) {
        credits1.addCast(credits2.getCast());
        return credits1;
    }

    public static Person savePerson(PeopleDetail person, String name, String profilePath, PersonService personService, ActorDataService actorDataService) {
        if (personService.existsByRemoteId(person.getId())) {
            return updateActor(person, personService, actorDataService);
        }
        Person personToSave = new Person();
        personToSave.setName(name);
        personToSave.setType(TypePerson.MOVIES);
        personToSave.setDateAdded(Instant.now());
        personToSave.setRemoteDbId(person.getId());
        personToSave.setPicUrl(profilePath);
        Person savedPerson = personService.save(personToSave);
        List<ActorData> actorDataList = new ArrayList<>();
        for (Cast cast : person.getCast()) {
            addActorData(actorDataService, savedPerson, actorDataList, cast);
        }
        savedPerson.setActorDataList(actorDataList);
        return personService.save(savedPerson);
    }

    public static Media saveMovie(MovieCast movieCast, String name, MediaService mediaService, PersonService personService) {
        if (mediaService.existsByRemoteId(movieCast.getId())) {
            return updateMovie(movieCast, mediaService, personService);
        }
        Media media = new Media();
        media.setName(name);
        media.setType(TypeMedia.MOVIE);
        media.setDateAdded(Instant.now());
        media.setRemoteDbId(movieCast.getId());
        Media savedMedia = mediaService.save(media);
        Set<Person> personSet = new HashSet<>();
        for (TvInfo.Cast cast : movieCast.getCast()) {
            addMoviePerson(cast, personSet, personService);
        }
        savedMedia.setPersonsIn(personSet);
        personSet.stream().filter(Objects::nonNull).collect(Collectors.toSet()).forEach(person -> {
            person.addMediaIn(savedMedia);
            personService.save(person);
        });
        return mediaService.save(savedMedia);
    }

    private static void addMoviePerson(TvInfo.Cast cast, Set<Person> personSet, PersonService personService) {
        Person person = new Person();
        person.setRemoteDbId(cast.getId());
        person.setName(cast.getName());
        person.setDateAdded(Instant.now());
        person.setType(TypePerson.MOVIES);
        if (cast.getProfilePath() instanceof String) {
            person.setPicUrl((String) cast.getProfilePath());
        }
        personSet.add(personService.save(person));
    }

    private static Media updateMovie(MovieCast movieCast, MediaService mediaService, PersonService personService) {
        Optional<Media> optionalMedia = mediaService.findByRemoteId(movieCast.getId());
        if (optionalMedia.isPresent()) {
            Media media = optionalMedia.get();
            Set<Person> personSet = new HashSet<>();
            for (TvInfo.Cast cast: movieCast.getCast()) {
                if (!personService.findByRemoteId(cast.getId()).isPresent()) {
                    addMoviePerson(cast, personSet, personService);
                }
            }
            return media;
        } else {
            return new Media();
        }
    }
    private static Person updateActor(PeopleDetail person, PersonService personService, ActorDataService actorDataService) {
        Optional<Person> optionalPerson = personService.findByRemoteId(person.getId());
        if (optionalPerson.isPresent()) {
            Person actor = optionalPerson.get();
            List<ActorData> actorDataList = new ArrayList<>();
            for (Cast cast : person.getCast()) {
                if (!actorDataService.findByPersonAndRemoteId(actor.getId(), cast.getId()).isPresent()) {
                    addActorData(actorDataService, actor, actorDataList, cast);
                }
            }
            return actor;
        } else {
            return new Person();
        }
    }

    private static void addActorData(ActorDataService actorDataService, Person actor, List<ActorData> actorDataList, Cast cast) {
        ActorData actorData = new ActorData();
        if (cast.getOriginalTitle() != null) {
            actorData.setTitle(cast.getOriginalTitle());
        } else if (cast.getTitle() != null) {
            actorData.setTitle(cast.getTitle());
        } else {
            return;
        }
        actorData.setPerson(actor);
        actorData.setRemoteDbId(cast.getId());
        if (actor.getType() == TypePerson.MOVIES) {
            actorData.setActorPicUrl(cast.getPosterPath());
        }
        actorDataList.add(actorDataService.save(actorData));
    }
}
