package org.ganesh.urlshortener.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class ClickLog {

    // Getters & setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String referrer;
    private String location;

    @ManyToOne
    @JoinColumn(name = "url_shortcode")
    private Url url;

}


