package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "song_metadata")
@Data
public class SongMetaData {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "artist")
    private String artist;

    @Column(name = "album")
    private String album;

    @Column(name = "length")
    private String length;

    @Column(name ="resource_Id")
    private Integer resourceId;

    @Column(name = "year_creation")
    private Integer year;
}
