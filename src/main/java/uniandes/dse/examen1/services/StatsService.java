package uniandes.dse.examen1.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.repositories.RecordRepository;

@Slf4j
@Service
public class StatsService {

    @Autowired
    StudentRepository estudianteRepository;

    @Autowired
    CourseRepository cursoRepository;

    @Autowired
    RecordRepository inscripcionRepository;

    public Double calculateStudentAverage(String login) {

        StudentEntity student = estudianteRepository.findByLogin(login).orElse(null);
        if (student == null){
            throw new IllegalArgumentException("No existe el estudiante con el login: " + login);
        }
        
        Double acc_grade = 0.0;
        int total_courses = 0;

        List<RecordEntity> records = student.getRecords();
        for (RecordEntity record : records){
            total_courses+=1;
            acc_grade+=record.getFinalGrade();
        }

        Double prom = acc_grade/total_courses;
        return prom;
    }

    public Double calculateCourseAverage(String courseCode) {
        CourseEntity course = cursoRepository.findByCourseCode(courseCode).orElse(null);
        if (course == null){
            throw new IllegalArgumentException("No existe el curso con el código: " + courseCode);
        }

        Double acc_grade = 0.0;
        int total_students = 0;

        List<StudentEntity> students = course.getStudents();
        for (StudentEntity student : students){
            List<RecordEntity> records = student.getRecords();
            for (RecordEntity record : records){
                if (record.getCourse().getCourseCode().equals(course.getCourseCode())){
                    total_students+=1;
                    acc_grade+=record.getFinalGrade();
                }
            }
        }

        Double prom = acc_grade/total_students;
        return prom;
    }

}
