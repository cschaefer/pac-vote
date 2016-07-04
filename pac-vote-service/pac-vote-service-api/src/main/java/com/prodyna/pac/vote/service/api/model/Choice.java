package com.prodyna.pac.vote.service.api.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * A choice belongs to a {@link Ballot} and can be chosen by any
 * authenticated {@link User} at most once for a given {@link Ballot}.
 * 
 * @author cschaefer
 *
 */
@Entity(name = "Choice")
@Table(name = "choice")
@NamedQueries({
    @NamedQuery(name = "choice.all", query = "select c from Choice c")
})
@JsonAutoDetect
public class Choice implements Serializable {
	
	private static final long serialVersionUID = 8740154066207943991L;
	
	private Integer choiceId;
	private String name;
	private String description;
	
	@Column(name="BALLOT_ID")
	private Integer ballotId;

	@Id
	@Column(name = "CHOICE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
	public Integer getChoiceId() {
		return this.choiceId;
	}
	
	public void setChoiceId(Integer choiceId) {
		this.choiceId = choiceId;
	}
	
	@Column(name = "NAME", nullable = false, length = 160)
    @JsonProperty
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "DESCRIPTION", nullable = true, length = 160)
    @JsonProperty
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

    @JsonProperty
    public Integer getBallotId() {
        return ballotId;
    }

    public void setBallotId(Integer ballotId) {
        this.ballotId = ballotId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((choiceId == null) ? 0 : choiceId.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Choice other = (Choice) obj;
		if (choiceId == null) {
			if (other.choiceId != null)
				return false;
		} else if (!choiceId.equals(other.choiceId))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Choice [choiceId=");
		builder.append(choiceId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}


	
}
