package com.mobilonix.voices.data.api.tasks;


import com.mobilonix.voices.data.model.Politico;

import java.util.ArrayList;


public interface  RetrievalCompletedListener {

    void onRetrievalComplete(ArrayList<Politico> responses);

}