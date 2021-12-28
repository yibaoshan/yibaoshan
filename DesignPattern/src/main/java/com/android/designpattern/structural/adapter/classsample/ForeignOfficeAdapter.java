package com.android.designpattern.structural.adapter.classsample;

public class ForeignOfficeAdapter extends IdentityCard implements IPublicIDCard {

    public ForeignOfficeAdapter(String localID, String fullName) {
        super(localID, fullName);
    }

    @Override
    public String getID() {
        return queryPassportNoByLocalID(this.LocalID);
    }

    @Override
    public String getName() {
        return queryPassportFullName(this.FullName);
    }

    public String queryPassportNoByLocalID(String localID) {
        //query..
        return "passport-" + localID;
    }

    public String queryPassportFullName(String fullName) {
        //query..
        return "passport-" + fullName;
    }
}
