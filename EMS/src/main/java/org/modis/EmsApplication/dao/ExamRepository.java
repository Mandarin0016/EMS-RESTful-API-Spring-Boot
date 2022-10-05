package org.modis.EmsApplication.dao;

import org.modis.EmsApplication.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    @Modifying
    @Query(value = "UPDATE exam_results set score = :score WHERE exam_id = :examID AND student_id = :studentID", nativeQuery = true)
    void updateExam(@Param("score") Double score, @Param("examID") Long examID, @Param("studentID") Long studentID);

    @Modifying
    @Query(value = "DELETE FROM student_exams WHERE exams_id = :examID", nativeQuery = true)
    void deleteStudentExam(@Param("examID") Long examID);
}
