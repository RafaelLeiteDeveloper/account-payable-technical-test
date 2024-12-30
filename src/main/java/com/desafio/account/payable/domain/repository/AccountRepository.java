package com.desafio.account.payable.domain.repository;

import com.desafio.account.payable.domain.model.AccountModel;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Repository
public interface AccountRepository extends JpaRepository<AccountModel, Long> {

    @Query("SELECT a FROM AccountModel a WHERE a.paymentDate BETWEEN :startDate AND :endDate")
    List<AccountModel> findAccountsByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    Page<AccountModel> findAll(Specification<AccountModel> spec, Pageable pageable);

    default AccountModel findByIdOrError(Long id){
        return this.findById(id).orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

}
