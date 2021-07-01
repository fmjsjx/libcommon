package com.github.fmjsjx.libcommon.example.bson.model;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.github.fmjsjx.libcommon.bson.BsonUtil;
import com.github.fmjsjx.libcommon.bson.model.DefaultMapModel;
import com.github.fmjsjx.libcommon.bson.model.RootModel;
import com.github.fmjsjx.libcommon.bson.model.SimpleMapModel;
import com.github.fmjsjx.libcommon.bson.model.SimpleValueTypes;
import com.github.fmjsjx.libcommon.util.DateTimeUtil;
import com.github.fmjsjx.libcommon.util.ObjectUtil;
import com.mongodb.client.model.Updates;

public class Player extends RootModel<Player> {

    private int uid;
    private final Wallet wallet = new Wallet(this);
    private final DefaultMapModel<String, Equipment, Player> equipments = DefaultMapModel.stringKeys(this, "eqm", Equipment::new);
    private final SimpleMapModel<Integer, Integer, Player> items = SimpleMapModel.integerKeys(this, "itm", SimpleValueTypes.INTEGER);
    private final CashInfo cash = new CashInfo(this);
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

    public DefaultMapModel<String, Equipment, Player> getEquipments() {
        return equipments;
    }

    public SimpleMapModel<Integer, Integer, Player> getItems() {
        return items;
    }

    public CashInfo getCash() {
        return cash;
    }

    public int getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(int updateVersion) {
        if (this.updateVersion != updateVersion) {
            this.updateVersion = updateVersion;
            updatedFields.set(6);
        }
    }

    public int increaseUpdateVersion() {
        var updateVersion = this.updateVersion += 1;
        updatedFields.set(6);
        return updateVersion;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        if (ObjectUtil.isNotEquals(this.createTime, createTime)) {
            this.createTime = createTime;
            updatedFields.set(7);
        }
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        if (ObjectUtil.isNotEquals(this.updateTime, updateTime)) {
            this.updateTime = updateTime;
            updatedFields.set(8);
        }
    }

    @Override
    public boolean updated() {
        if (wallet.updated() || equipments.updated() || items.updated() || cash.updated()) {
            return true;
        }
        return super.updated();
    }

    @Override
    public BsonDocument toBson() {
        var bson = new BsonDocument();
        bson.append("_id", new BsonInt32(uid));
        bson.append("wt", wallet.toBson());
        bson.append("eqm", equipments.toBson());
        bson.append("itm", items.toBson());
        bson.append("cs", cash.toBson());
        bson.append("_uv", new BsonInt32(updateVersion));
        bson.append("_ct", BsonUtil.toBsonDateTime(createTime));
        if (updateTime != null) {
            bson.append("_ut", BsonUtil.toBsonDateTime(updateTime));
        }
        return bson;
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("_id", uid);
        doc.append("wt", wallet.toDocument());
        doc.append("eqm", equipments.toDocument());
        doc.append("itm", items.toDocument());
        doc.append("cs", cash.toDocument());
        doc.append("_uv", updateVersion);
        doc.append("_ct", DateTimeUtil.toLegacyDate(createTime));
        if (updateTime != null) {
            doc.append("_ut", DateTimeUtil.toLegacyDate(updateTime));
        }
        return doc;
    }

    @Override
    public void load(Document src) {
        uid = BsonUtil.intValue(src, "_id").getAsInt();
        wallet.load(BsonUtil.documentValue(src, "wt").orElseGet(Document::new));
        BsonUtil.documentValue(src, "eqm").ifPresentOrElse(equipments::load, equipments::clear);
        BsonUtil.documentValue(src, "itm").ifPresentOrElse(items::load, items::clear);
        cash.load(BsonUtil.documentValue(src, "cs").orElseGet(Document::new));
        updateVersion = BsonUtil.intValue(src, "_uv").orElse(0);
        createTime = BsonUtil.dateTimeValue(src, "_ct").get();
        updateTime = BsonUtil.dateTimeValue(src, "_ut").orElseGet(LocalDateTime::now);
        reset();
    }

    @Override
    public void load(BsonDocument src) {
        uid = BsonUtil.intValue(src, "_id").getAsInt();
        wallet.load(BsonUtil.documentValue(src, "wt").orElseGet(BsonDocument::new));
        BsonUtil.documentValue(src, "eqm").ifPresentOrElse(equipments::load, equipments::clear);
        BsonUtil.documentValue(src, "itm").ifPresentOrElse(items::load, items::clear);
        cash.load(BsonUtil.documentValue(src, "cs").orElseGet(BsonDocument::new));
        updateVersion = BsonUtil.intValue(src, "_uv").orElse(0);
        createTime = BsonUtil.dateTimeValue(src, "_ct").get();
        updateTime = BsonUtil.dateTimeValue(src, "_ut").orElseGet(LocalDateTime::now);
        reset();
    }

    @Override
    protected void appendFieldUpdates(List<Bson> updates) {
        var updatedFields = this.updatedFields;
        if (updatedFields.get(1)) {
            updates.add(Updates.set("_id", uid));
        }
        var wallet = this.wallet;
        if (wallet.updated()) {
            wallet.appendUpdates(updates);
        }
        var equipments = this.equipments;
        if (equipments.updated()) {
            equipments.appendUpdates(updates);
        }
        var items = this.items;
        if (items.updated()) {
            items.appendUpdates(updates);
        }
        var cash = this.cash;
        if (cash.updated()) {
            cash.appendUpdates(updates);
        }
        if (updatedFields.get(6)) {
            updates.add(Updates.set("_uv", updateVersion));
        }
        if (updatedFields.get(7)) {
            updates.add(Updates.set("_ct", BsonUtil.toBsonDateTime(createTime)));
        }
        if (updatedFields.get(8)) {
            updates.add(Updates.set("_ut", BsonUtil.toBsonDateTime(updateTime)));
        }
    }

    @Override
    protected void resetChildren() {
        wallet.reset();
        equipments.reset();
        items.reset();
        cash.reset();
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
        if (equipments.updated()) {
            update.put("equipments", equipments.toUpdate());
        }
        if (items.updated()) {
            update.put("items", items.toUpdate());
        }
        if (cash.updated()) {
            update.put("cash", cash.toUpdate());
        }
        return update;
    }

    @Override
    public Map<Object, Object> toDelete() {
        var delete = new LinkedHashMap<>();
        var wallet = this.wallet;
        if (wallet.deletedSize() > 0) {
            delete.put("wallet", wallet.toDelete());
        }
        var equipments = this.equipments;
        if (equipments.deletedSize() > 0) {
            delete.put("equipments", equipments.toDelete());
        }
        var items = this.items;
        if (items.deletedSize() > 0) {
            delete.put("items", items.toDelete());
        }
        var cash = this.cash;
        if (cash.deletedSize() > 0) {
            delete.put("cash", cash.toDelete());
        }
        return delete;
    }

    @Override
    protected int deletedSize() {
        var n = 0;
        if (wallet.deletedSize() > 0) {
            n++;
        }
        if (equipments.deletedSize() > 0) {
            n++;
        }
        if (items.deletedSize() > 0) {
            n++;
        }
        if (cash.deletedSize() > 0) {
            n++;
        }
        return n;
    }

}
