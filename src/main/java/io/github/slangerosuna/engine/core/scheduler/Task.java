package io.github.slangerosuna.engine.core.scheduler;

import java.util.function.Supplier;

public class Task {
    private Supplier<Boolean> function;
    public Task(Supplier<Boolean> function){
        this.function = function;
    }
    public boolean execute(){
        return function.get();
    }
}
