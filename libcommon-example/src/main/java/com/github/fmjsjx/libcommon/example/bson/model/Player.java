package com.github.fmjsjx.libcommon.example.bson.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.github.fmjsjx.libcommon.bson.BsonUtil;
import com.github.fmjsjx.libcommon.bson.model.RootModel;
import com.github.fmjsjx.libcommon.bson.model.SimpleMapModel;
import com.mongodb.client.model.Updates;

public class Player extends RootModel<Player> {

    private int uid;
    private final Wallet wallet = new Wallet(this);
    private final SimpleMapModel<Integer, Integer, Player> items = SimpleMapModel.integerKeys(this, "itm",
            BsonInt32::new);
    @com.fasterxml.jackson.annotation.JsonIgnore
    private int updateVersion;
    @com.fasterxml.jackson.annotation.JsonIgnore
    private LocalDateTime createTime;
    @com.fasterxml.jackson.annotation.JsonIgnore
    private LocalDateTime updateTime;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        if (this.uid != uid) {
            this.uid = uid;
            updatedFields.set(1);
        }
    }

    public Wallet getWallet() {
        return wallet;
    }

    public SimpleMapModel<Integer, Integer, Player> getItems() {
        return items;
    }

    public int getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(int updateVersion) {
        if (this.updateVersion != updateVersion) {
            this.updateVersion = updateVersion;
            updatedFields.set(4);
        }
    }

    public int increaseUpdateVersion() {
        var updateVersion = this.updateVersion += 1;
        updatedFields.set(4);
        return updateVersion;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        if (this.createTime != createTime) {
            this.createTime = createTime;
            updatedFields.set(5);
        }
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        if (this.updateTime != updateTime) {
            this.updateTime = updateTime;
            updatedFields.set(6);
        }
    }

    @Override
    public BsonDocument toBson() {
        var bson = new BsonDocument();
        bson.append("_id", new BsonInt32(uid));
        bson.append("wt", wallet.toBson());
        bson.append("itm", items.toBson());
        bson.append("_uv", new BsonInt32(updateVersion));
        bson.append("_ct", BsonUtil.toBsonDateTime(createTime));
        bson.append("_ut", BsonUtil.toBsonDateTime(updateTime));
        return bson;
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("_id", uid);
        doc.append("wt", wallet.toDocument());
        doc.append("itm", items.toDocument());
        doc.append("_uv", updateVersion);
        doc.append("_ct", Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant()));
        doc.append("_ut", Date.from(updateTime.atZone(ZoneId.systemDefault()).toInstant()));
        return doc;
    }

    @Override
    public void load(Document src) {
        uid = BsonUtil.intValue(src, "_id").orElse(0);
        BsonUtil.documentValue(src, "wt").ifPresent(wallet::load);
        BsonUtil.documentValue(src, "itm").ifPresent(items::load);
        updateVersion = BsonUtil.intValue(src, "_uv").orElse(0);
        createTime = BsonUtil.dateTimeValue(src, "_ct").orElse(null);
        updateTime = BsonUtil.dateTimeValue(src, "_ut").orElse(null);
    }

    @Override
    protected void appendFieldUpdates(List<Bson> updates) {
        var updatedFields = this.updatedFields;
        if (updatedFields.get(1)) {
            updates.add(Updates.set("_id", uid));
        }
        if (wallet.updated()) {
            wallet.appendUpdates(updates);
        }
        if (items.updated()) {
            items.appendUpdates(updates);
        }
        if (updatedFields.get(4)) {
            updates.add(Updates.set("_uv", updateVersion));
        }
        if (updatedFields.get(5)) {
            updates.add(Updates.set("_ct", BsonUtil.toBsonDateTime(createTime)));
        }
        if (updatedFields.get(6)) {
            updates.add(Updates.set("_ut", BsonUtil.toBsonDateTime(updateTime)));
        }
    }

    @Override
    protected void resetChildren() {
        wallet.reset();
        items.reset();
    }

    @Override
    public Object toSubUpdate() {
        var update = new LinkedHashMap<>();
        var updatedFields = this.updatedFields;
        if (updatedFields.get(1)) {
            update.put("uid", uid);
        }
        if (wallet.updated()) {
            update.put("wallet", wallet.toUpdate());
        }
        if (items.updated()) {
            update.put("items", items.toUpdate());
        }
        return update;
    }

    @Override
    public Object toDelete() {
        var delete = new LinkedHashMap<>();
        if (items.deletedSize() > 0) {
            delete.put("items", items.toDelete());
        }
        return delete;
    }

}