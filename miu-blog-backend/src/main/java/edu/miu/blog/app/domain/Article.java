package edu.miu.blog.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "article")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String title;

    @Column(nullable = false,columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String description;

    @Column(nullable = false,columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String body;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "tag_list", columnDefinition = "TEXT", nullable = false)
    private String tagList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false,
            foreignKey = @ForeignKey(name = "article_author_id_foreign"))
    private User author;

    @Column(name = "favorites_count", nullable = false)
    private int favoritesCount;

    @Column(name = "locked_by")
    private Integer lockedBy;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @ManyToMany(mappedBy = "contributedArticles")
    private Set<User> contributors = new HashSet<>();

    @ManyToMany(mappedBy = "favoriteArticles")
    private Set<User> favoredByUsers = new HashSet<>();


    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @Transient
    private List<Tag> tags;


    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }


}
