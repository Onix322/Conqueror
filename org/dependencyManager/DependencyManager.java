package org.dependencyManager;

import org.dependencyManager.util.PomReader;

public class DependencyManager {

    private final PomReader pomReader;

    private DependencyManager(PomReader pomReader){
        this.pomReader = pomReader;
    }

    private static class Holder{
        private static DependencyManager INSTANCE = null;
    }

    public synchronized static void init(PomReader pomReader){
        if(Holder.INSTANCE == null){
            Holder.INSTANCE = new DependencyManager(pomReader);
        }
    }

    public static DependencyManager getInstance(){
        return Holder.INSTANCE;
    }

}
