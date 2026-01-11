package org.ormi.priv.tfa.orderflow.productregistry.infra.api;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.jboss.resteasy.reactive.RestResponse;

import jakarta.validation.ConstraintViolationException;

public class ServerErrorMappers {

    @ServerExceptionMapper
    public RestResponse<String> mapIllegalArgument(IllegalArgumentException ex) {
        return RestResponse.status(RestResponse.Status.BAD_REQUEST, ex.getMessage());
    }

    @ServerExceptionMapper
    public RestResponse<String> mapConstraintViolation(ConstraintViolationException ex) {
        return RestResponse.status(RestResponse.Status.BAD_REQUEST, "Validation error");
    }

    @ServerExceptionMapper
    public RestResponse<String> mapNullPointer(NullPointerException ex) {
        return RestResponse.status(RestResponse.Status.BAD_REQUEST, "Invalid request body");
    }
}
