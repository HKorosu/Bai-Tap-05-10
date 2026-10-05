package com.example.ormdemo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "author_profiles")
public class AuthorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String biography;

    // ============================================================
    // MINH HỌA FetchType.LAZY (đã dùng trong Author.profile)
    // Ở đây ta minh họa FetchType.EAGER: khi load AuthorProfile,
    // Author sẽ được load NGAY LẬP TỨC (mặc định của @OneToOne là EAGER).
    // ============================================================
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    public AuthorProfile() {
    }

    public AuthorProfile(String biography) {
        this.biography = biography;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}