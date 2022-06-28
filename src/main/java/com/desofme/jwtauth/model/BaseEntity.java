package com.desofme.jwtauth.model;

import com.desofme.jwtauth.enums.EnumStatus;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@MappedSuperclass
@Data
public abstract class BaseEntity {
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt = new Date();
    private Integer status = EnumStatus.ACTIVE.getValue();
}
