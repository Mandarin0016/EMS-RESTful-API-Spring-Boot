package org.modis.EmsApplication.utils;

public class CommonMessages {
    public static final String USER_CREATION_EMAIL = """
            <!DOCTYPE html>
            <html>
            <body>          
            Welcome %s %s,<br>
            <br>
            Your school account was successfully created!<br>
            Please consider logging into your account with your email address and temporary password:<br><br>
            - Email address: %s<br>
            - Temporary password: %s<br>
            <br>
            <b>Important note:</b> You must change your password before your next login. Please use endpoint: /api/auth/resetPassword!<br>
            <br>
            Regards,<br>
            Education Management System - Account Support<br>
            </body>
            </html>
            """;
    public static final String PASSWORD_SUCCESSFULLY_CHANGED = """
            <!DOCTYPE html>
            <html>
            <body>          
            Congratulations %s %s,<br>
            <br>
            <b>Your account password was successfully changed!</b><br>
            <br>
            Regards,<br>
            Education Management System - Account Support<br>
            </body>
            </html>
            """;
    ;
    public static final String DEFAULT_HEADMASTER_ACCOUNT_SUCCESSFULLY_CREATED = "The default Headmaster account was successfully created!\nID='%s'\nFirst name='%s'\nLast name='%s'\nEmail address='%s'\nRole='%s'\nAccess level='%s'";
    public static final String SCHOOL_ACCOUNT_SUCCESSFULLY_CREATED = "School account was successfully created!";
    public static final String SCHOOL_ACCOUNT_PASSWORD_SUCCESSFULLY_CHANGED = "Account password was successfully changed!";
    public static final String GRADE_SEPARATOR = ", ";
    public static final String ALREADY_EXISTING_EMAIL = "User with email '%s' already exists.";
    public static final String USER_DOES_NOT_EXISTS = "User with ID = '%s' not existing.";
    public static final String USER_DOES_NOT_EXISTS_BY_EMAIL = "User with email = '%s' not existing.";
    public static final String STUDENT_DOES_NOT_EXISTS = "Student with ID = '%s' not existing.";
    public static final String SCHOOLYEAR_DOES_NOT_EXISTS = "SchoolYear with ID = '%s' not existing.";
    public static final String CANT_HAVE_MORE_THAN_TWELVE_SCHOOLYEARS = "The EMS System doesn't support school years count more than 12.";
    public static final String CANT_ADD_NON_STUDENT_TO_SCHOOLYEAR_STUDENTS_COLLECTION = "Can't add non student user to schoolyear 'student' collection.";
    public static final String CANT_ADD_NON_TEACHER_TO_SCHOOLYEAR_TEACHERS_COLLECTION = "Can't add non teacher user to schoolyear 'teacher' collection.";
    public static final String SUCH_GRADE_CAN_NOT_BE_FOUND = "Grade '%s' can't be found for Student with ID='%s'.";
    public static final String SUCH_SUBJECT_CAN_NOT_BE_FOUND = "Subject with name '%s' can't be found for Student with ID='%s'.";
    public static final String GRADE_MUST_BE_BETWEEN_TWO_AND_SIX = "Grade must be in range [2.00 - 6.00].";
    public static final String CAN_NOT_GRADE_NON_STUDENT = "Non student can't be graded.";
    public static final String CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT = "This operation can't be performed on/for non student.";
    public static final String HOMEWORK_NOT_ASSIGNED_TO_THE_USER = "Homework with ID='%s' is not assigned to Student with ID='%s'.";
    public static final String HOMEWORK_NOT_POSSESS_STUDENT_RESULT = "Homework with ID='%s' is missing result from Student with ID='%s'.";
    public static final String EXAM_NOT_ASSIGNED_TO_THE_USER = "Exam with ID='%s' is not assigned to Student with ID='%s'.";
    public static final String THIS_CERTIFICATE_HAS_NO_OWNER = "Certificate with ID='%s' has no owner yet.";
    public static final String USER_IS_NOT_EXAM_PERFORMER = "Student with ID='%s' is not performer for Exam with ID='%s'.";
    public static final String USER_ALREADY_WAS_GRADED_FOR_THIS_EXAM = "Student with ID='%s' already received grade from Exam with ID='%s'. Grade that was assigned - '%s'.";
    public static final String USER_MISSING_FROM_EXAM_RESULTS = "Student with ID='%s' is missing from results entries for Exam with ID='%s'.";
    public static final String COMPETITION_MISSING_REWARD = "Competition with ID='%s' is missing reward. Winner can't be found.";
    public static final String COMPETITION_NOT_FINISHED_YET = "Competition with ID='%s' is not finished yet. Try again later...";
    public static final String INVALID_USER_TYPE = "User with ID='%s' is not %s.";
    public static final String PASSWORD_RESET_ACCOUNT_STATUS = "User with email = '%s' posses PASSWORD_REST account status, therefore can't login before resetting the password.";
    public static final String THE_GIVEN_PASSWORD_IS_INCORRECT = "The given password is incorrect.";
    public static final String ALREADY_ACQUIRED_CERTIFICATE = "Certificate with ID='%s' is already acquired by User with ID='%s'.";
    public static final String STUDENT_ALREADY_PARTICIPATED_IN_THIS_COMPETITION = "Student with ID='%s' already participated within Competition with ID='%s'.";
    public static final String STUDENT_IS_NOT_REGISTERED_FOR_THIS_COMPETITION = "Student with ID='%s' is not registered for Competition with ID='%s'.";
    public static final String THIS_STUDENT_IS_ALREADY_WINNER = "This user is already a winner for this competition, no actions required.";
    public static final String COMPETITION_ALREADY_POSSESS_CERTIFICATE = "Competition with ID='%s' already possess Certificate with ID='%s'.";
    public static final String COMPETITION_MISSING_CERTIFICATE = "Competition with ID='%s' missing Certificate reward.";
    public static final String COMPETITION_NOT_STARTED_YET = "Competition with ID='%s' not started yet.";
    public static final String ONLY_PERFORMERS_CAN_BECOME_WINNERS = "User with ID='%s' is not performer for this competition, therefor can't be winner.";
    public static final String STUDENT_DOES_NOT_HAVE_SCHOOLYEAR = "Student with ID='%s' doesn't have SchoolYear, therefore you can't add this resource.";
    public static final String USER_ALREADY_ASSOCIATED_WITH_THIS_SCHOOLYEAR = "User with ID='%s' is already associated with SchoolYear with ID='%s'.";
    public static final String USER_NOT_ASSOCIATED_WITH_THIS_SCHOOLYEAR = "User with ID='%s' is not associated with SchoolYear with ID='%s'.";
    public static final String STUDENT_ALREADY_ASSIGNED_TO_ANOTHER_SCHOOLYEAR = "Student with ID='%s' is already assigned to another SchoolYear. Students can't have more than 1 SchoolYear!";
    public static final String SUBJECT_ALREADY_ASSOCIATED_WITH_THIS_SCHOOLYEAR = "Subject '%s' is already associated with SchoolYear with ID='%s'.";
    public static final String EXAM_ALREADY_ASSOCIATED_WITH_THIS_SCHOOLYEAR = "Exam with ID='%s' is already associated with SchoolYear with ID='%s'.";
    public static final String HOMEWORK_ALREADY_ASSOCIATED_WITH_THIS_SCHOOLYEAR = "Homework with ID='%s' is already associated with SchoolYear with ID='%s'.";
    public static final String TEACHER_MISSING_TIMETABLE = "Teacher with ID='%s' is missing Timetable.";
    public static final String NON_STUDENT_TRY_TO_PERFORM_EXAM = "Only Students can perform exams";
    public static final String EXAM_NOT_ASSIGNED_TO_STUDENT = "Exam with ID='%s' is not assigned to Student with ID='%s'.";
    public static final String CANT_ADD_CERTIFICATE_TO_NON_STUDENT = "Can't add certificate to non student.";
    public static final String STUDENT_ALREADY_POSSESS_THIS_CERTIFICATE = "Student with ID='%s' already possess this certificate.";
    public static final String COMPETITION_MISSING_WINNER = "Competition with ID='%s' is missing winner.";
    public static final String INVALID_DATA_PROVIDED = "Invalid data provided!";
    public static final String MUST_ADD_SUBJECT_TO_TEACHERS = "Teacher with ID='%s' is missing Subject!";
    public static final String SCHOOLYEAR_POSSESS_TIMETABLE = "SchoolYear with ID='%s' already possess Timetable with ID='%s'.";
    public static final String SCHOOLYEAR_DOES_NOT_POSSESS_TIMETABLE = "SchoolYear with ID='%s' does not have a timetable.";
    public static final String MISSING_USER_ANSWER = "Exam with ID='%s' missing answer from User with ID='%s'.";
    public static final String MISSING_STUDENT_RESULT = "Exam with ID='%s' missing result for Student with ID='%s'.";
    public static final String SUCH_CERTIFICATE_ALREADY_EXISTS = "Such Certificate already exists.";
    public static final String CERTIFICATE_MISSING_OWNER = "Certificate with ID='%s' is missing owner.";
    public static final String USER_DOES_NOT_HAVE_TIMETABLE = "User with ID='%s' does not have Timetable.";
    public static final String TIMETABLE_DOES_NOT_EXISTS = "Timetable with ID = '%s' not existing.";
    public static final String HOMEWORK_DOES_NOT_EXISTS = "Homework with ID = '%s' not existing.";
    public static final String CERTIFICATE_DOES_NOT_EXISTS = "Certificate with ID = '%s' not existing.";
    public static final String COMPETITION_DOES_NOT_EXISTS = "Competition with ID = '%s' not existing.";
    public static final String COMPETITION_FINISHED = "Competition with ID = '%s' is already finished, can't be performed.";
    public static final String EXAM_DOES_NOT_EXISTS = "Exam with ID = '%s' not existing.";
    public static final String ALREADY_EXISTING_TIMETABLE_TITLE = "Timetable with given title = '%s' already existing.";
    public static final String ALREADY_EXISTING_TIMETABLE_CONTENT_URL = "Timetable with given content URL = '%s' already existing.";
    public static final String THERE_IS_NO_WINNER = "It seems like there is no winner for Competition with ID='%s'.";
    public static final String DIFFERENT_ID_IN_REQUEST_BODY_AND_URL = "ID in URL='%d' is different from ID in message body = '%d'";
    public static final String EMS_CREATION_SERVICE_MAILBOX = "emscreationservices@gmail.com";
    public static final String EMAIL_CANT_BE_SENT = "Email can't be sent due to unknown reasons...";
    public static final String USER_ALREADY_POSSESS_THIS_RESOURCE = "User with ID='%s' already possess this resource!";
    public static final String EMAIL_ALREADY_EXISTING = "User with such email already existing.";
}
