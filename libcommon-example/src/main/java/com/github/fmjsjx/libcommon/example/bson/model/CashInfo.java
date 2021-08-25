package com.github.fmjsjx.libcommon.example.bson.model;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fmjsjx.libcommon.bson.BsonUtil;
import com.github.fmjsjx.libcommon.bson.DotNotation;
import com.github.fmjsjx.libcommon.bson.model.ObjectModel;
import com.github.fmjsjx.libcommon.bson.model.SimpleListModel;
import com.github.fmjsjx.libcommon.bson.model.SimpleMapModel;
import com.github.fmjsjx.libcommon.bson.model.SimpleValueTypes;
import com.github.fmjsjx.libcommon.util.DateTimeUtil;
import com.github.fmjsjx.libcommon.util.ObjectUtil;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.mongodb.client.model.Updates;

public class CashInfo extends ObjectModel<CashInfo> {

    public static final String BNAME_STAGES = "stg";
    public static final String BNAME_CARDS = "cs";
    public static final String BNAME_ORDER_IDS = "ois";
    public static final String BNAME_TEST_DATE = "tsd";
    public static final String BNAME_TEST_DATE_MAP = "tdm";

    private static final DotNotation XPATH = DotNotation.of("cs");

    private final Player parent;

    private final SimpleMapModel<Integer, Integer, CashInfo> stages = SimpleMapModel.integerKeys(this, "stg", SimpleValueTypes.INTEGER);
    private final SimpleListModel<Integer, CashInfo> cards = new SimpleListModel<>(this, "cs", SimpleValueTypes.INTEGER);
    @JsonIgnore
    private final SimpleListModel<Integer, CashInfo> orderIds = new SimpleListModel<>(this, "ois", SimpleValueTypes.INTEGER);
    @JsonIgnore
    private LocalDate testDate;
    @JsonIgnore
    private final SimpleMapModel<Integer, LocalDate, CashInfo> testDateMap = SimpleMapModel.integerKeys(this, "tdm", SimpleValueTypes.DATE);

    public CashInfo(Player parent) {
        this.parent = parent;
    }

    public SimpleMapModel<Integer, Integer, CashInfo> getStages() {
        return stages;
    }

    public SimpleListModel<Integer, CashInfo> getCards() {
        return cards;
    }

    @JsonIgnore
    public SimpleListModel<Integer, CashInfo> getOrderIds() {
        return orderIds;
    }

