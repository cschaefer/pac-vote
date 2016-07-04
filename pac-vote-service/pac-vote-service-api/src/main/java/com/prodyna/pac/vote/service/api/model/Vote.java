package com.prodyna.pac.vote.service.api.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * A vote domain object which represents a {@link User}'s {@link Choice} for a
 * given {@link Ballot}.  A user may only vote once per ballot.
 *
 * @author cschaefer
 *
 */
@Entity(name = "Vote")
@Table(name = "vote",
uniqueConstraints = @UniqueConstraint(columnNames = {"USER_ID", "BALLOT_ID"}))
@NamedQueries({
    @NamedQuery(name = "vote.all", query = "SELECT v FROM Vote v"),
    @NamedQuery(name = "vote.userVoted", query = "SELECT v FROM Vote v WHERE v.userId = :userId AND v.ballotId = :ballotId"),
    @NamedQuery(name = "vote.ballotVotes", query = "SELECT v FROM Vote v WHERE v.ballotId = :ballotId" ),
    @NamedQuery(name = "vote.myBallotVotes", query = "SELECT v FROM Vote v WHERE v.userId = :userId"),
    @NamedQuery(name = "vote.ballotResult", query = "SELECT NEW com.prodyna.pac.vote.service.api.model.ChoiceCount(v.choiceId, COUNT(DISTINCT v.choiceId)) FROM Vote v WHERE v.ballotId = :ballotId GROUP BY v.choiceId")
})
@JsonAutoDetect
public class Vote implements Serializable {

    private static final long serialVersionUID = 5042922514880501677L;

    private Integer voteId;
    private Integer userId;
    private Integer ballotId;
    private Integer choiceId;
    private LocalDate voteDate;

    @Id
    @Column(name = "VOTE_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    public Integer getVoteId() {
        return this.voteId;
    }

    public void setVoteId(Integer voteId) {
        this.voteId = voteId;
    }

    @Column(name = "USER_ID")
    @JsonProperty
    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "BALLOT_ID")
    @JsonProperty
    public Integer getBallotId() {
        return this.ballotId;
    }

    public void setBallotId(Integer ballotId) {
        this.ballotId = ballotId;
    }

    @Column(name = "CHOICE_ID")
    @JsonProperty
    public Integer getChoiceId() {
        return this.choiceId;
    }

    public void setChoiceId(Integer choiceId) {
        this.choiceId = choiceId;
    }

    @Column(nullable = false)
    @NotNull
    @JsonProperty
    public LocalDate getVoteDate() {
        return this.voteDate;
    }

    public void setVoteDate(LocalDate voteDate) {
        this.voteDate = voteDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.ballotId == null) ? 0 : this.ballotId.hashCode());
        result = prime * result + ((this.choiceId == null) ? 0 : this.choiceId.hashCode());
        result = prime * result + ((this.userId == null) ? 0 : this.userId.hashCode());
        result = prime * result + ((this.voteDate == null) ? 0 : this.voteDate.hashCode());
        result = prime * result + ((this.voteId == null) ? 0 : this.voteId.hashCode());
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
        final Vote other = (Vote) obj;
        if (this.ballotId == null) {
            if (other.ballotId != null) {
                return false;
            }
        } else if (!this.ballotId.equals(other.ballotId)) {
            return false;
        }
        if (this.choiceId == null) {
            if (other.choiceId != null) {
                return false;
            }
        } else if (!this.choiceId.equals(other.choiceId)) {
            return false;
        }
        if (this.userId == null) {
            if (other.userId != null) {
                return false;
            }
        } else if (!this.userId.equals(other.userId)) {
            return false;
        }
        if (this.voteDate == null) {
            if (other.voteDate != null) {
                return false;
            }
        } else if (!this.voteDate.equals(other.voteDate)) {
            return false;
        }
        if (this.voteId == null) {
            if (other.voteId != null) {
                return false;
            }
        } else if (!this.voteId.equals(other.voteId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Vote [voteId=");
        builder.append(this.voteId);
        builder.append(", userId=");
        builder.append(this.userId);
        builder.append(", ballotId=");
        builder.append(this.ballotId);
        builder.append(", choiceId=");
        builder.append(this.choiceId);
        builder.append(", voteDate=");
        builder.append(this.voteDate);
        builder.append("]");
        return builder.toString();
    }


}
