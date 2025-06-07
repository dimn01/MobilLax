package MobilLax.Domain.Payment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findByEmailAndDateBetween(String email, LocalDate start, LocalDate end);
}