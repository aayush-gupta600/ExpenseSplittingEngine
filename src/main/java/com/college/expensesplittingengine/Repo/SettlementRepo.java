package com.college.expensesplittingengine.Repo;

import com.college.expensesplittingengine.Models.Settlements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepo extends JpaRepository<Settlements,Long> {

}
