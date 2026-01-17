package com.shared_observability.feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;

import java.util.UUID;

public class CidFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String cid = MDC.get("cid");

        if (cid == null || cid.trim().isEmpty()) {
            cid = UUID.randomUUID().toString();
        }

        template.header("X-Request-Id", cid);

    }
}
