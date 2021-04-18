package com.computerservice.config.rest;

import com.computerservice.entity.pc.gpu.Gpu;
import com.computerservice.entity.pc.motherboard.Motherboard;
import com.computerservice.entity.pc.motherboard.SupportedCpu;
import com.computerservice.entity.pc.powersupply.PowerSupply;
import com.computerservice.entity.pc.processor.Processor;
import com.computerservice.entity.pc.ram.Ram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ExposureConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    private EntityManager theEntityManager;

    @Autowired
    public RestConfig(EntityManager theEntityManager) {
        this.theEntityManager = theEntityManager;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry corsRegistry) {
        HttpMethod[] unsupportedMethods = {HttpMethod.DELETE, HttpMethod.POST, HttpMethod.PUT};

        disableHttpMethods(config, unsupportedMethods, Gpu.class);
        disableHttpMethods(config, unsupportedMethods, Motherboard.class);
        disableHttpMethods(config, unsupportedMethods, PowerSupply.class);
        disableHttpMethods(config, unsupportedMethods, Ram.class);
        disableHttpMethods(config, unsupportedMethods, Processor.class);
        disableHttpMethods(config, unsupportedMethods, SupportedCpu.class);

        exposeIds(config);
    }

    private void disableHttpMethods(RepositoryRestConfiguration config, HttpMethod[] unsupportedMethods, Class<?> theClass) {
        config.getExposureConfiguration()
                .forDomainType(theClass)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedMethods));
    }

    private void exposeIds(RepositoryRestConfiguration config) {

        Set<EntityType<?>> entityTypeSet = theEntityManager.getMetamodel().getEntities();

        List<Class> entities = new ArrayList<>();

        for (EntityType entityType : entityTypeSet) {
            entities.add(entityType.getJavaType());
        }

        Class[] domainTypes = entities.toArray(new Class[0]);
        config.exposeIdsFor(domainTypes);
    }
}
