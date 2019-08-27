package com.ampmangu.degrees.utils;

import com.ampmangu.degrees.domain.Person;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class DegreeResponse {
    public DegreeResponsePerson from;
    public DegreeResponsePerson to;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer degrees;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<DegreeResponse> people;

    public DegreeResponse(Person from, Person to, Integer degrees) {
        this.from = new DegreeResponsePerson(from.getId(), from.getName());
        this.to = new DegreeResponsePerson(to.getId(), to.getName());
        this.degrees = degrees;
        this.people = null;
    }

    public DegreeResponse(Person from, Person to, Integer degrees, List<DegreeResponse> people) {
        this(from, to, degrees);
        this.people = people;
    }

    public DegreeResponsePerson getFrom() {
        return from;
    }

    public void setFrom(DegreeResponsePerson from) {
        this.from = from;
    }

    public DegreeResponsePerson getTo() {
        return to;
    }

    public void setTo(DegreeResponsePerson to) {
        this.to = to;
    }

    public Integer getDegrees() {
        return degrees;
    }

    public void setDegrees(Integer degrees) {
        this.degrees = degrees;
    }

    public List<DegreeResponse> getPeople() {
        return people;
    }

    public void setPeople(List<DegreeResponse> people) {
        this.people = people;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DegreeResponse that = (DegreeResponse) o;

        if (!getFrom().equals(that.getFrom())) return false;
        if (!getTo().equals(that.getTo())) return false;
        if (getDegrees() != null ? !getDegrees().equals(that.getDegrees()) : that.getDegrees() != null) return false;
        return getPeople() != null ? getPeople().equals(that.getPeople()) : that.getPeople() == null;
    }

    @Override
    public int hashCode() {
        int result = getFrom().hashCode();
        result = 31 * result + getTo().hashCode();
        result = 31 * result + (getDegrees() != null ? getDegrees().hashCode() : 0);
        result = 31 * result + (getPeople() != null ? getPeople().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DegreeResponse{" +
                "from=" + from +
                ", to=" + to +
                ", degrees=" + degrees +
                '}';
    }
}
