package com.ayush.framework.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Subset of the Shopify product JSON returned by /products/&lt;handle&gt;.js.
 * Prices are integers in the smallest currency unit (pence), e.g. 5500 = £55.00.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Product(
        long id,
        String title,
        String handle,
        String vendor,
        String type,
        long price,
        boolean available,
        List<String> tags,
        List<Variant> variants,
        List<String> images
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Variant(
            long id,
            String title,
            long price,
            boolean available,
            @JsonProperty("option1") String option1
    ) {
    }
}
