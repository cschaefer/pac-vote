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
 * The user domain object.  A user may only be created should they
 * possess an OAuth accessToken issued by a 3rd party OAuth provider
 * that shares the client id and secret of this application.
 *
 * The user has a userId for the application.  However, the uid
 * comes from the 3rd party OAuth provider.
 *
 * A user may be an administrator.  The first user created for the application
 * is automatically the super administrator.
 *
 * @author cschaefer
 *
 */
@Entity(name = "User")
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = "user.all", query = "SELECT u FROM User u"),
    @NamedQuery(name = "user.findByName", query = "SELECT u FROM User u WHERE u.userName = :userName"),
    @NamedQuery(name = "user.findByUid", query = "SELECT u FROM User u WHERE u.uid = :uid")
})
@JsonAutoDetect
public class User implements Serializable {

    private static final long serialVersionUID = 7299800843150040093L;

    private Integer userId;
    private String userName;
    private String uid;
    private String provider;
    private String profileImageUrl;
    private Boolean administrator;

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    public Integer getUserId() {
        return this.userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "USERNAME", nullable = true, unique= true, updatable = false, length = 160)
    @JsonProperty
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "UID", nullable = true, unique= true, updatable = false, length = 160)
    @JsonProperty
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Column(name = "PROVIDER", nullable = true, length = 160)
    @JsonProperty
    public String getProvider() {
        return this.provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Column(name = "PROFILEIMAGEURL", nullable = true, unique= true, length = 450)
    @JsonProperty
    public String getProfileImageUrl() {
        return this.profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Column(name = "ADMINISTRATOR", nullable = true)
    @JsonProperty
    public Boolean isAdministrator() {
        return this.administrator;
    }

    public void setAdministrator(Boolean administrator) {
        this.administrator = administrator;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.profileImageUrl == null) ? 0 : this.profileImageUrl.hashCode());
        result = prime * result + ((this.provider == null) ? 0 : this.provider.hashCode());
        result = prime * result + ((this.uid == null) ? 0 : this.uid.hashCode());
        result = prime * result + ((this.userId == null) ? 0 : this.userId.hashCode());
        result = prime * result + ((this.userName == null) ? 0 : this.userName.hashCode());
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
        final User other = (User) obj;
        if (this.profileImageUrl == null) {
            if (other.profileImageUrl != null) {
                return false;
            }
        } else if (!this.profileImageUrl.equals(other.profileImageUrl)) {
            return false;
        }
        if (this.provider == null) {
            if (other.provider != null) {
                return false;
            }
        } else if (!this.provider.equals(other.provider)) {
            return false;
        }
        if (this.uid == null) {
            if (other.uid != null) {
                return false;
            }
        } else if (!this.uid.equals(other.uid)) {
            return false;
        }
        if (this.userId == null) {
            if (other.userId != null) {
                return false;
            }
        } else if (!this.userId.equals(other.userId)) {
            return false;
        }
        if (this.userName == null) {
            if (other.userName != null) {
                return false;
            }
        } else if (!this.userName.equals(other.userName)) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [userId=");
        builder.append(this.userId);
        builder.append(", userName=");
        builder.append(this.userName);
        builder.append(", uid=");
        builder.append(this.uid);
        builder.append(", provider=");
        builder.append(this.provider);
        builder.append(", profileImageUrl=");
        builder.append(this.profileImageUrl);
        builder.append("]");
        return builder.toString();
    }



}
