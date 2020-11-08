package com.gescov.webserver.dao.user;

import com.gescov.webserver.dao.user.UserCustomizedMongoRepository;
import com.gescov.webserver.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends MongoRepository<User, String>, UserCustomizedMongoRepository<User, String> {

}