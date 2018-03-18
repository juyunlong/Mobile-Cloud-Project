package com.jlu.mcloud.db.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by koko on 17-3-18.
 */
public class Mongotest {
    public static void main(String[] args) {
        String hosts = "127.0.0.1";
        int port = 27017;
        MongoConfig.setHost(hosts);
        MongoConfig.setPort(port);
        MongoConfig.setDbName("novel");
        MongoManager mongoManager = new MongoManager();
        MongoCollection<Document> collection = mongoManager.getDBCollection("chapter");
        Map<String, Object> doc = new HashMap<String, Object>();
        doc.put("_id", "cusId");
        doc.put("name", "xiaoxiannv");
        doc.put("age", 19);
        Document document = new Document(doc);

        FindIterable<Document> iterable = collection.find(Filters.eq("_id", "cusId"));
        if (iterable.iterator().hasNext()) {
            collection.updateOne(Filters.eq("_id", "cusId"), new Document("$set", document));

        } else {
            collection.insertOne(document);
        }
        //Document document = new Document("title", "lalala");
        //collection.replaceOne(Filters.eq("title", "小仙女"), document);


    }
}
