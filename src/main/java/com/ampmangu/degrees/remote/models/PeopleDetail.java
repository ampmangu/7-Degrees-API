package com.ampmangu.degrees.remote.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PeopleDetail {

    @SerializedName("cast")
    @Expose
    private List<Cast> cast = null;
    @SerializedName("crew")
    @Expose
    private List<Crew> crew = null;
    @SerializedName("id")
    @Expose
    private Integer id;

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }

    public void addCast(List<CastTv> cast) {
        List<Cast> convertedCastList = new ArrayList<>();
        cast.forEach(c -> {
            Cast convertedCast = new Cast();
            convertedCast.setTitle(c.getName());
            convertedCast.setOriginalTitle(c.getOriginalName());
            convertedCast.setPosterPath(c.getPosterPath());
            convertedCast.setId(c.getId());
            convertedCastList.add(convertedCast);
        });
        this.cast.addAll(convertedCastList);
    }

    public List<Crew> getCrew() {
        return crew;
    }

    public void setCrew(List<Crew> crew) {
        this.crew = crew;
    }

    public void addCrew(List<Crew> crew) {
        this.crew.addAll(crew);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}