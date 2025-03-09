package uniandes.dse.examen1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.repositories.CourseRepository;

@Slf4j
@Service
public class CourseService {

    @Autowired
    CourseRepository courseRepository;

    public CourseEntity createCourse(CourseEntity newCourse) throws RepeatedCourseException {
        if (courseRepository.findByCourseCode(newCourse.getCourseCode()).isPresent()){
            throw new RepeatedCourseException(newCourse.getCourseCode());
        }

        CourseEntity course = new CourseEntity();
        course.setCourseCode(newCourse.getCourseCode());
        course.setCredits(newCourse.getCredits());
        course.setName(newCourse.getName());

        return courseRepository.save(course);
    }
}
