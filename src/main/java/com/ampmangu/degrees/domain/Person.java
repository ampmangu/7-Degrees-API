package com.ampmangu.degrees.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "person")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "date_added", nullable = false)
    private Instant dateAdded = Instant.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypePerson type = TypePerson.GENERAL;

    @Column(name = "remote_db_id")
    private Integer remoteDbId;

    @OneToMany(mappedBy = "leftSidePerson", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnoreProperties("leftSidePerson")
    private Set<PersonRelation> relations;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnoreProperties("person")
    private List<ActorData> actorDataList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Instant dateAdded) {
        this.dateAdded = dateAdded;
    }

    public TypePerson getType() {
        return type;
    }

    public void setType(TypePerson type) {
        this.type = type;
    }

    public Set<PersonRelation> getRelations() {
        return relations;
    }

    public void setRelations(Set<PersonRelation> relations) {
        this.relations = relations;
    }

    public List<ActorData> getActorDataList() {
        return actorDataList;
    }

    public void setActorDataList(List<ActorData> actorDataList) {
        this.actorDataList = actorDataList;
    }

    public Integer getRemoteDbId() {
        return remoteDbId;
    }

    public void setRemoteDbId(Integer remoteDbId) {
        this.remoteDbId = remoteDbId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (!getId().equals(person.getId())) return false;
        if (!getName().equals(person.getName())) return false;
        if (!getDateAdded().equals(person.getDateAdded())) return false;
        if (getType() != person.getType()) return false;
        return getRemoteDbId().equals(person.getRemoteDbId());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getDateAdded().hashCode();
        result = 31 * result + getType().hashCode();
        result = 31 * result + getRemoteDbId().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", dateAdded=" + dateAdded +
                ", type=" + type +
                '}';
    }
}
