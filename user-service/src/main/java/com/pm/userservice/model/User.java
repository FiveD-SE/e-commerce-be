package com.pm.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"addresses", "credential"})
@Data
@Builder
public final class User extends AbstractMappedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false, updatable = false)
    private Integer userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "image_url")
    private String imageUrl;

    @Email(message = "*Input must be in Email format!**")
    private String email;

    private String phone;

    @Builder.Default
    @Column(name = "email_subscription")
    private Boolean emailSubscription = false;

    @Builder.Default
    @Column(name = "sms_subscription")
    private Boolean smsSubscription = false;

    @Builder.Default
    @Column(name = "marketing_consent")
    private Boolean marketingConsent = false;

    @Builder.Default
    @Column(name = "newsletter_subscription")
    private Boolean newsletterSubscription = false;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Address> addresses;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Wishlist> wishlists;

    @OneToOne(mappedBy = "user")
    private Credential credential;

}
