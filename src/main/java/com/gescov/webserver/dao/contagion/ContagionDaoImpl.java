package com.gescov.webserver.dao.contagion;

import com.gescov.webserver.dao.user.UserDao;
import com.gescov.webserver.model.Contagion;
import com.gescov.webserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("contagionMongo")
public class ContagionDaoImpl implements ContagionDaoCustom {

    private final MongoTemplate mongoTemplate;
    private final UserDao userDao;

    @Autowired
    public ContagionDaoImpl(MongoTemplate mongoTemplate, UserDao userService) {
        this.mongoTemplate = mongoTemplate;
        this.userDao = userService;
    }

    @Override
    public List<Contagion> findInfectedBySchool(String schoolID) {
        List<User> infected = userDao.findAllBySchoolID(schoolID);
        List<String> infectedID = new ArrayList<>();
        Query q = new Query();
        for (User in : infected) {
            infectedID.add(in.getId());
        }
        q.addCriteria(Criteria.where("infectedID").in(infectedID));
        q.addCriteria(Criteria.where("endContagion").is(null));
        return mongoTemplate.find(q, Contagion.class);
    }

}