/*
 * PaymentRepository.java
 * ✅ 목적: 결제 내역 조회 및 저장을 위한 JPA 리포지토리
 */
package MobilLax.Domain.Payment.Repository;

import MobilLax.Domain.Payment.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByEmail(String email);
}
