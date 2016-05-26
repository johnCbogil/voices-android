package com.johnbogil.voices.misc;

import com.johnbogil.voices.congress.CongressClass;
import com.johnbogil.voices.state.StateLegislatorsClass;
import com.johnbogil.voices.district.CouncilMembersClass;

/**
 * Created by chrislinder1 on 1/31/16.
 */
public class ParseJSONData {

    private CongressClass[] mCongressClasses;
    private CouncilMembersClass[] mCounciMembersClasses;
    private StateLegislatorsClass[] mStateLegislatorsClasses;

    public ParseJSONData(StateLegislatorsClass[] stateLegislatorsClasses) {
        mStateLegislatorsClasses = stateLegislatorsClasses;
    }

    public ParseJSONData() {

    }

    public void setStateLegislatorsClasses(StateLegislatorsClass[] stateLegislatorsClasses) {
        mStateLegislatorsClasses = stateLegislatorsClasses;
    }

    public CongressClass[] getCongressClasses() {
        return mCongressClasses;
    }

    public void setCongressClasses(CongressClass[] congressClasses) {
        mCongressClasses = congressClasses;
    }

    public StateLegislatorsClass[] getStateLegislatorsClasses() {
        return mStateLegislatorsClasses;
    }

    public CouncilMembersClass[] getCouncilMembersClasses() {
        return mCounciMembersClasses;
    }

    public void setCouncilMembersClass(CouncilMembersClass[] councilMembersClasses) {
        mCounciMembersClasses = councilMembersClasses;
    }
}

