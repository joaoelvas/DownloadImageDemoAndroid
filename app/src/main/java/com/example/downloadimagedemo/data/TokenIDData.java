package com.example.downloadimagedemo.data;

/**
 * Created on 23/06/2017.
 *
 * @author Joao Elvas
 */

public class TokenIDData {

    String tokenID;

    public TokenIDData(String tokenID) {
        this.tokenID = tokenID;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    @Override
    public String toString() {
        return "TokenIDData{" +
                "tokenID='" + tokenID + '\'' +
                '}';
    }
}
