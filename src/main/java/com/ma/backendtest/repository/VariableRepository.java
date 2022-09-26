package com.ma.backendtest.repository;

import com.ma.backendtest.model.Variable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VariableRepository extends JpaRepository<Variable, Long> {

    Optional<Variable> findByName(String name);
}
