package de.ep.team2.core.entities;

import de.ep.team2.core.dtos.RegistrationDto;

import java.util.Date;
import java.util.UUID;

public class ConfirmationToken {

    private int tokenId;
    private String confirmationToken;
    private Date createdDate;
    private String emailUser;

    //Constructor
    public ConfirmationToken (String emailUser) {
        this.emailUser = emailUser;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }

    public ConfirmationToken (String confirmationToken, String emailUser, Date createdDate) {
        this.confirmationToken = confirmationToken;
        this.emailUser = emailUser;
        this.createdDate = createdDate;
    }

    public ConfirmationToken () {}

    //Getter and Setter

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUser() {
        return emailUser;
    }

    public void setUser(String emailUser) {
        this.emailUser = emailUser;
    }

}
