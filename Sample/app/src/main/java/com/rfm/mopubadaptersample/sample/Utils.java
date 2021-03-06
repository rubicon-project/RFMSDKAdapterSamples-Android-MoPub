/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */
package com.rfm.mopubadaptersample.sample;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;

public class Utils {

    static void snackbar(Activity activity, String msg, boolean positive) {
        Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setActionTextColor(positive?Color.GREEN:Color.RED)
                .show();
    }

}
