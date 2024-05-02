package io.github.slangerosuna.engine.core.scheduler;

import java.time.Instant;
import java.util.EnumMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import io.github.slangerosuna.engine.core.ecs.Scene;
import io.github.slangerosuna.engine.core.ecs.System;
import io.github.slangerosuna.engine.core.ecs.SystemType;
import io.github.slangerosuna.engine.core.query.Query;

public class Scheduler {
    private Scene scene;
    private BlockingQueue<Task> tasks;
    private EnumMap<SystemType, ExecutionOrder> executionOrder;
    private Instant lastTime = Instant.now();
    private WorkerThread[] workers;

    public Scheduler(Scene scene, int numThreads) {
        tasks = new LinkedBlockingQueue<Task>();
        workers = new WorkerThread[numThreads];
        for (int i = 0; i < numThreads; i++)
            workers[i] = new WorkerThread(tasks);
        this.scene = scene;
    }

    public void execute(SystemType type){
        var next = executionOrder.get(type).next();
        if (next == null) return;

        var dt = getDeltaTime(type);

        var entities = scene.getEntities();
        var resources = scene.getResources();

        while (next != null) {
            var tasks = next.genTasks(entities, resources, dt);

			if (tasks.size() != 1) {
            for (var task : tasks)
                schedule(task);
				waitUntilTasksAreDone();
			} else {
				tasks.get(0).execute();
			}
            next = executionOrder.get(type).next();
        }
    }

    public void genExecutionOrders(ArrayList<System> systems) {
        var initExecutionOrder = genExecutionOrder(selectOfType(systems, SystemType.INIT));
        var updateExecutionOrder = genExecutionOrder(selectOfType(systems, SystemType.UPDATE));
        var lateUpdateExecutionOrder = genExecutionOrder(selectOfType(systems, SystemType.LATEUPDATE));
        var renderExecutionOrder = genExecutionOrder(selectOfType(systems, SystemType.RENDER));
        updateExecutionOrder.addNextOrder(lateUpdateExecutionOrder);
        lateUpdateExecutionOrder.addNextOrder(renderExecutionOrder);
        var fixedUpdateExecutionOrder = genExecutionOrder(selectOfType(systems, SystemType.FIXEDUPDATE));
        var deInitExecutionOrder = genExecutionOrder(selectOfType(systems, SystemType.DEINIT));

        executionOrder = new EnumMap<SystemType, ExecutionOrder>(SystemType.class);
        executionOrder.put(SystemType.INIT, initExecutionOrder);
        executionOrder.put(SystemType.UPDATE, updateExecutionOrder);
        executionOrder.put(SystemType.FIXEDUPDATE, fixedUpdateExecutionOrder);
        executionOrder.put(SystemType.DEINIT, deInitExecutionOrder);
    }

    public List<System> selectOfType(List<System> systems, SystemType type) {
        return systems.stream().filter(e -> e.getType() == type).collect(Collectors.toList());
    }

    public ExecutionOrder genExecutionOrder(List<System> systems) {
        var executionGroups = new ArrayList<ExecutionGroup>();
        var remaining = new ArrayList<Integer>();
        for (int i = 0; i < systems.size(); i++)
            remaining.add(i);

        while (remaining.size() > 0)
            executionGroups.add(nextGroup(systems, remaining));

        return new ExecutionOrder(executionGroups.toArray(new ExecutionGroup[0]));
    }

    private ExecutionGroup nextGroup(List<System> systems, List<Integer> remaining) {
        var res = new ArrayList<System>();

        for (int i = 0; i < remaining.size(); i++) {
            if(!overlaps(res, systems.get(remaining.get(i))))
                res.add(systems.get(remaining.remove(i--)));
        }

        return new ExecutionGroup(res.toArray(new System[0]));
    }

    private boolean overlaps(ArrayList<System> systems, System system) {
        for(var elem : systems) {
            if (Query.overlaps(elem.getQueries(), system.getQueries()))
                return true;
        }
        return false;
    }

    private float getDeltaTime(SystemType type) {
        switch (type) {
            case FIXEDUPDATE:
                return 1.0f / 60;
            case UPDATE:
                var dt = (float)(Instant.now().toEpochMilli() - lastTime.toEpochMilli()) / 1000;
                lastTime = Instant.now();
                return dt;
            default:
                return 0.0f;
        }
    }

    private void waitUntilTasksAreDone() {
        for (var worker : workers)
            worker.waitUntilDone();
    }

    private void schedule(Task task) {
        try { tasks.put(task); }
        catch (InterruptedException e) {
            // should be impossible?
            java.lang.System.err.println("task scheduler interrupted: see Scheduler.java:72");
            Thread.currentThread().interrupt();
            schedule(task);
        }
    }
}
