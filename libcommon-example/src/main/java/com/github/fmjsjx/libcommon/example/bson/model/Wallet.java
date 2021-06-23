package com.github.fmjsjx.libcommon.example.bson.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.github.fmjsjx.libcommon.bson.BsonUtil;
import com.github.fmjsjx.libcommon.bson.DotNotation;
import com.github.fmjsjx.libcommon.bson.model.ObjectModel;
import com.mongodb.client.model.Updates;

public class Wallet extends ObjectModel<Wallet> {

    private static final DotNotation XPATH = DotNotation.of("wt");

    private final Player parent;

    private long coin;
    private long diamond;
    private int ad;

    public Wallet(Player parent) {
        this.parent = parent;
    }

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
        if (this.coin != coin) {
            this.coin = coin;
            updatedFields.set(1);
        }
    }

    public long getDiamond() {
        return diamond;
    }

    public void setDiamond(long diamond) {
        if (this.diamond != diamond) {
            this.diamond = diamond;
            updatedFields.set(2);
        }
    }

    public int getAd() {
        return ad;
    }

    public void setAd(int ad) {
        if (this.ad != ad) {
            this.ad = ad;
            updatedFields.set(3);
        }
    }

    public int increaseAd() {
        var ad = this.ad += 1;
        updatedFields.set(3);
        return ad;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Player parent() {
        return parent;
    }

    @Override
    public DotNotation xpath() {
        return XPATH;
    }

    @Override
    public BsonDocument toBson() {
        var bson = new BsonDocument();
        bson.append("c", new BsonInt64(coin));
        bson.append("d", new BsonInt64(diamond));
        bson.append("ad", new BsonInt32(ad));
        return bson;
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("c", coin);
        doc.append("d", diamond);
        doc.append("ad", ad);
        return doc;
    }

    @Override
    public void load(Document src) {
        coin = BsonUtil.longValue(src, "c").getAsLong();
        diamond = BsonUtil.longValue(src, "d").getAsLong();
        ad = BsonUtil.intValue(src, "ad").getAsInt();
    }

    @Override
    protected void appendFieldUpdates(List<Bson> updates) {
        var updatedFields = this.updatedFields;
        if (updatedFields.get(1)) {
            updates.add(Updates.set(xpath().resolve("c").value(), coin));
        }
        if (updatedFields.get(2)) {
            updates.add(Updates.set(xpath().resolve("d").value(), diamond));
        }
        if (updatedFields.get(3)) {
            updates.add(Updates.set(xpath().resolve("ad").value(), ad));
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
            update.put("coin", coin);
        }
        if (updatedFields.get(2)) {
            update.put("diamond", diamond);
        }
        if (updatedFields.get(3)) {
            update.put("ad", ad);
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
