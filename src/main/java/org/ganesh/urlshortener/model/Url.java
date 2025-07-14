package org.ganesh.urlshortener.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
public class Url {

    // Getters & setters
    @Id
    private String shortcode;
    @Column(length = 50000)
    private String longUrl;

    private LocalDateTime createdAt;

    private LocalDateTime expiry;

    private int clickCount = 0;

    @OneToMany(mappedBy = "url", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClickLog> clickLogs;

}


