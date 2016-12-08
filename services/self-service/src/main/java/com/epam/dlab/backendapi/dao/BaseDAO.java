/***************************************************************************

Copyright (c) 2016, EPAM SYSTEMS INC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

****************************************************************************/

package com.epam.dlab.backendapi.dao;

import com.epam.dlab.backendapi.dao.databind.IsoDateModule;
import com.epam.dlab.client.mongo.MongoService;
import com.epam.dlab.exceptions.DlabException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

class BaseDAO implements MongoCollections {
    protected static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true)
            .registerModule(new IsoDateModule());
    public static final String FIELD_DELIMETER = ".";
    public static final String FIELD_SET_DELIMETER = ".$.";
    public static final String ID = "_id";
    public static final String USER = "user";
    public static final String STATUS = "status";
    public static final String TIMESTAMP = "timestamp";

    @Inject
    protected MongoService mongoService;

    protected void insertOne(String collection, Supplier<Document> supplier) {
        insertOne(collection, supplier, generateUUID());
    }

    protected void insertOne(String collection, Supplier<Document> supplier, String uuid) {
        mongoService.getCollection(collection).insertOne(supplier.get()
                .append(ID, uuid)
                .append(TIMESTAMP, new Date()));
    }

    protected void insertOne(String collection, Object object) {
        insertOne(collection, object, generateUUID());
    }

    protected void insertOne(String collection, Object object, String uuid) {
        mongoService.getCollection(collection).insertOne(convertToBson(object)
                .append(ID, uuid)
                .append(TIMESTAMP, new Date()));
    }

    protected UpdateResult updateOne(String collection, Bson condition, Bson value) {
        return mongoService.getCollection(collection).updateOne(condition, value);
    }

    protected FindIterable<Document> find(String collection,
                                          Bson condition) {
        return mongoService.getCollection(collection)
                .find(condition);
    }

    protected FindIterable<Document> find(String collection,
                                          Bson condition,
                                          Bson projection) {
        return mongoService.getCollection(collection)
                .find(condition)
                .projection(projection);
    }

    private Optional<Document> limitOne(FindIterable<Document> documents) {
        ArrayList<Document> array = documents.limit(2).into(new ArrayList<>());
        if(array.size() > 1) {
            throw new DlabException("too many items found while one is expected");
        }
        return array.isEmpty() ? Optional.empty() : Optional.ofNullable(array.get(0));
    }

    protected Optional<Document> findOne(String collection,
                               Bson condition) {
        FindIterable<Document> found = find(collection, condition);
        return limitOne(found);
    }

    protected Optional<Document> findOne(String collection,
                               Bson condition,
                               Bson projection) {
        FindIterable<Document> found = find(collection, condition, projection);
        return limitOne(found);
    }

    protected Document convertToBson(Object object) {
        try {
            return Document.parse(MAPPER.writeValueAsString(object));
        } catch (IOException e) {
            throw new DlabException("error converting to bson", e);
        }
    }

    protected <T> Optional<T> findOne(String collection, Bson eq, Class<T> clazz) {
        Optional<Document> doc = findOne(collection, eq);
        return doc.isPresent() ? Optional.ofNullable(convertFromDocument(doc.get(), clazz)) : Optional.empty();
    }

    private <T> T convertFromDocument(Document document, Class<T> clazz) {
        try {
            String json = document.toJson();
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new DlabException("error converting from document with id " + document.get(ID), e);
        }
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
