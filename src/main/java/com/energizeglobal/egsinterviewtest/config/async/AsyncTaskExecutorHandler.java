package com.energizeglobal.egsinterviewtest.config.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class AsyncTaskExecutorHandler implements AsyncTaskExecutor, InitializingBean, DisposableBean {

    private final Logger log = LoggerFactory.getLogger(AsyncTaskExecutorHandler.class);
    private final AsyncTaskExecutor executor;

    public AsyncTaskExecutorHandler(AsyncTaskExecutor executor) {

        this.executor = executor;
    }

    @Override
    public void execute(Runnable task) {

        this.executor.execute(this.createWrappedRunnable(task));
    }

    @Override
    public void execute(Runnable task, long startTimeout) {

        this.executor.execute(this.createWrappedRunnable(task), startTimeout);
    }

    private <T> Callable<T> createCallable(Callable<T> task) {

        return () -> {
            try {

                return task.call();
            } catch (Exception exception) {

                this.handle(exception);
                throw exception;
            }
        };
    }

    private Runnable createWrappedRunnable(Runnable task) {

        return () -> {

            try {

                task.run();
            } catch (Exception exception) {

                this.handle(exception);
            }
        };
    }

    protected void handle(Exception exception) {

        this.log.error("Caught async exception", exception);
    }

    @Override
    public Future<?> submit(Runnable task) {

        return this.executor.submit(this.createWrappedRunnable(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {

        return this.executor.submit(this.createCallable(task));
    }

    @Override
    public void destroy() throws Exception {

        if (this.executor instanceof DisposableBean) {

            DisposableBean bean = (DisposableBean) this.executor;

            bean.destroy();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (this.executor instanceof InitializingBean) {

            InitializingBean bean = (InitializingBean) this.executor;

            bean.afterPropertiesSet();
        }
    }
}
