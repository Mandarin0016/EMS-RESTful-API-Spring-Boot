package org.modis.EmsApplication.dao;

import org.modis.EmsApplication.model.SchoolYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SchoolYearRepository extends JpaRepository<SchoolYear, Long> {
    @Modifying
    @Query(value = "UPDATE school_year set timetable_id = null WHERE id = :schoolyearID ", nativeQuery = true)
    void cleanSchoolYearTimetable(@Param("schoolyearID") Long schoolyearID);

    @Modifying
    @Query(value = "DELETE FROM school_year_exams WHERE school_year_id = :schoolYearID", nativeQuery = true)
    void cleanSchoolYearExamsRelation(@Param("schoolYearID") Long schoolYearID);

    @Modifying
    @Query(value = "DELETE FROM school_year_homework WHERE school_year_id = :schoolYearID", nativeQuery = true)
    void cleanSchoolYearHomeworkRelation(@Param("schoolYearID") Long schoolYearID);

    @Modifying
    @Query(value = "DELETE FROM school_year_students WHERE school_year_id = :schoolYearID", nativeQuery = true)
    void cleanSchoolYearStudentRelation(@Param("schoolYearID") Long schoolYearID);

    @Modifying
    @Query(value = "DELETE FROM school_year_teachers WHERE school_years_id = :schoolYearID", nativeQuery = true)
    void cleanSchoolYearTeacherRelation(@Param("schoolYearID") Long schoolYearID);

    @Modifying
    @Query(value = "DELETE FROM schoolyear_subjects WHERE schoolyear_id = :schoolYearID", nativeQuery = true)
    void cleanSchoolYearSubjectRelation(@Param("schoolYearID") Long schoolYearID);

    @Modifying
    @Query(value = "UPDATE user SET school_year_id = null WHERE school_year_id = :schoolYearID", nativeQuery = true)
    void cleanSchoolYearUserRelation(@Param("schoolYearID") Long schoolYearID);
}
