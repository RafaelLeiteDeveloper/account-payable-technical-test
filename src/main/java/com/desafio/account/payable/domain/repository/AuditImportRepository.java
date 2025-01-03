package com.desafio.account.payable.domain.repository;

import com.desafio.account.payable.domain.model.AuditImportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditImportRepository extends JpaRepository<AuditImportModel, Long> {
     Optional<AuditImportModel> findByIdProcess(String idProcess);
}
