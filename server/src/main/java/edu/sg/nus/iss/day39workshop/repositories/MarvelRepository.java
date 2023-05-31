package edu.sg.nus.iss.day39workshop.repositories;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import edu.sg.nus.iss.day39workshop.model.MarvelCharacter;
import software.amazon.ion.Timestamp;

@Repository
public class MarvelRepository {

    @Autowired
    RedisTemplate<String, String> template;

    @Autowired
    private MongoTemplate mongoTemplate;

    public int saveCharacter(final MarvelCharacter m) {
        String mIdString = String.valueOf(m.getId());
        template.opsForValue().set(mIdString, m.toJSON().toString());
        template.expire(mIdString, 1L, TimeUnit.HOURS);
        System.out.println("key: " + mIdString + " expiring in " + template.getExpire(mIdString));
        String result = (String) template.opsForValue().get(mIdString);
        if (result != null) {
            return 1;
        }
        return 0;
    }

    public MarvelCharacter getCharacterById(String mId) throws IOException {
        String jsonStrVal = (String) template.opsForValue().get(mId);
        MarvelCharacter m = MarvelCharacter.create(jsonStrVal);
        return m;
    }

    public List<String> getComments(String mId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(mId));
        query.with(Sort.by(Sort.Direction.DESC,"ts"));
        query.limit(10);

        return mongoTemplate.find(query, Document.class, "comments").stream().map(d -> d.getString("comment")).toList();
    }

    public void insertComment(String mId, String comment) {
        Document d = new Document();
        d.append("id", mId);
        d.append("comment", comment);
        d.append("ts", Timestamp.now().toString());

        mongoTemplate.insert(d, "comments");
    }
}
