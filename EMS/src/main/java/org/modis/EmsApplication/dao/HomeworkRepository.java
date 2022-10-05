package org.modis.EmsApplication.dao;

import org.modis.EmsApplication.model.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    @Modifying
    @Query(value = "DELETE FROM student_homework WHERE student_id = :studentID", nativeQuery = true)
    void cleanStudentEntry(@Param("studentID") Long studentID);
    @Modifying
    @Query(value = "DELETE FROM homework_answers WHERE student_id = :studentID", nativeQuery = true)
    void cleanStudentEntryAnswer(@Param("studentID") Long studentID);
    @Modifying
    @Query(value = "DELETE FROM student_homework WHERE homework_id = :homeworkID", nativeQuery = true)
    void cleanStudentHomework(@Param("homeworkID") Long homeworkID);
    @Modifying
    @Query(value = "DELETE FROM school_year_homework WHERE homework_id = :homeworkID", nativeQuery = true)
    void cleanSchoolYearHomework(@Param("homeworkID") Long homeworkID);
}
