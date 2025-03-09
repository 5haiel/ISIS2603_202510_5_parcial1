package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.exceptions.RepeatedStudentException;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.services.CourseService;
import uniandes.dse.examen1.services.StudentService;
import uniandes.dse.examen1.services.RecordService;

@DataJpaTest
@Transactional
@Import({ RecordService.class, CourseService.class, StudentService.class })
public class RecordServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private String login;
    private String courseCode;

    @BeforeEach
    void setUp() throws RepeatedCourseException, RepeatedStudentException {
        CourseEntity newCourse = factory.manufacturePojo(CourseEntity.class);
        newCourse = courseService.createCourse(newCourse);
        courseCode = newCourse.getCourseCode();

        StudentEntity newStudent = factory.manufacturePojo(StudentEntity.class);
        newStudent = studentService.createStudent(newStudent);
        login = newStudent.getLogin();
    }

    /**
     * Tests the normal creation of a record for a student in a course
     */
    @Test
    void testCreateRecord() {
        Double grade = 4.0;
        String semester = "2025-10";

        try {
            RecordEntity record = recordService.createRecord(login, courseCode, grade, semester);
            RecordEntity retrieveRecordEntity = entityManager.find(RecordEntity.class, record.getId());
            assertNotNull(record);
            assertNotNull(retrieveRecordEntity);
            assertEquals(login, retrieveRecordEntity.getStudent().getLogin());
            assertEquals(courseCode, retrieveRecordEntity.getCourse().getCourseCode());
            assertEquals(grade, retrieveRecordEntity.getFinalGrade());
            assertEquals(semester, retrieveRecordEntity.getSemester());
        } catch (InvalidRecordException e) {
            fail("No debería lanzarse una excepción");
        }
    }

    /**
     * Tests the creation of a record when the login of the student is wrong
     */
    @Test
    void testCreateRecordMissingStudent() {
        Double grade = 3.5;
        String semester = "2025-10";
        String invalidLogin = "EstudianteNoExiste123";

        try {
            recordService.createRecord(invalidLogin, courseCode, grade, semester);
            fail("Se esperaba una excepción porque el estudiante no existe.");
        } catch (InvalidRecordException e) {
            assertEquals("No existe un estudiante con el login: " + invalidLogin, e.getMessage());
        }
    }

    /**
     * Tests the creation of a record when the course code is wrong
     */
    @Test
    void testCreateInscripcionMissingCourse() {
        Double grade = 3.5;
        String semester = "2025-10";
        String invalidCourseCode = "CursoNoExiste123";

        try{
            recordService.createRecord(login, invalidCourseCode, grade, semester);
            fail("Se esperaba una excepción porque el curso no existe.");
        } catch (InvalidRecordException e) {
            assertEquals("No existe un curso con el codigo: " + invalidCourseCode, e.getMessage());
        }
    }

    /**
     * Tests the creation of a record when the grade is not valid
     */
    @Test
    void testCreateInscripcionWrongGrade() {
        Double wrongGrade = 5.1;
        String semester = "2025-10";    

        try{
            recordService.createRecord(login, courseCode, wrongGrade, semester);
            fail("Se esperaba una excepción porque la nota no es válida.");
        } catch (InvalidRecordException e) {
            assertEquals("La nota debe ser un número entre 1.5 y 5.0", e.getMessage());
        }
    }

    /**
     * Tests the creation of a record when the student already has a passing grade
     * for the course
     */
    @Test
    void testCreateInscripcionRepetida1() {
        Double validGrade = 4.0;
        String semester = "2024-1";

        try {
            recordService.createRecord(login, courseCode, validGrade, semester);
        } catch (InvalidRecordException e) {
            fail("No debería fallar la primera inscripción.");
        }

        entityManager.clear();
        try {
            recordService.createRecord(login, courseCode, 2.0, semester);
            fail("Se esperaba una excepción porque el estudiante ya aprobó el curso.");
        } catch (InvalidRecordException e) {
            assertEquals("El estudiante ya aprobó este curso con la nota: " + validGrade, e.getMessage());
        }
    }

    /**
     * Tests the creation of a record when the student already has a record for the
     * course, but he has not passed the course yet.
     */
    @Test
    void testCreateInscripcionRepetida2() {
        Double failedGrade = 2.0;
        String semester = "2024-1";

        try {
            recordService.createRecord(login, courseCode, failedGrade, semester);
        } catch (InvalidRecordException e) {
            fail("No debería fallar la primera inscripción.");
        }

        Double newGrade = 3.0;
        entityManager.clear();
        try {
            recordService.createRecord(login, courseCode, newGrade, semester);
            fail("Se esperaba una excepción porque el estudiante ya tiene una inscripción en el curso.");
        } catch (InvalidRecordException e) {
            assertEquals("El estudiante ya está inscrito en este curso", e.getMessage());
        }
    }
}
