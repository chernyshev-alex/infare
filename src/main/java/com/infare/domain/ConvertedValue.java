package com.infare.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

// Currency service response

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConvertedValue {

    private Double price;

}
