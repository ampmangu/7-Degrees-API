package com.ampmangu.degrees.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "media")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Media implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "remote_db_id")
    private Integer remoteDbId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeMedia type = TypeMedia.MOVIE;

    @NotNull
    @Column(name = "date_added", nullable = false)
    private Instant dateAdded = Instant.now();

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;
    //add the mapped by
    @ManyToMany (mappedBy = "mediaIn")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Person> personsIn = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRemoteDbId() {
        return remoteDbId;
    }

    public void setRemoteDbId(Integer remoteDbId) {
        this.remoteDbId = remoteDbId;
    }

    public TypeMedia getType() {
        return type;
    }

    public void setType(TypeMedia type) {
        this.type = type;
    }

    public Instant getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Instant dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Person> getPersonsIn() {
        return personsIn;
    }

    public void setPersonsIn(Set<Person> personsIn) {
        this.personsIn = personsIn;
    }
}
