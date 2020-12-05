package com.gescov.webserver.service;

import com.gescov.webserver.dao.school.SchoolDao;
import com.gescov.webserver.exception.*;
import com.gescov.webserver.model.Classroom;
import com.gescov.webserver.model.School;
import com.gescov.webserver.model.Subject;
import com.gescov.webserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class SchoolService {

    @Autowired
    SchoolDao schoolDao;

    @Autowired
    UserService userService;

    @Autowired
    ClassroomService classroomService;

    @Autowired
    SubjectService subjectService;

    public School addSchool(School school) {
        String creatorID = school.getCreatorID();
        school.setEntryCode(generateEntryCode());
        userService.existsTeacher(creatorID);
        schoolDao.insert(school);
        userService.addSchool(creatorID, school.getId());
        return school;
    }

    public void addAdministrator(String schoolID, String adminID, String newAdminID) {
        Optional<School> s = schoolDao.findById(schoolID);
        if (s.isEmpty()) throw new NotFoundException(School.class, schoolID);
        School sc = s.get();
        userService.existsTeacher(adminID);
        userService.existsTeacher(newAdminID);
        isAdmin(schoolID, adminID);
        if (sc.getAdministratorsID().contains(newAdminID)) throw new AlreadyExistsException(User.class, newAdminID);
        sc.addAdministrator(newAdminID);
        schoolDao.save(sc);
    }

    public List<School> getAllSchools() {
        return schoolDao.findAll();
    }

    public School getSchoolByName(String schoolName) {
        return schoolDao.findByName(schoolName);
    }

    public void deleteSchool(String id, String creatorID) {
        existsSchoolByID(id);
        userService.existsTeacher(creatorID);
        isCreator(id, creatorID);
        schoolDao.deleteById(id);
        deleteAllClassroomsOfSchool(id);
        deleteAllSubjectsOfSchool(id);
        userService.deleteSchool(id);
    }

    public void deleteAdmin(String id, String creatorID, String adminID) {
        if (creatorID.equals(adminID)) throw new CreatorCantBeDeletedException(User.class, creatorID);
        Optional <School> sc = schoolDao.findById(id);
        if (sc.isEmpty()) throw new NotFoundException(School.class, id);
        School s = sc.get();
        userService.existsTeacher(creatorID);
        isCreator(id, creatorID);
        userService.existsTeacher(adminID);
        isAdmin(id, adminID);
        s.getAdministratorsID().remove(adminID);
        schoolDao.save(s);
    }

    private void deleteAllSubjectsOfSchool(String id) {
        List<Subject> su = subjectService.selectSubjectBySchoolId(id);
        for (Subject sub : su) subjectService.deleteSubject(sub.getId());
    }

    private void deleteAllClassroomsOfSchool(String id) {
        List<Classroom> c = classroomService.getClassroomsBySchoolID(id);
        for (Classroom cl : c) classroomService.deleteClassroom(cl.getId());
    }

    public School updateSchool(String id, String name, float latitude, float longitude, String phone, String website, String address) {
        Optional<School> s = schoolDao.findById(id);
        if (s.isEmpty()) throw new NotFoundException(School.class, id);
        School sc = s.get();
        if (!name.equals("")) sc.setName(name);
        if (longitude != 0.0) sc.setLongitude(longitude);
        if (latitude != 0.0) sc.setLongitude(latitude);
        if (phone.equals("")) sc.setPhone(phone);
        if (!website.equals("")) sc.setWebsite(website);
        if (!address.equals("")) sc.setAddress(address);
        return schoolDao.save(sc);
    }

    public void updateSchoolName(String id, String update) {
        Optional<School> s = schoolDao.findById(id);
        if (s.isEmpty()) throw new NotFoundException(School.class, id);
        s.get().setName(update);
        schoolDao.save(s.get());
    }

    public void updateSchoolState(String id, String update) {
        Optional<School> s = schoolDao.findById(id);
        if (s.isEmpty()) throw new NotFoundException(School.class, id);
        s.get().setState(update);
        schoolDao.save(s.get());
    }

    public void isAdmin(String schoolID, String adminID) {
        if (!schoolDao.existsByIdAndAdministratorsIDIn(schoolID, adminID)) throw new IsNotAnAdministratorException(User.class, adminID, schoolID);
    }

    public void isCreator(String schoolID, String creatorID) {
        if (!schoolDao.existsByIdAndCreatorID(schoolID, creatorID)) throw new IsNotTheCreatorException(User.class, creatorID, schoolID);
    }

    public List<Pair<School, Integer>> getSchoolsAndNumInfected() {
        List<School> schoolList = schoolDao.findAll();
        if (schoolList.isEmpty()) return null;
        int punctuation;
        List<Pair<School, Integer>> aux = new ArrayList<>();
        for (School school : schoolList) {
            punctuation = userService.countInfectedInSchool(school.getId());
            aux.add(Pair.of(school, punctuation));
        }
        aux.sort(Comparator.comparingInt(Pair::getSecond));
        return aux;
    }

    public School getSchoolByID(String id) {
        Optional <School> sc = schoolDao.findById(id);
        if (sc.isEmpty()) throw new NotFoundException(School.class, id);
        return sc.get();
    }

    public void existsSchoolByID(String schoolID) {
        Optional <School> sc = schoolDao.findById(schoolID);
        if (sc.isEmpty()) throw new NotFoundException(School.class, schoolID);
    }

    public List<Pair<LocalDate, Integer>> getPunctuation(String schoolID) {
        Optional<School> school = schoolDao.findById(schoolID);
        if (school.isEmpty()) throw new NotFoundException(School.class, schoolID);
        return school.get().getRegister();
    }

    public void doRegister(LocalDate date) {
        List<School> schoolList = schoolDao.findAll();
        for (School school : schoolList) {
            int punctuation = userService.countInfectedInSchool(school.getId());
            school.addRegister(Pair.of(date, punctuation));
            schoolDao.save(school);
        }
    }

    public boolean checkEntryCode(String schoolID, String userID, String code) {
        School s = getSchoolByID(schoolID);
        if (s.getEntryCode().equals(code)) {
            userService.existsUser(userID);
            userService.addSchool(userID, schoolID);
            return true;
        }
        else return false;
    }


    public void updateEntryCode() {
        List<School> schoolList = schoolDao.findAll();
        for (School school : schoolList){
            school.setEntryCode(generateEntryCode());
            schoolDao.save(school);
        }
    }

    private String generateEntryCode() {
        int leftLimitUp = 65; // letter 'A'
        int rightLimitUp = 90; // letter 'Z'
        int leftLimitLow = 97; // letter 'a'
        int rightLimitLow = 122; // letter 'z'
        int targetStringLength = 6;
        boolean n = Math.random() < 0.5;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        int randomLimitedInt;
        for (int i = 0; i < targetStringLength; i++) {
            if (n) randomLimitedInt = leftLimitUp + (int)
                    (random.nextFloat() * (rightLimitUp - leftLimitUp + 1));
            else randomLimitedInt = leftLimitLow + (int)
                    (random.nextFloat() * (rightLimitLow - leftLimitLow + 1));
            buffer.append((char) randomLimitedInt);
            n = Math.random() < 0.5;
        }
        return buffer.toString();
    }
}