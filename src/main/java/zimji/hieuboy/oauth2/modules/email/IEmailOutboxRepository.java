package zimji.hieuboy.oauth2.modules.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 20/08/2020 - 14:22
 */

@Repository
public interface IEmailOutboxRepository extends JpaRepository<EmailOutboxEntity, Long> {

}
