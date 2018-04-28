package xyz.arnau.muvicat.utils;


import java.util.concurrent.Executor;

import xyz.arnau.muvicat.AppExecutors;

public class InstantAppExecutors extends AppExecutors {
    private static Executor instant = command -> command.run();

    public InstantAppExecutors() {
        super(instant, instant, instant);
    }
}
