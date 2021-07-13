package com.energizeglobal.egsinterviewtest.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

    private HeaderUtil() {
    }

    public static HttpHeaders createAlert(String applicationName, String message, String param) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(String.format("X-%s-alert", applicationName), message);

        try {

            headers.add(String.format("X-%s-params", applicationName),
                    URLEncoder.encode(param, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException exception) {

            log.error(exception.getMessage());
        }

        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String applicationName,
                                                        boolean enableTranslation,
                                                        String entityName,
                                                        String param) {

        String message = enableTranslation ?
                String.format("%s.%s.created", applicationName, entityName) :
                String.format("A new %s is created with identifier %s", entityName, param);

        return createAlert(applicationName, message, param);
    }

    public static HttpHeaders createEntityUpdateAlert(String applicationName,
                                                      boolean enableTranslation,
                                                      String entityName,
                                                      String param) {

        String message = enableTranslation ?
                String.format("%s.%s.updated", applicationName, entityName) :
                String.format("A %s is updated with identifier %s", entityName, param);

        return createAlert(applicationName, message, param);
    }

    public static HttpHeaders createEntityDeletionAlert(String applicationName,
                                                        boolean enableTranslation,
                                                        String entityName,
                                                        String param) {

        String message = enableTranslation ?
                String.format("%s.%s.deleted", applicationName, entityName) :
                String.format("A %s is deleted with identifier %s", entityName, param);

        return createAlert(applicationName, message, param);
    }

    public static HttpHeaders createFailureAlert(String applicationName,
                                                 boolean enableTranslation,
                                                 String entityName,
                                                 String errorKey,
                                                 String defaultMessage) {

        log.error("Entity processing failed, {}", defaultMessage);

        String message = enableTranslation ? String.format("error.%s", errorKey) : defaultMessage;

        HttpHeaders headers = new HttpHeaders();

        headers.add(String.format("X-%s-error", applicationName), message);
        headers.add(String.format("X-%s-params", applicationName), entityName);

        return headers;
    }
}
