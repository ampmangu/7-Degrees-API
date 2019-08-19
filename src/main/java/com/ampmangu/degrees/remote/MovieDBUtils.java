package com.ampmangu.degrees.remote;

import com.ampmangu.degrees.remote.models.PeopleDetail;
import com.ampmangu.degrees.remote.models.PeopleDetailTv;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;


public class MovieDBUtils {
    public static PeopleDetail processPersonRequest(Integer id, MovieDBService movieDBService) {
        final PeopleDetail[] result = {new PeopleDetail()};
        Observable<PeopleDetail> idObs = movieDBService.getActorDetail(id);
//        idObs.subscribe(actorDetail -> result[0].addCast(actorDetail.getCast()));
        Observable<PeopleDetailTv> actorDetailObs = movieDBService.getActorTvDetail(id);
        Observable.zip(idObs, actorDetailObs, (credits1, credits2) -> joinCredits(credits1, credits2)).subscribe(act -> result[0] = act);
//        actorDetailObs.subscribe(actorDetail -> actorDetail.addCast(actorDetail.getCast()));
        return result[0];
    }

    private static PeopleDetail joinCredits(PeopleDetail credits1, PeopleDetailTv credits2) {
        credits1.addCast(credits2.getCast());
        return credits1;
    }
}
