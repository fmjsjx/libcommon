package com.github.fmjsjx.libcommon.example.bson.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.github.fmjsjx.libcommon.bson.BsonUtil;
import com.github.fmjsjx.libcommon.bson.DotNotation;
import com.github.fmjsjx.libcommon.bson.model.ObjectModel;
import com.github.fmjsjx.libcommon.bson.model.SimpleListModel;
import com.github.fmjsjx.libcommon.bson.model.SimpleMapModel;
import com.github.fmjsjx.libcommon.bson.model.SimpleValueTypes;

public class CashInfo extends ObjectModel<CashInfo> {

    private static final DotNotation XPATH = DotNotation.of("cs");

    private final Player parent;

    private final SimpleMapModel<Integer, Integer, CashInfo> stages = SimpleMapModel.integerKeys(this, "stg", SimpleValueTypes.INTEGER);
    private final SimpleListModel<Integer, CashInfo> cards = new SimpleListModel<>(this, "cs", SimpleValueTypes.INTEGER);
    @com.fasterxml.jackson.annotation.JsonIgnore
    private final SimpleListModel<Integer, CashInfo> orderIds = new SimpleListModel<>(this, "ois", SimpleValueTypes.INTEGER);

    public CashInfo(Player parent) {
        this.parent = parent;
    }

    public SimpleMapModel<Integer, Integer, CashInfo> getStages() {
        return stages;
    }

    public SimpleListModel<Integer, CashInfo> getCards() {
        return cards;
    }

    public SimpleListModel<Integer, CashInfo> getOrderIds() {
        return orderIds;
    }

    @Override
    public Player parent() {
        return parent;
    }

    @Override
    public DotNotation xpath() {
        return XPATH;
    }

    @Override
    public boolean updated() {
        return stages.updated() || cards.updated() || orderIds.updated();
    }

    @Override
    public BsonDocument toBson() {
        var bson = new BsonDocument();
        bson.append("stg", stages.toBson());
        if (!cards.nil()) {
            bson.append("cs", cards.toBson());
        }
        if (!orderIds.nil()) {
            bson.append("ois", orderIds.toBson());
        }
        return bson;
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("stg", stages.toDocument());
        cards.values().ifPresent(list -> doc.append("cs", list));
        orderIds.values().ifPresent(list -> doc.append("ois", list));
        return doc;
    }

    @Override
    public void load(Document src) {
        BsonUtil.documentValue(src, "stg").ifPresentOrElse(stages::load, stages::clear);
        BsonUtil.listValue(src, "cs").ifPresentOrElse(cards::load, cards::clear);
        BsonUtil.listValue(src, "ois").ifPresentOrElse(orderIds::load, orderIds::clear);
    }

    @Override
    public void load(BsonDocument src) {
        BsonUtil.documentValue(src, "stg").ifPresentOrElse(stages::load, stages::clear);
        BsonUtil.arrayValue(src, "cs").ifPresentOrElse(cards::load, cards::clear);
        BsonUtil.arrayValue(src, "ois").ifPresentOrElse(orderIds::load, orderIds::clear);
    }

    @Override
    protected void appendFieldUpdates(List<Bson> updates) {
        var stages = this.stages;
        if (stages.updated()) {
            stages.appendUpdates(updates);
        }
        var cards = this.cards;
        if (cards.updated()) {
            cards.appendUpdates(updates);
        }
        var orderIds = this.orderIds;
        if (orderIds.updated()) {
            orderIds.appendUpdates(updates);
        }
    }

    @Override
    protected void resetChildren() {
        stages.reset();
        cards.reset();
        orderIds.reset();
    }

    @Override
    public Object toSubUpdate() {
        var update = new LinkedHashMap<>();
        if (stages.updated()) {
            update.put("stages", stages.toUpdate());
        }
        var cards = this.cards;
        if (cards.updated()) {
            cards.values().ifPresent(values -> update.put("cards", values));
        }
        return update;
    }

    @Override
    public Map<Object, Object> toDelete() {
        var delete = new LinkedHashMap<>();
        var stages = this.stages;
        if (stages.deletedSize() > 0) {
            delete.put("stages", stages.toDelete());
        }
        var cards = this.cards;
        if (cards.deletedSize() > 0) {
            delete.put("cards", 1);
        }
        return delete;
    }

    @Override
    protected int deletedSize() {
        var n = 0;
        if (stages.deletedSize() > 0) {
            n++;
        }
        if (cards.deletedSize() > 0) {
            n++;
        }
        return n;
    }

    @Override
    public String toString() {
        return "CashInfo(" + "stages=" + stages + ", " + "cards=" + cards + ", " + "orderIds=" + orderIds + ")";
    }

}
