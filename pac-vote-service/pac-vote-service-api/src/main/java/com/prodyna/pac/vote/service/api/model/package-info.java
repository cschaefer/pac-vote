@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(type = LocalDate.class, 
                        value = LocalDateAdapter.class)
})  
package com.prodyna.pac.vote.service.api.model;

import java.time.LocalDate;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

