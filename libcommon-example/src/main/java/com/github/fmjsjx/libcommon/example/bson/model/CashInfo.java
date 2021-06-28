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
import com.github.fmjsjx.libcommon.bson.model.SimpleMapModel;
import com.github.fmjsjx.libcommon.bson.model.SimpleMapValueTypes;

public class CashInfo extends ObjectModel<CashInfo> {

    private static final DotNotation XPATH = DotNotation.of("cs");

    private final Player parent;

    private final SimpleMapModel<Integer, Integer, CashInfo> stages = SimpleMapModel.integerKeys(this, "stg", SimpleMapValueTypes.INTEGER);

    public CashInfo(Player parent) {
        this.parent = parent;
    }

    public SimpleMapModel<Integer, Integer, CashInfo> getStages() {
        return stages;
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
        return stages.updated();
    }

    @Override
    public BsonDocument toBson() {
        var bson = new BsonDocument();
        bson.append("stg", stages.toBson());
        return bson;
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("stg", stages.toDocument());
        return doc;
    }

    @Override
    public void load(Document src) {
        BsonUtil.documentValue(src, "stg").ifPresentOrElse(stages::load, stages::clear);
    }

    @Override
    public void load(BsonDocument src) {
        BsonUtil.documentValue(src, "stg").ifPresentOrElse(stages::load, stages::clear);
    }

    @Override
    protected void appendFieldUpdates(List<Bson> updates) {
        var stages = this.stages;
        if (stages.updated()) {
            stages.appendUpdates(updates);
        }
    }

    @Override
    protected void resetChildren() {
        stages.reset();
    }

    @Override
    public Object toSubUpdate() {
        var update = new LinkedHashMap<>();
        if (stages.updated()) {
            update.put("stages", stages.toUpdate());
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
        return delete;
    }

    @Override
    protected int deletedSize() {
        var n = 0;
        if (stages.deletedSize() > 0) {
            n++;
        }
        return n;
    }

}
