package com.asimov.sportroutes;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Sarekito on 17/11/15.
 */
public class OpcionesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Fragment3())
                .commit();
    }
}
