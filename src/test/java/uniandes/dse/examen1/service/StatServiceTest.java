package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.exceptions.RepeatedStudentException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.services.CourseService;
import uniandes.dse.examen1.services.RecordService;
import uniandes.dse.examen1.services.StatsService;
import uniandes.dse.examen1.services.StudentService;
import uniandes.dse.examen1.entities.*;

@DataJpaTest
@Transactional
@Import({ RecordService.class, CourseService.class, StudentService.class, StatsService.class })
public class StatServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StatsService statsService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private String courseCode;
    private String studentLogin;

    @BeforeEach
    void setUp() throws RepeatedCourseException, RepeatedStudentException {
        CourseEntity newCourse = factory.manufacturePojo(CourseEntity.class);
        newCourse = courseService.createCourse(newCourse);
        courseCode = newCourse.getCourseCode();

        StudentEntity newStudent = factory.manufacturePojo(StudentEntity.class);
        newStudent = studentService.createStudent(newStudent);
        studentLogin = newStudent.getLogin();

        try {
            RecordEntity record1 = recordService.createRecord(newStudent.getLogin(), newCourse.getCourseCode(), 4.0, "2025-10");
            record1.setStudent(newStudent);
            record1.setCourse(newCourse);
            record1.setFinalGrade(4.0);
            record1.setSemester("2025-10");

        } catch (Exception e) {
            fail("No debería lanzarse una excepción: " + e.getMessage());
        }

        try {
            RecordEntity record2 = recordService.createRecord(newStudent.getLogin(), newCourse.getCourseCode(), 3.0, "2025-10");
            record2.setStudent(newStudent);
            record2.setCourse(newCourse);
            record2.setFinalGrade(3.0);
            record2.setSemester("2025-10");
        } catch (Exception e) {
            fail("No debería lanzarse una excepción: " + e.getMessage());
        }

        entityManager.flush();  
        entityManager.clear();
    }

    // @Test
    // void testFailure() {
    //     fail("always fails ...");
    // }

    @Test
    void testcalculateStudentAverage(){
        Double expectedAverage = 3.5;
        try {
            StudentEntity student = studentRepository.findByLogin(studentLogin).orElse(null);
            Double average = statsService.calculateStudentAverage(student.getLogin());
            assertEquals(expectedAverage, average);
        } catch (Exception e) {
            fail("No debería lanzarse una excepción: " + e.getMessage());
        }
    }

    @Test
    void testCalculateStudentAverageNoRecords() {
        StudentEntity newStudent = factory.manufacturePojo(StudentEntity.class);
        try{
            newStudent = studentService.createStudent(newStudent);
            Double average = statsService.calculateStudentAverage(newStudent.getLogin());
            assertEquals(0.0, average, "El promedio debe ser 0.0 si el estudiante no tiene registros.");
        } catch (Exception e){
            fail("No debería lanzarse una excepción: " + e.getMessage());
        }
    }

    @Test
    void testCalculateStudentAverageInvalidStudent() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> statsService.calculateStudentAverage("noExiste"));

        assertEquals("No existe el estudiante con el login: noExiste", exception.getMessage());
    }

    @Test
    void testCalculateCourseAverage() {
        Double average = statsService.calculateCourseAverage(courseCode);
        assertEquals(3.5, average, 0.01, "El promedio del curso debe ser 3.75");
    }

    @Test
    void testCalculateCourseAverageNoStudents() {
        CourseEntity newCourse = factory.manufacturePojo(CourseEntity.class);
        try {
            newCourse = courseService.createCourse(newCourse);
            Double average = statsService.calculateCourseAverage(newCourse.getCourseCode());
            assertEquals(0.0, average, "El promedio debe ser 0.0 si no hay estudiantes con registros.");
        } catch (Exception e) {
            fail("No debería lanzarse una excepción: " + e.getMessage());
        }
    }

    @Test
    void testCalculateCourseAverageInvalidCourse() {
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> statsService.calculateCourseAverage("cursoInvalido"));

        assertEquals("No existe el curso con el código: cursoInvalido", exception.getMessage());
    }
}
