package uniandes.dse.examen1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.exceptions.RepeatedStudentException;
import uniandes.dse.examen1.repositories.StudentRepository;

@Slf4j
@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;

    public StudentEntity createStudent(StudentEntity newStudent) throws RepeatedStudentException {
        StudentEntity exists = studentRepository.findByLogin(newStudent.getLogin()).orElse(null);
        if (exists != null){
            throw new RepeatedStudentException(newStudent.getLogin());
        }

        StudentEntity student = new StudentEntity();
        newStudent.setName(newStudent.getName());
        newStudent.setId(newStudent.getId());
        newStudent.setLogin(newStudent.getLogin());
        newStudent.setCourses(newStudent.getCourses());
        newStudent.setRecords(newStudent.getRecords());

        return studentRepository.save(student);
    }
}
