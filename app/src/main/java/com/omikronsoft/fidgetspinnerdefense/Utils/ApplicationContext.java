package com.omikronsoft.fidgetspinnerdefense.Utils;

import android.content.Context;

/**
 * Created by Dariusz Lelek on 5/23/2017.
 * dariusz.lelek@gmail.com
 */

public class ApplicationContext {
    private Context appContext;

    private ApplicationContext() {
    }

    public void init(Context context) {
        if (appContext == null) {
            appContext = context;
        }
    }

    private Context getContext() {
        return appContext;
    }

    public static Context get() {
        return getInstance().getContext();
    }

    private static ApplicationContext instance;

    public static ApplicationContext getInstance() {
        return instance == null ?
                (instance = new ApplicationContext()) :
                instance;
    }
}
