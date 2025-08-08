package edu.miu.blog.app.repository;

import edu.miu.blog.app.domain.User;
import edu.miu.blog.app.dto.user.UserRegisterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    default boolean isUserRegister(UserRegisterRequest request) {
        return existsByUsername(request.getUsername()) ||
                existsByEmail(request.getEmail());
    }

}