package com.desofme.jwtauth.auth;

import com.desofme.jwtauth.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class RefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredAt;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private User user;

    public RefreshToken(User user,Date expiredAt){
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.expiredAt = expiredAt;
    }
}
