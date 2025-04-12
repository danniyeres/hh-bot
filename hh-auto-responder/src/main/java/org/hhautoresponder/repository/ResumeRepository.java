package org.hhautoresponder.repository;

import org.hhautoresponder.dto.resume.ResumeDto;
import org.hhautoresponder.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume,Long> {
    List<ResumeDto> findByUser_UserId(Long userUserId);
}
