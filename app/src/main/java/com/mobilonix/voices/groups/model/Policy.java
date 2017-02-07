package com.mobilonix.voices.groups.model;

public class Policy {

    String policyName;
    String policyDescription;
    String associatedRepresentative;

    public Policy(String policyName, String policyDescription, String associatedRepresentative) {
        this.policyName = policyName;
        this. policyDescription = policyDescription;
        this.associatedRepresentative = associatedRepresentative;
    }

    //TODO: Do we need this or not?
    public String getAssociatedRepresentative() {
        return associatedRepresentative;
    }

    public String getPolicyDescription() {
        return policyDescription;
    }

    public String getPolicyName() {
        return policyName;
    }

}
