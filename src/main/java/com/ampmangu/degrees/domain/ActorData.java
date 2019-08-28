package com.ampmangu.degrees.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A ActorData.
 */
@Entity
@Table(name = "actor_data")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ActorData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "remote_db_id")
    private Integer remoteDbId;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "pic_url", nullable = true)
    private String picUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @JsonIgnoreProperties
    private Person person;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRemoteDbId() {
        return remoteDbId;
    }

    public ActorData remoteDbId(Integer remoteDbId) {
        this.remoteDbId = remoteDbId;
        return this;
    }

    public void setRemoteDbId(Integer remoteDbId) {
        this.remoteDbId = remoteDbId;
    }

    public String getTitle() {
        return title;
    }

    public ActorData title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Person getPerson() {
        return person;
    }

    public ActorData person(Person person) {
        this.person = person;
        return this;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    public void setActorPicUrl(String picUrl) {
            this.picUrl = "https://image.tmdb.org/t/p/w45" + picUrl;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActorData actorData = (ActorData) o;

        if (!getId().equals(actorData.getId())) return false;
        if (!getRemoteDbId().equals(actorData.getRemoteDbId())) return false;
        if (!getTitle().equals(actorData.getTitle())) return false;
        return getPerson().equals(actorData.getPerson());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getRemoteDbId().hashCode();
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getPerson().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ActorData{" +
                "id=" + getId() +
                ", remoteDbId=" + getRemoteDbId() +
                ", title='" + getTitle() + "'" +
                "}";
    }
}