    @JsonIgnore
    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        if (ObjectUtil.isNotEquals(this.testDate, testDate)) {
            this.testDate = testDate;
            updatedFields.set(4);
        }
    }

    @JsonIgnore
    public SimpleMapModel<Integer, LocalDate, CashInfo> getTestDateMap() {
        return testDateMap;
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
        if (stages.updated() || cards.updated() || orderIds.updated() || testDateMap.updated()) {
            return true;
        }
        return super.updated();
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
        if (testDate != null) {
            bson.append("tsd", new BsonInt32(DateTimeUtil.toNumber(testDate)));
        }
        bson.append("tdm", testDateMap.toBson());
        return bson;
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("stg", stages.toDocument());
        cards.values().ifPresent(list -> doc.append("cs", list));
        orderIds.values().ifPresent(list -> doc.append("ois", list));
        if (testDate != null) {
            doc.append("tsd", DateTimeUtil.toNumber(testDate));
        }
        doc.append("tdm", testDateMap.toDocument());
        return doc;
    }

    @Override
    public Map<String, ?> toData() {
        var data = new LinkedHashMap<String, Object>();
        data.put("stg", stages.toData());
        if (!cards.nil()) {
            data.put("cs", cards.toData());
        }
        if (!orderIds.nil()) {
            data.put("ois", orderIds.toData());
        }
        if (testDate != null) {
            data.put("tsd", DateTimeUtil.toNumber(testDate));
        }
        data.put("tdm", testDateMap.toData());
        return data;
    }

    @Override
    public void load(Document src) {
        BsonUtil.documentValue(src, "stg").ifPresentOrElse(stages::load, stages::clear);
        BsonUtil.listValue(src, "cs").ifPresentOrElse(cards::load, cards::clear);
        BsonUtil.listValue(src, "ois").ifPresentOrElse(orderIds::load, orderIds::clear);
        var testDateOptionalInt = BsonUtil.intValue(src, "tsd");
        testDate = testDateOptionalInt.isEmpty() ? null : DateTimeUtil.toDate(testDateOptionalInt.getAsInt());
        BsonUtil.documentValue(src, "tdm").ifPresentOrElse(testDateMap::load, testDateMap::clear);
    }

    @Override
    public void load(BsonDocument src) {
        BsonUtil.documentValue(src, "stg").ifPresentOrElse(stages::load, stages::clear);
        BsonUtil.arrayValue(src, "cs").ifPresentOrElse(cards::load, cards::clear);
        BsonUtil.arrayValue(src, "ois").ifPresentOrElse(orderIds::load, orderIds::clear);
        var testDateOptionalInt = BsonUtil.intValue(src, "tsd");
        testDate = testDateOptionalInt.isEmpty() ? null : DateTimeUtil.toDate(testDateOptionalInt.getAsInt());
        BsonUtil.documentValue(src, "tdm").ifPresentOrElse(testDateMap::load, testDateMap::clear);
    }

    @Override
    public void load(Any src) {
        if (src.valueType() != ValueType.OBJECT) {
            reset();
            return;
        }
        BsonUtil.objectValue(src, "stg").ifPresentOrElse(stages::load, stages::clear);
        BsonUtil.arrayValue(src, "cs").ifPresentOrElse(cards::load, cards::clear);
        BsonUtil.arrayValue(src, "ois").ifPresentOrElse(orderIds::load, orderIds::clear);
        var testDateOptionalInt = BsonUtil.intValue(src, "tsd");
        testDate = testDateOptionalInt.isEmpty() ? null : DateTimeUtil.toDate(testDateOptionalInt.getAsInt());
        BsonUtil.objectValue(src, "tdm").ifPresentOrElse(testDateMap::load, testDateMap::clear);
    }

    @Override
    public void load(JsonNode src) {
        if (!src.isObject()) {
            reset();
            return;
        }
        BsonUtil.objectValue(src, "stg").ifPresentOrElse(stages::load, stages::clear);
        BsonUtil.arrayValue(src, "cs").ifPresentOrElse(cards::load, cards::clear);
        BsonUtil.arrayValue(src, "ois").ifPresentOrElse(orderIds::load, orderIds::clear);
        var testDateOptionalInt = BsonUtil.intValue(src, "tsd");
        testDate = testDateOptionalInt.isEmpty() ? null : DateTimeUtil.toDate(testDateOptionalInt.getAsInt());
        BsonUtil.objectValue(src, "tdm").ifPresentOrElse(testDateMap::load, testDateMap::clear);
    }

    public boolean stagesUpdated() {
        return stages.updated();
    }

    public boolean cardsUpdated() {
        return cards.updated();
    }

    public boolean orderIdsUpdated() {
        return orderIds.updated();
    }

    public boolean testDateUpdated() {
        return updatedFields.get(4);
    }

    public boolean testDateMapUpdated() {
        return testDateMap.updated();
    }

    @Override
    protected void appendFieldUpdates(List<Bson> updates) {
        var updatedFields = this.updatedFields;
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
        if (updatedFields.get(4)) {
            updates.add(Updates.set(xpath().resolve("tsd").value(), DateTimeUtil.toNumber(testDate)));
        }
        var testDateMap = this.testDateMap;
        if (testDateMap.updated()) {
            testDateMap.appendUpdates(updates);
        }
    }

    @Override
    protected void resetChildren() {
        stages.reset();
        cards.reset();
        orderIds.reset();
        testDateMap.reset();
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
        return "CashInfo(" + "stages=" + stages + ", " + "cards=" + cards + ", " + "orderIds=" + orderIds + ", " + "testDate=" + testDate + ", " + "testDateMap=" + testDateMap + ")";
    }

}
