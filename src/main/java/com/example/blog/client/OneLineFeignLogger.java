package com.example.blog.client;

import feign.Logger;
import feign.Request;
import feign.Response;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OneLineFeignLogger extends Logger {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(OneLineFeignLogger.class);

    @Override
    protected void log(String configKey, String format, Object... args) {
        LOG.debug("{} {}", configKey, String.format(format, args).replaceAll("[\\r\\n]+", " "));
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel,
                                             Response response, long elapsedTime) {
        LOG.info("{} {} {} -> {} ({} ms)", configKey, response.request().httpMethod(),
                response.request().url(), response.status(), elapsedTime);
        return response;
    }

    @Override
    protected IOException logIOException(String configKey, Level logLevel,
                                         IOException exception, long elapsedTime) {
        LOG.warn("{} -> {} ({} ms)", configKey, exception.getClass().getSimpleName(), elapsedTime);
        return exception;
    }
}
