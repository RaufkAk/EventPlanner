package com.yeditepe.entity;



import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import java.util.Set;
@Builder
@Entity
@Table(name = "users")
@Data


public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name ="first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name ="last_name",length = 100, nullable = false)
    private String lastName;

    @Column( unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER) // Kullanıcı birden fazla role sahip olabiliyor mu sor ?
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public User(){}

    public User(Long id, String firstName, String lastName, String username, String password, String email, Set<Role> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }
}