package at.univie.davidreichert.analyticsandreport.repo;

import at.univie.davidreichert.analyticsandreport.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Optional<Analysis> findAnalysisByEventId(Long eventId);

    void deleteAnalysisByEventId(Long eventId);
}
