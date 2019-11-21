package com.ampmangu.degrees.remote;

import com.ampmangu.degrees.remote.models.*;
import com.ampmangu.degrees.remote.models.media.MovieCast;
import com.ampmangu.degrees.remote.models.media.MovieList;
import com.ampmangu.degrees.remote.models.media.TvInfo;
import com.ampmangu.degrees.remote.models.media.TvList;
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

    @GET("person/{person_id}")
    Observable<BasicPerson> getActorBasicInfo(@Path("person_id") Integer id);

    @GET("search/movie")
    Observable<MovieList> getMovieList(@Query("query") String name);

    @GET("movie/{movie_id}/credits")
    Observable<MovieCast> getMovieCast(@Path("movie_id") Integer id);

    @GET("search/tv")
    Observable<TvList> getTvList(@Query("query") String name);

    @GET("tv/{tv_id}/credits")
    Observable<TvInfo> getTvCast(@Path("tv_id") Integer id);

}
