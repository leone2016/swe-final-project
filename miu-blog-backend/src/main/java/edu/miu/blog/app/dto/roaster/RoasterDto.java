package edu.miu.blog.app.dto.roaster;

public interface RoasterDto {
    Long getId();
    String getUsername();
    Long getTotalArticlesWritten();
    Long getTotalLikesReceived();
    String getFirstArticleDate(); // or LocalDate if your DB returns that
}
