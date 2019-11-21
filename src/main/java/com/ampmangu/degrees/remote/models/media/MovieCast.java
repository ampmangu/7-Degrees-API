package com.ampmangu.degrees.remote.models.media;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieCast {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cast")
    @Expose
    private List<TvInfo.Cast> cast = null;
    @SerializedName("crew")
    @Expose
    private List<TvInfo.Crew> crew = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TvInfo.Cast> getCast() {
        return cast;
    }

    public void setCast(List<TvInfo.Cast> cast) {
        this.cast = cast;
    }

    public List<TvInfo.Crew> getCrew() {
        return crew;
    }

    public void setCrew(List<TvInfo.Crew> crew) {
        this.crew = crew;
    }

}