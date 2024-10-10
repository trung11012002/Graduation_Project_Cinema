package com.cinema.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "thumnail")
@Data
@NoArgsConstructor
public class Thumnail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "url")
    private String url;

    @Column(name = "publicId")
    private String publicId;

    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    @JsonIgnore
    private Film film;
}
