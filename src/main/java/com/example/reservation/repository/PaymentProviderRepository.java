package com.example.reservation.repository;

import com.example.reservation.entity.PaymentProvider;
import com.example.reservation.type.PaymentProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, Long> {
    Optional<PaymentProvider> findByPaymentProviderType(PaymentProviderType paymentProviderType);
}
