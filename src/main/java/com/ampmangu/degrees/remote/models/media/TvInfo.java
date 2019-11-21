package com.ampmangu.degrees.remote.models.media;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TvInfo {
    @SerializedName("cast")
    @Expose
    private List<TvCast> cast = null;
    @SerializedName("crew")
    @Expose
    private List<TvCrew> crew = null;
    @SerializedName("id")
    @Expose
    private Integer id;

    public List<TvCast> getCast() {
        return cast;
    }

    public void setCast(List<TvCast> cast) {
        this.cast = cast;
    }

    public List<TvCrew> getCrew() {
        return crew;
    }

    public void setCrew(List<TvCrew> crew) {
        this.crew = crew;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static class Cast {

        @SerializedName("cast_id")
        @Expose
        private Integer castId;
        @SerializedName("character")
        @Expose
        private String character;
        @SerializedName("credit_id")
        @Expose
        private String creditId;
        @SerializedName("gender")
        @Expose
        private Integer gender;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("order")
        @Expose
        private Integer order;
        @SerializedName("profile_path")
        @Expose
        private Object profilePath;

        public Integer getCastId() {
            return castId;
        }

        public void setCastId(Integer castId) {
            this.castId = castId;
        }

        public String getCharacter() {
            return character;
        }

        public void setCharacter(String character) {
            this.character = character;
        }

        public String getCreditId() {
            return creditId;
        }

        public void setCreditId(String creditId) {
            this.creditId = creditId;
        }

        public Integer getGender() {
            return gender;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public Object getProfilePath() {
            return profilePath;
        }

        public void setProfilePath(Object profilePath) {
            this.profilePath = profilePath;
        }

    }

    public static class Crew {

        @SerializedName("credit_id")
        @Expose
        private String creditId;
        @SerializedName("department")
        @Expose
        private String department;
        @SerializedName("gender")
        @Expose
        private Integer gender;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("job")
        @Expose
        private String job;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("profile_path")
        @Expose
        private Object profilePath;

        public String getCreditId() {
            return creditId;
        }

        public void setCreditId(String creditId) {
            this.creditId = creditId;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public Integer getGender() {
            return gender;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getProfilePath() {
            return profilePath;
        }

        public void setProfilePath(Object profilePath) {
            this.profilePath = profilePath;
        }

    }
}
