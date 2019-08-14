package com.ampmangu.degrees.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class PersonRelation implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private PersonRelationId id;

    @Column(name = "stillValid")
    private Boolean stillValid = true;

    @ManyToOne
    @JoinColumn(name = "leftSideId", insertable = false, updatable = false)
    private Person leftSidePerson;

    @ManyToOne
    @JoinColumn(name = "rightSideId", insertable = false, updatable = false)
    private Person rightSidePerson;

    public PersonRelationId getId() {
        return id;
    }

    public void setId(PersonRelationId id) {
        this.id = id;
    }

    public Boolean getStillValid() {
        return stillValid;
    }

    public void setStillValid(Boolean stillValid) {
        this.stillValid = stillValid;
    }

    public Person getLeftSidePerson() {
        return leftSidePerson;
    }

    public void setLeftSidePerson(Person leftSidePerson) {
        this.leftSidePerson = leftSidePerson;
    }

    public Person getRightSidePerson() {
        return rightSidePerson;
    }

    public void setRightSidePerson(Person rightSidePerson) {
        this.rightSidePerson = rightSidePerson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonRelation relation = (PersonRelation) o;

        if (!getId().equals(relation.getId())) return false;
        if (!getStillValid().equals(relation.getStillValid())) return false;
        if (!getLeftSidePerson().equals(relation.getLeftSidePerson())) return false;
        return getRightSidePerson().equals(relation.getRightSidePerson());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getStillValid().hashCode();
        result = 31 * result + getLeftSidePerson().hashCode();
        result = 31 * result + getRightSidePerson().hashCode();
        return result;
    }

    @Embeddable
    public static class PersonRelationId implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "leftSideId", nullable = false, updatable = false)
        private Integer leftSideId;

        @Column(name = "rightSideId", nullable = false, updatable = false)
        private Integer rightSideId;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PersonRelationId)) {
                return false;
            }
            PersonRelationId other = (PersonRelationId) obj;
            if (!(other.getLeftSideId()).equals(getLeftSideId())) {
                return false;
            }
            if (!(other.getRightSideId()).equals(getRightSideId())) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(leftSideId, rightSideId);
        }

        public Integer getLeftSideId() {
            return leftSideId;
        }

        public void setLeftSideId(Integer leftSideId) {
            this.leftSideId = leftSideId;
        }

        public Integer getRightSideId() {
            return rightSideId;
        }

        public void setRightSideId(Integer rightSideId) {
            this.rightSideId = rightSideId;
        }
    }
}
