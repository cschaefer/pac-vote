package com.prodyna.pac.vote.service.producer;

import com.prodyna.pac.vote.annotations.ApplicationProperty;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
 
public class ApplicationPropertyProducer {
 
    @Inject
    private PropertyFileReader propertyReader;
 
    @Produces
    @ApplicationProperty(name = "")
    public String getPropertyAsString(InjectionPoint injectionPoint) {
         
        String propertyName = injectionPoint.getAnnotated().getAnnotation(ApplicationProperty.class).name();
        String value = propertyReader.getProperty(propertyName);
         
        if (value == null || propertyName.trim().length() == 0) {
            throw new IllegalArgumentException("No property found with name " + value);
        }
        return value;
    }
     

}