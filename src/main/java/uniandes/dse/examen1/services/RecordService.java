package uniandes.dse.examen1.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.repositories.RecordRepository;

@Slf4j
@Service
public class RecordService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    RecordRepository recordRepository;

    public RecordEntity createRecord(String loginStudent, String courseCode, Double grade, String semester)
            throws InvalidRecordException {
        // TODO
        StudentEntity student = studentRepository.findByLogin(loginStudent).orElse(null);
        if (student == null){
            throw new InvalidRecordException("No existe un estudiante con el login: " + loginStudent);
        }

        CourseEntity course = courseRepository.findByCourseCode(courseCode).orElse(null);
        if (course == null){
            throw new InvalidRecordException("No existe un curso con el codigo: " + courseCode);
        }

        if (grade < 1.5 || grade > 5.0){
            throw new InvalidRecordException("La nota debe ser un número entre 1.5 y 5.0");
        }

        RecordEntity record = new RecordEntity();
        record.setFinalGrade(grade);
        record.setSemester(semester);
        record.setStudent(student);
        
        return recordRepository.save(record);
    }
}
