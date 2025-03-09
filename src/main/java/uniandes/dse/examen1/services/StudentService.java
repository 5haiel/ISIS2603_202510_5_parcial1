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
        if (studentRepository.findByLogin(newStudent.getLogin()).isPresent()){
            throw new RepeatedStudentException(newStudent.getLogin());
        }

        StudentEntity student = new StudentEntity();
        student.setName(newStudent.getName());
        student.setLogin(newStudent.getLogin());

        return studentRepository.save(student);
    }
}
