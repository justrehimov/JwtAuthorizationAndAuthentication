package com.desofme.jwtauth.auth;

import com.desofme.jwtauth.model.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class ConfirmationToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String token;
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredAt;
    @OneToOne
    private User user;

    public ConfirmationToken(User user){
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.expiredAt = Date.from(Instant.now().plusSeconds(600));
    }
}
