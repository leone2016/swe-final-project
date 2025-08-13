package edu.miu.blog.app.repository;

import edu.miu.blog.app.domain.User;
import edu.miu.blog.app.dto.user.UserRegisterRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    default boolean isUserRegister(UserRegisterRequest request) {
        return existsByUsername(request.username()) ||
                existsByEmail(request.email());
    }

    Optional<User> findByEmailAndPassword(String email, String password);

    @EntityGraph(attributePaths = "followers")
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByEmailIn(List<String> emails);

    void deleteByEmail(String email);

    @EntityGraph(attributePaths = "followers")
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsernameWithFollowers(@Param("username") String username);

}