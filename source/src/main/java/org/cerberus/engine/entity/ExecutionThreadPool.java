/*
 * Cerberus  Copyright (C) 2013  vertigo17
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.engine.entity;

import org.cerberus.crud.entity.CountryEnvironmentParameters;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The execution thread pool to control Test Cases executions.
 *
 * Internally, this execution thread pool works as a size-settable {@link ThreadPoolExecutor}
 *
 * @author bcivel
 * @author abourdon
 */
public class ExecutionThreadPool {

    /**
     * The associated name of this {@link ExecutionThreadPool}
     */
    private String name;

    /**
     * The inner {@link ThreadPoolExecutor} that control Test Cases executions.
     * <p>
     * When instanciated, this {@link ThreadPoolExecutor} act the same as a {@link java.util.concurrent.Executors#newFixedThreadPool(int)},
     * but with the ability to tune its core pool size
     */
    private ThreadPoolExecutor executor;

    /**
     * Create a new {@link ExecutionThreadPool} based on the given {@link CountryEnvironmentParameters.Key} and initial pool size
     *
     * @param name        the {@link ExecutionThreadPool} name
     * @param initialSize the initial pool size of this {@link ExecutionThreadPool}
     */
    public ExecutionThreadPool(String name, int initialSize) {
        setName(name);
        initExecutor(initialSize);
    }

    /**
     * Get the name of this {@link ExecutionThreadPool}.
     *
     * @return the name of this {@link ExecutionThreadPool}
     */
    public String getName() {
        return name;
    }

    /**
     * Set a name for this {@link ExecutionThreadPool}.
     *
     * @param name the name of this {@link ExecutionThreadPool}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the number of maximum simultaneous active threads this {@link ExecutionThreadPool} can have
     *
     * @param size the number of maximum simultaneous active threads to set
     */
    public synchronized void setSize(Integer size) {
        int currentSize = getPoolSize();
        if (size < currentSize) {
            executor.setCorePoolSize(size);
            executor.setMaximumPoolSize(size);
        } else if (size > currentSize) {
            executor.setMaximumPoolSize(size);
            executor.setCorePoolSize(size);
        }
    }

    /**
     * Get the number of {@link Runnable} tasks this {@link ExecutionThreadPool} can execute in the same time
     *
     * @return the number of {@link Runnable} tasks this {@link ExecutionThreadPool} can execute in the same time
     */
    public synchronized int getPoolSize() {
        // Should be equal to executor.getMaximumPoolSize()
        return executor.getCorePoolSize();
    }

    /**
     * Get the number of currently active threads
     *
     * @return the number of currently active threads
     */
    public synchronized int getInExecution() {
        return executor.getActiveCount();
    }

    /**
     * Get the approximate number of active and pending executions
     *
     * @return the approximate number of active and pending executions
     */
    public synchronized long getInQueue() {
        return executor.getTaskCount() - executor.getCompletedTaskCount();
    }

    /**
     * Submit a new {@link Runnable} to a new thread from this {@link ExecutionThreadPool}.
     * <p>
     * If the maximum of simultaneous active threads is reached, then this task is kept in queue until a thread is released
     *
     * @param task the {@link Runnable} to submit to this {@link ExecutionThreadPool}
     * @see #getPoolSize()
     */
    public synchronized void submit(Runnable task) {
        executor.submit(task);
    }

    /**
     * Reset this {@link ExecutionThreadPool} by trying to stop any submitted tasks and make the {@link #getInExecution()} equals to 0
     *
     * @see #getInExecution()
     */
    public synchronized void reset() {
        int currentSIze = getPoolSize();
        stopExecutor();
        initExecutor(currentSIze);
    }

    /**
     * Stop this {@link ExecutionThreadPool} stopping its inner thread pool
     */
    public void stop() {
        stopExecutor();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Initialize the inner thread pool executor
     *
     * @param poolSize the thread pool size to set to the inner thread pool executor
     */
    private void initExecutor(int poolSize) {
        // The same as Executors#newFixedThreadPool(int) but with access to the
        // ThreadPoolExecutor API and so more controls than the ExecutorService one provided by
        // Executors#newFixedThreadPool(int)
        executor = new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
    }

    /**
     * Shutdown the inner thread pool by trying to stop any of its active or pending tasks
     */
    private void stopExecutor() {
        if (!executor.isShutdown()) {
            executor.shutdownNow();
        }
    }

}
