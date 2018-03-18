package com.jlu.mcloud.service;

import com.jlu.mcloud.communicate.login.LoginHandler;
import com.jlu.mcloud.manager.ConnectionManager;
import com.jlu.mcloud.utils.Constant;
import com.jlu.mcloud.utils.MD5;
import com.jlu.mcloud.db.mongo.MongoManager;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.currentDate;
import static com.mongodb.client.model.Updates.set;

/**
 * Created by koko on 17-3-22.
 */
public class LoginHandlerImpl implements LoginHandler {
    private ConnectionManager connectionManager = ConnectionManager.getInstance();

    @Override
    public int login(String username, String passwd) throws IOException{
        int ret;
        MongoManager mongoManager = new MongoManager();
        MongoCollection<Document> collection = mongoManager.getDBCollection(Constant.TABLE_USER);
        FindIterable<Document> iterable = collection.find(eq("_id", username));
        MongoCursor<Document> cursor = iterable.iterator();
        if (cursor.hasNext()) {
            Document document = cursor.next();
            if (document.get("password").equals(MD5.toMD5(passwd.getBytes()))) {
                collection.updateMany(
                        eq("_id", username),
                        combine(
                                set("status", true),
                                currentDate("lastModified")
                        )
                );
                connectionManager.putConnectionSet(username);
                ret = LOGIN_SUCESS;
            } else {
                ret = LOGIN_ERROR_PASSWORD;
            }

        } else {
            ret = LOGIN_ERROR_USERNAME;
        }
        return ret;
    }
}
