// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * <p>ExceptionHandlingAsyncTaskExecutor class.</p>
 */
public class ExceptionHandlingAsyncTaskExecutor implements AsyncTaskExecutor,
        InitializingBean, DisposableBean {

    static final String EXCEPTION_MESSAGE = "Caught async exception";
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlingAsyncTaskExecutor.class);

    private final AsyncTaskExecutor executor;

    /**
     * <p>Constructor for ExceptionHandlingAsyncTaskExecutor.</p>
     *
     * @param executor a {@link org.springframework.core.task.AsyncTaskExecutor} object.
     */
    public ExceptionHandlingAsyncTaskExecutor(AsyncTaskExecutor executor) {
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable task) {
        executor.execute(createWrappedRunnable(task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable task, long startTimeout) {
        executor.submit(createWrappedRunnable(task));
    }

    private <T> Callable<T> createCallable(Callable<T> task) {
        return () -> {
            try {
                return task.call();
            } catch (Exception e) {
                handle(e);
                throw e;
            }
        };
    }

    private Runnable createWrappedRunnable(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                handle(e);
            }
        };
    }

    /**
     * <p>handle.</p>
     *
     * @param e a {@link java.lang.Exception} object.
     */
    protected void handle(Exception e) {
        log.error(EXCEPTION_MESSAGE, e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<?> submit(Runnable task) {
        return executor.submit(createWrappedRunnable(task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(createCallable(task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {
        if (executor instanceof DisposableBean bean) {
            bean.destroy();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (executor instanceof InitializingBean bean) {
            bean.afterPropertiesSet();
        }
    }
}