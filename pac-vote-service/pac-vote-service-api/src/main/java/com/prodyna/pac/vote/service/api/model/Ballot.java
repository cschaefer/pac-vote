package com.prodyna.pac.vote.service.api.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Ballot domain object which details the question to be polled.
 * A ballot has a set of {@link Choice}s and an {@link User} who is
 * the owner of this ballot.
 *
 * @author cschaefer
 *
 */
@Entity(name = "Ballot")
@Table(name = "ballot")
@NamedQueries({
    @NamedQuery(name = "ballot.all", query = "select b from Ballot b")
})
@JsonAutoDetect
public class Ballot implements Serializable {

    private static final long serialVersionUID = -6600191032261549322L;

    private Integer ballotId;
    private String question;
    private String description;
    private LocalDate lastVoteDate;
    private Boolean active;
    private User owner;

    @Transient private boolean edit;
    @Transient private boolean voted;


    private Set<Choice> choices = new HashSet<>();

    public Ballot() {
        // empty
    }

    public Ballot(String question, Set<Choice> choices) {
        this.question = question;
        this.choices = choices;
    }

    @Id
    @Column(name = "BALLOT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    public Integer getBallotId() {
        return this.ballotId;
    }

    public void setBallotId(Integer ballotId) {
        this.ballotId = ballotId;
    }

    @Column(name = "QUESTION", nullable = false, length = 160)
    @NotNull
    @JsonProperty
    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Column(name = "DESCRIPTION", nullable = true, length = 160)
    @JsonProperty
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(nullable = true)
    @JsonProperty
    public LocalDate getLastVoteDate() {
        return this.lastVoteDate;
    }

    public void setLastVoteDate(LocalDate lastVoteDate) {
        this.lastVoteDate = lastVoteDate;
    }

    @Column(nullable = false)
    @NotNull
    @JsonProperty
    public Boolean isActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    @JsonProperty
    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name= "BALLOT_ID", referencedColumnName = "BALLOT_ID")
    @JsonProperty
    public Set<Choice> getChoices() {
        return this.choices;
    }

    public void setChoices(Set<Choice> choices) {
        this.choices = choices;
    }

    @Transient
    public boolean isEdit() {
        return this.edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    @Transient
    public boolean isVoted() {
        return this.voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.active == null) ? 0 : this.active.hashCode());
        result = prime * result + ((this.ballotId == null) ? 0 : this.ballotId.hashCode());
        result = prime * result + ((this.choices == null) ? 0 : this.choices.hashCode());
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        result = prime * result + ((this.lastVoteDate == null) ? 0 : this.lastVoteDate.hashCode());
        result = prime * result + ((this.owner == null) ? 0 : this.owner.hashCode());
        result = prime * result + ((this.question == null) ? 0 : this.question.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Ballot other = (Ballot) obj;
        if (this.active == null) {
            if (other.active != null) {
                return false;
            }
        } else if (!this.active.equals(other.active)) {
            return false;
        }
        if (this.ballotId == null) {
            if (other.ballotId != null) {
                return false;
            }
        } else if (!this.ballotId.equals(other.ballotId)) {
            return false;
        }
        if (this.choices == null) {
            if (other.choices != null) {
                return false;
            }
        } else if (!this.choices.equals(other.choices)) {
            return false;
        }
        if (this.description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!this.description.equals(other.description)) {
            return false;
        }
        if (this.lastVoteDate == null) {
            if (other.lastVoteDate != null) {
                return false;
            }
        } else if (!this.lastVoteDate.equals(other.lastVoteDate)) {
            return false;
        }
        if (this.owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!this.owner.equals(other.owner)) {
            return false;
        }
        if (this.question == null) {
            if (other.question != null) {
                return false;
            }
        } else if (!this.question.equals(other.question)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Ballot [ballotId=");
        builder.append(this.ballotId);
        builder.append(", question=");
        builder.append(this.question);
        builder.append(", description=");
        builder.append(this.description);
        builder.append(", lastVoteDate=");
        builder.append(this.lastVoteDate);
        builder.append(", active=");
        builder.append(this.active);
        builder.append(", owner=");
        builder.append(this.owner);
        builder.append(", choices=");
        builder.append(this.choices);
        builder.append("]");
        return builder.toString();
    }




}
