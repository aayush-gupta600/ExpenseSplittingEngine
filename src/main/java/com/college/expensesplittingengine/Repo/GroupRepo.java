package com.college.expensesplittingengine.Repo;

import com.college.expensesplittingengine.Models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface GroupRepo extends JpaRepository<Group, Long> {

    Optional<Group> findByGroupCode(String groupCode);
    boolean existsByGroupCode(String groupCode);
}