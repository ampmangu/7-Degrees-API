package com.ampmangu.degrees.utils;

import com.ampmangu.degrees.domain.ActorData;
import com.ampmangu.degrees.domain.Person;

import java.util.*;
import java.util.stream.Collectors;

public class DegreeResponseBuilder {
    public boolean ended;
    public List<Person> personList;
    public List<ActorData> actorDataList;
    public List<Person> filter;

    public DegreeResponseBuilder() {
        this.ended = false;
        this.personList = new ArrayList<>();
        this.actorDataList = new ArrayList<>();
        this.filter = new ArrayList<>();
    }

    public DegreeResponseBuilder(boolean ended, List<Person> personList, List<ActorData> actorDataList) {
        this.ended = ended;
        this.personList = personList;
        this.actorDataList = actorDataList;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }

    public List<ActorData> getActorDataList() {
        return actorDataList;
    }

    public void setActorDataList(List<ActorData> actorDataList) {
        this.actorDataList = actorDataList;
    }

    public void addActorData(ActorData actorData) {
        this.actorDataList.add(actorData);
    }

    public void addPerson(Person person) {
        this.personList.add(person);
    }

    public List<Person> getFilter() {
        return filter;
    }

    public void setFilter(List<Person> filter) {
        this.filter = filter;
    }

    public void addFilter(Person person) {
        this.filter.add(person);
    }

    public List<Long> getFilters() {
        return this.filter.stream().map(Person::getId).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DegreeResponseBuilder that = (DegreeResponseBuilder) o;

        if (isEnded() != that.isEnded()) return false;
        if (getPersonList() != null ? !getPersonList().equals(that.getPersonList()) : that.getPersonList() != null)
            return false;
        return getActorDataList() != null ? getActorDataList().equals(that.getActorDataList()) : that.getActorDataList() == null;
    }

    @Override
    public int hashCode() {
        int result = (isEnded() ? 1 : 0);
        result = 31 * result + (getPersonList() != null ? getPersonList().hashCode() : 0);
        result = 31 * result + (getActorDataList() != null ? getActorDataList().hashCode() : 0);
        return result;
    }

    public Map<Integer, List<DegreeResponse>> buildResponse() {
        Map<Integer, List<DegreeResponse>> rtn = new HashMap<>();
        Set<DegreeResponse> degreeResponseSet = new LinkedHashSet<>();
        for (int index = this.personList.size() - 1; index >= 0; index--) {
            Person dataPerson = new Person();
            ActorData data = this.actorDataList.get(index);
            dataPerson.setId(data.getId());
            dataPerson.setName(data.getTitle());
            DegreeResponse degreeResponse = new DegreeResponse(personList.get(index), dataPerson, null);
            degreeResponseSet.add(degreeResponse);
        }
        rtn.put(degreeResponseSet.size(), new ArrayList<>(degreeResponseSet));
        return rtn;
    }
}
