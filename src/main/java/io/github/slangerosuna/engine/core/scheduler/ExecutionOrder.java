package io.github.slangerosuna.engine.core.scheduler;

public class ExecutionOrder {
    private ExecutionGroup[] executionGroups;
    private ExecutionOrder nextOrder;
    int index = 0;

    public ExecutionOrder(ExecutionGroup[] executionGroups){
        this.executionGroups = executionGroups;
    }

    public void addNextOrder(ExecutionOrder nextOrder) {
        // assert this.nextOrder == null;
        this.nextOrder = nextOrder;
    }

    public ExecutionGroup next(){
        if(index < executionGroups.length){
            return executionGroups[index++];
        }
        if (nextOrder != null) {
            var next = nextOrder.next();
            if (next != null)
                return next;
        }
        index = 0;
        return null;
    }
}
