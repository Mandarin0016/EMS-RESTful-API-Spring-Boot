package org.modis.EmsApplication.dao;

import org.modis.EmsApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query(value = "UPDATE user set timetable_id = null WHERE id = :userID ", nativeQuery = true)
    void cleanUserTimetable(@Param("userID") Long userID);

    @Modifying
    @Query(value = "DELETE FROM school_year_teachers WHERE teachers_id = :teacherID", nativeQuery = true)
    void cleanTeacherFromSchoolYears(@Param("teacherID") Long teacherID);

    @Modifying
    @Query(value = "DELETE FROM student_parents WHERE parent_id = :parentID", nativeQuery = true)
    void cleanParentRelation(@Param("parentID") Long parentID);

    @Modifying
    @Query(value = "DELETE FROM exam_results WHERE student_id = :studentID", nativeQuery = true)
    void cleanExamResults(@Param("studentID") Long studentID);

    @Modifying
    @Query(value = "DELETE FROM exam_answers WHERE student_id = :studentID", nativeQuery = true)
    void cleanExamAnswers(@Param("studentID") Long studentID);

    @Modifying
    @Query(value = "DELETE FROM competition_registered_student WHERE registered_student_id = :studentID", nativeQuery = true)
    void cleanCompetitionRegisteredStudents(@Param("studentID") Long studentID);

    @Modifying
    @Query(value = "DELETE FROM competition_performers WHERE student_id = :studentID", nativeQuery = true)
    void cleanCompetitionPerformers(@Param("studentID") Long studentID);

    @Modifying
    @Query(value = "UPDATE competition SET winner_id = null WHERE winner_id = :studentID", nativeQuery = true)
    void cleanCompetitionWinner(@Param("studentID") Long studentID);

    @Modifying
    @Query(value = "UPDATE certificate SET owner_id = null WHERE owner_id = :studentID", nativeQuery = true)
    void cleanCertificateOwner(@Param("studentID") Long studentID);


}
