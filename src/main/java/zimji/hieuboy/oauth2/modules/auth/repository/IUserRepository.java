package zimji.hieuboy.oauth2.modules.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zimji.hieuboy.oauth2.modules.auth.entity.UserEntity;

import java.util.Optional;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 22:29
 */

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    Optional<UserEntity> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

}
