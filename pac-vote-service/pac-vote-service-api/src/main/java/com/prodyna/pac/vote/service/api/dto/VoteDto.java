package com.prodyna.pac.vote.service.api.dto;

/**
 * Vote data transfer object detailing on the combination of a ballot
 * and the choice.
 * 
 * @author cschaefer
 *
 */
public class VoteDto {

    private Integer ballotId;
    
    private Integer choiceId;

    public Integer getBallotId() {
        return ballotId;
    }

    public void setBallotId(Integer ballotId) {
        this.ballotId = ballotId;
    }

    public Integer getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Integer choiceId) {
        this.choiceId = choiceId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ballotId == null) ? 0 : ballotId.hashCode());
        result = prime * result + ((choiceId == null) ? 0 : choiceId.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        VoteDto other = (VoteDto) obj;
        if (ballotId == null) {
            if (other.ballotId != null) {
                return false;
            }
        } else if (!ballotId.equals(other.ballotId)) {
            return false;
        }
        if (choiceId == null) {
            if (other.choiceId != null) {
                return false;
            }
        } else if (!choiceId.equals(other.choiceId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VoteDto [ballotId=");
        builder.append(ballotId);
        builder.append(", choiceId=");
        builder.append(choiceId);
        builder.append("]");
        return builder.toString();
    }
    
}
