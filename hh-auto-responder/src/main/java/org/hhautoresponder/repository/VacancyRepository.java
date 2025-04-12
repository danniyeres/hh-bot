package org.hhautoresponder.repository;

import org.hhautoresponder.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyRepository extends JpaRepository<Vacancy,Long> {
}
