package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "song_metadata")
@Data
public class SongMetadata {
    @Id
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "artist")
    private String artist;

    @Column(name = "album")
    private String album;

    @Column(name = "length")
    private String length;

    @Column(name = "year_creation")
    private Integer year;
}
