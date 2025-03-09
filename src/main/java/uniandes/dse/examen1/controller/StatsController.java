package uniandes.dse.examen1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import uniandes.dse.examen1.dto.StudentAverageDTO;
import uniandes.dse.examen1.dto.CourseAverageDTO;
import uniandes.dse.examen1.services.StatsService;

@RestController
@RequestMapping("/stats")   
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/student/{login}")
    @ResponseStatus(HttpStatus.OK)
    public StudentAverageDTO getStudentAverage(@PathVariable String login) throws EntityNotFoundException {
        Double average = statsService.calculateStudentAverage(login);
        StudentAverageDTO studentAverageDTO = new StudentAverageDTO();
        studentAverageDTO.setLogin(login);
        studentAverageDTO.setAverage(average);
        return studentAverageDTO;
    }

    @GetMapping("/course/{courseCode}")
    @ResponseStatus(HttpStatus.OK)
    public CourseAverageDTO getCourseAverage(@PathVariable String courseCode) throws EntityNotFoundException {
        Double average = statsService.calculateCourseAverage(courseCode);
        CourseAverageDTO courseAverageDTO = new CourseAverageDTO();
        courseAverageDTO.setCourseCode(courseCode);
        courseAverageDTO.setAverage(average);
        return courseAverageDTO;
    }
}