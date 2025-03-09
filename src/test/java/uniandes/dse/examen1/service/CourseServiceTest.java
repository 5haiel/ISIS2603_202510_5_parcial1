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
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.services.CourseService;

@DataJpaTest
@Transactional
@Import(CourseService.class)

public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    @BeforeEach
    void setUp() {

    }

    @Test
    void testCreateCourse() {
        // TODO
        CourseEntity course = factory.manufacturePojo(CourseEntity.class);
        String courseCode = course.getCourseCode();
        try{
            CourseEntity storedEntity = courseService.createCourse(course);
            CourseEntity retrieved = entityManager.find(CourseEntity.class, storedEntity.getId());
            assertEquals(courseCode, retrieved.getCourseCode(), "The course code is correct."); 
        } catch (RepeatedCourseException e){
            fail("No exception should be thrown: " + e.getMessage());
        }

    }

    @Test
    void testCreateRepeatedCourse() {
        // TODO
        CourseEntity firstCourseEntity = factory.manufacturePojo(CourseEntity.class);
        String courseCode = firstCourseEntity.getCourseCode();

        CourseEntity repeatedCourseEntity = new CourseEntity();
        repeatedCourseEntity.setCourseCode(courseCode);
        repeatedCourseEntity.setName("Repeated name");

        try {
            courseService.createCourse(firstCourseEntity);
            courseService.createCourse(repeatedCourseEntity);
            fail("An exception must be thrown");
        } catch (Exception e) {
        }

    
    }
}
