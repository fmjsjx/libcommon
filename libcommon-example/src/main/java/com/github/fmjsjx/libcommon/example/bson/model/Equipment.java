package com.github.fmjsjx.libcommon.example.bson.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.github.fmjsjx.libcommon.bson.BsonUtil;
import com.github.fmjsjx.libcommon.bson.model.DefaultMapValueModel;
import com.github.fmjsjx.libcommon.util.ObjectUtil;
import com.mongodb.client.model.Updates;

public class Equipment extends DefaultMapValueModel<String, Equipment> {

    public static final Equipment of(Document document) {
        var obj = new Equipment();
        obj.load(document);
        return obj;
    }

    private String id;
    private int refId;
    private int atk;
    private int def;
    private int hp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (ObjectUtil.isNotEquals(this.id, id)) {
            this.id = id;
            updatedFields.set(1);
            emitUpdated();
        }
    }

    public int getRefId() {
        return refId;
    }

    public void setRefId(int refId) {
        if (this.refId != refId) {
            this.refId = refId;
            updatedFields.set(2);
            emitUpdated();
        }
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        if (this.atk != atk) {
            this.atk = atk;
            updatedFields.set(3);
            emitUpdated();
        }
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        if (this.def != def) {
            this.def = def;
            updatedFields.set(4);
            emitUpdated();
        }
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        if (this.hp != hp) {
            this.hp = hp;
            updatedFields.set(5);
            emitUpdated();
        }
    }

    @Override
    public BsonDocument toBson() {
        var bson = new BsonDocument();
        bson.append("id", new BsonString(id));
        bson.append("rid", new BsonInt32(refId));
        bson.append("atk", new BsonInt32(atk));
        bson.append("def", new BsonInt32(def));
        bson.append("hp", new BsonInt32(hp));
        return bson;
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("id", id);
        doc.append("rid", refId);
        doc.append("atk", atk);
        doc.append("def", def);
        doc.append("hp", hp);
        return doc;
    }

    @Override
    public void load(Document src) {
        id = BsonUtil.stringValue(src, "id").get();
        refId = BsonUtil.intValue(src, "rid").getAsInt();
        atk = BsonUtil.intValue(src, "atk").getAsInt();
        def = BsonUtil.intValue(src, "def").getAsInt();
        hp = BsonUtil.intValue(src, "hp").getAsInt();
    }

    @Override
    protected void appendFieldUpdates(List<Bson> updates) {
        var updatedFields = this.updatedFields;
        if (updatedFields.get(1)) {
            updates.add(Updates.set(xpath().resolve("id").value(), id));
        }
        if (updatedFields.get(2)) {
            updates.add(Updates.set(xpath().resolve("rid").value(), refId));
        }
        if (updatedFields.get(3)) {
            updates.add(Updates.set(xpath().resolve("atk").value(), atk));
        }
        if (updatedFields.get(4)) {
            updates.add(Updates.set(xpath().resolve("def").value(), def));
        }
        if (updatedFields.get(5)) {
            updates.add(Updates.set(xpath().resolve("hp").value(), hp));
        }
    }

    @Override
    protected void resetChildren() {
    }

    @Override
    public Object toSubUpdate() {
        var update = new LinkedHashMap<>();
        var updatedFields = this.updatedFields;
        if (updatedFields.get(1)) {
            update.put("id", id);
        }
        if (updatedFields.get(2)) {
            update.put("refId", refId);
        }
        if (updatedFields.get(3)) {
            update.put("atk", atk);
        }
        if (updatedFields.get(4)) {
            update.put("def", def);
        }
        if (updatedFields.get(5)) {
            update.put("hp", hp);
        }
        return update;
    }

    @Override
    public Map<Object, Object> toDelete() {
        return Map.of();
    }

    @Override
    protected int deletedSize() {
        return 0;
    }

}
