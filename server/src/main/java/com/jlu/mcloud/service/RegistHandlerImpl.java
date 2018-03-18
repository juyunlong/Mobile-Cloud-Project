package com.jlu.mcloud.service;

import com.jlu.mcloud.communicate.regist.RegistHandler;
import com.jlu.mcloud.utils.Constant;
import com.jlu.mcloud.utils.MD5;
import com.jlu.mcloud.db.mongo.MongoManager;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by koko on 17-3-22.
 */
public class RegistHandlerImpl implements RegistHandler {
    @Override
    public int regist(String username, String passwd) throws IOException{
        if (!checkUsernameAvial(username)) {

            return REGIST_ERROR_INVALID_USERNAME;

        } else {

            MongoManager manager = new MongoManager();
            MongoCollection<Document> collection = manager.getDBCollection(Constant.TABLE_USER);
            FindIterable<Document> iterable = collection.find(eq("_id", username));
            if (iterable.iterator().hasNext()) {
                return REGIST_ERROR_ALLREADY_REGIST;
            }
            Document document = new Document();
            document.append("_id", username)
                    .append("password", MD5.toMD5(passwd.getBytes()))
                    .append("status", false);

            collection.insertOne(document);
            return REGIST_SUCCESS;
        }
    }

    private boolean checkUsernameAvial(String username) {
        // TODO: 可以检查用户名的合法性
        return true;
    }
}
