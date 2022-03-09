package io.ont.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service("ConfigParam")
public class ConfigParam {

    @Value("${sync.ontology.url}")
    public String ONTOLOGY_RESTFUL_URL;
}