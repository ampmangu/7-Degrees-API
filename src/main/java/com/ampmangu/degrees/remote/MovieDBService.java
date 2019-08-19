package com.ampmangu.degrees.remote;

import com.ampmangu.degrees.remote.models.PeopleDetail;
import com.ampmangu.degrees.remote.models.PeopleDetailTv;
import com.ampmangu.degrees.remote.models.PeopleResults;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDBService {
    @GET("search/person")
    Observable<PeopleResults> getActorList(@Query("query") String name);

    @GET("person/{person_id}/movie_credits")
    Observable<PeopleDetail> getActorDetail(@Path("person_id") Integer id);

    @GET("person/{person_id}/tv_credits")
    Observable<PeopleDetailTv> getActorTvDetail(@Path("person_id") Integer id);

}
