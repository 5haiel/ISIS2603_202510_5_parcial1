package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.exceptions.RepeatedStudentException;
import uniandes.dse.examen1.services.StudentService;

@DataJpaTest
@Transactional
@Import(StudentService.class)
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    @BeforeEach
    void setUp() {

    }

    @Test
    void testCreateStudent() {
        StudentEntity newEntity = factory.manufacturePojo(StudentEntity.class);
        String login = newEntity.getLogin();

        try {
            StudentEntity storedEntity = studentService.createStudent(newEntity);
            StudentEntity retrieved = entityManager.find(StudentEntity.class, storedEntity.getId());
            assertEquals(login, retrieved.getLogin(), "The login is not correct");
        } catch (RepeatedStudentException e) {
            fail("No exception should be thrown: " + e.getMessage());
        }
    }

    @Test
    void testCreateEstudianteRepetido() {
        StudentEntity firstEntity = factory.manufacturePojo(StudentEntity.class);
        String login = firstEntity.getLogin();

        StudentEntity repeatedEntity = new StudentEntity();
        repeatedEntity.setLogin(login);
        repeatedEntity.setName("repeated name");

        try {
            studentService.createStudent(firstEntity);
            studentService.createStudent(repeatedEntity);
            fail("An exception must be thrown");
        } catch (Exception e) {
        }
    }
}