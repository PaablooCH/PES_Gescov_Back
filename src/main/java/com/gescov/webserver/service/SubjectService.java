package com.gescov.webserver.service;

import com.gescov.webserver.dao.subject.SubjectDao;
import com.gescov.webserver.exception.IsNotAnAdministratorException;
import com.gescov.webserver.exception.NotFoundException;
import com.gescov.webserver.exception.NotInSchool;
import com.gescov.webserver.model.ClassSession;
import com.gescov.webserver.model.School;
import com.gescov.webserver.model.Subject;
import com.gescov.webserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    SubjectDao subjectDao;

    @Autowired
    SchoolService schoolService;

    @Autowired
    UserService userService;

    @Autowired
    ClassSessionService classSessionService;



    public Subject addSubject(Subject subject, String creatorID){
        String schoolID = subject.getSchoolID();
        School school = schoolService.getSchoolByID(schoolID);
        userService.existsUser(creatorID);
        if (!school.getAdministratorsID().contains(creatorID)) throw new IsNotAnAdministratorException(User.class, creatorID, schoolID);
        subject.addTeacher(creatorID);
        return subjectDao.insert(subject);
    }

    public List<Subject> getAllSubject() { return subjectDao.findAll(); }

    public List<Subject> selectSubjectBySchoolId(String id) { return subjectDao.findAllBySchoolID(id); }

    public Subject getSubjectById(String id) {
        Optional<Subject> subject = subjectDao.findById(id);
        if (subject.isEmpty()) throw new NotFoundException(Subject.class, id);
        return subject.get();
    }

    public List<Subject> getSubjectBySchool(String schoolName) {
        School school = schoolService.getSchoolByName(schoolName);
        return subjectDao.selectAllBySchoolID(school.getId());
    }

    public List<Subject> getSubjectByName(String name) { return subjectDao.findAllByName(name); }

    public void deleteSubject(String id) {
        deleteClassSessionsOfASubject(id);
        subjectDao.deleteById(id);
    }

    public void deleteSubjectById(String id, String adminID) {
        Subject s = getSubjectById(id);
        School school = schoolService.getSchoolByID(s.getSchoolID());
        if (!school.getAdministratorsID().contains(adminID)) throw new IsNotAnAdministratorException(User.class, adminID, school.getId());
        deleteClassSessionsOfASubject(id);
        subjectDao.deleteById(id);
    }

    private void deleteClassSessionsOfASubject(String id) {
        List<ClassSession> cs = classSessionService.getSessionBySubject(id);
        if (!cs.isEmpty()) {
            for (ClassSession classSes : cs) {
                classSessionService.deleteClassSession(classSes.getId());
            }
        }
    }

    public void updateSubject(String id, String name){
        Subject s = getSubjectById(id);
        s.setName(name);
        subjectDao.save(s);
    }

    public Subject addUser(String id, String userId){
        Subject s = getSubjectById(id);
        User user = userService.getUserById(userId);
        String schoolID = s.getSchoolID();
        if (!user.getSchoolsID().contains(schoolID)) throw new NotInSchool(userId, schoolID);
        if (user.isStudent()) s.addStudent(userId);
        else s.addTeacher(userId);
        subjectDao.save(s);
        return s;
    }

}

