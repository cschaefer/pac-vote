package com.prodyna.pac.vote.service.api.model;

/**
 * A query result detailing the number of votes for this 
 * choice.
 * 
 * @author cschaefer
 *
 */
public class ChoiceCount {
    
    private Integer choiceId;
    
    private Long count;

    public ChoiceCount() {
        
    }
    
    public ChoiceCount(Integer choiceId, Long count) {
        this.choiceId = choiceId;
        this.count = count;
    }
    
    public Integer getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Integer choiceId) {
        this.choiceId = choiceId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
    
    public void incrementCount() {
        this.count++;
    }

    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((choiceId == null) ? 0 : choiceId.hashCode());
        result = prime * result + ((count == null) ? 0 : count.hashCode());
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
        ChoiceCount other = (ChoiceCount) obj;
        if (choiceId == null) {
            if (other.choiceId != null) {
                return false;
            }
        } else if (!choiceId.equals(other.choiceId)) {
            return false;
        }
        if (count == null) {
            if (other.count != null) {
                return false;
            }
        } else if (!count.equals(other.count)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChoiceCount [choiceId=");
        builder.append(choiceId);
        builder.append(", count=");
        builder.append(count);
        builder.append("]");
        return builder.toString();
    }

}
