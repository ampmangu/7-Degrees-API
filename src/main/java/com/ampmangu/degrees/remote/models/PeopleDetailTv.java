package com.ampmangu.degrees.remote.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PeopleDetailTv {

    @SerializedName("cast")
    @Expose
    private List<CastTv> cast = null;
    @SerializedName("crew")
    @Expose
    private List<Crew> crew = null;
    @SerializedName("id")
    @Expose
    private Integer id;

    public List<CastTv> getCast() {
        return cast;
    }

    public void setCast(List<CastTv> cast) {
        this.cast = cast;
    }

    public void addCast(List<CastTv> cast) {
        this.cast.addAll(cast);
    }

    public void addCrew(List<Crew> crew) {
        this.crew.addAll(crew);
    }

    public List<Crew> getCrew() {
        return crew;
    }

    public void setCrew(List<Crew> crew) {
        this.crew = crew;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}