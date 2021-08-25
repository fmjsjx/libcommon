package com.github.fmjsjx.libcommon.example.bson.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fmjsjx.libcommon.bson.BsonUtil;
import com.github.fmjsjx.libcommon.bson.DotNotation;
import com.github.fmjsjx.libcommon.bson.model.ObjectModel;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.mongodb.client.model.Updates;

public class Wallet extends ObjectModel<Wallet> {

    public static final String BNAME_COIN_TOTAL = "ct";
    public static final String BNAME_COIN_USED = "cu";
    public static final String BNAME_DIAMOND = "d";
    public static final String BNAME_AD = "ad";

    private static final DotNotation XPATH = DotNotation.of("wt");

    private final Player parent;

    private long coinTotal;
    @JsonIgnore
    private long coinUsed;
    private long diamond;
    private int ad;

    public Wallet(Player parent) {
        this.parent = parent;
    }

    public long getCoinTotal() {
        return coinTotal;
    }

    public void setCoinTotal(long coinTotal) {
        if (this.coinTotal != coinTotal) {
            this.coinTotal = coinTotal;
            updatedFields.set(1);
            updatedFields.set(3);
        }
    }

    public long addCoinTotal(long n) {
        var coinTotal = this.coinTotal += n;
        updatedFields.set(1);
        updatedFields.set(3);
        return coinTotal;
    }

    @JsonIgnore
    public long getCoinUsed() {
        return coinUsed;
    }

    public void setCoinUsed(long coinUsed) {
        if (this.coinUsed != coinUsed) {
            this.coinUsed = coinUsed;
            updatedFields.set(2);
            updatedFields.set(3);
        }
    }

    public long addCoinUsed(long n) {
        var coinUsed = this.coinUsed += n;
        updatedFields.set(2);
        updatedFields.set(3);
        return coinUsed;
    }

    public long getCoin() {
        return coinTotal - coinUsed;
    }

    public long getDiamond() {
        return diamond;
    }

    public void setDiamond(long diamond) {
        if (this.diamond != diamond) {
            this.diamond = diamond;
            updatedFields.set(4);
        }
    }

    public long addDiamond(long n) {
        var diamond = this.diamond += n;
        updatedFields.set(4);
        return diamond;
    }

    public int getAd() {
        return ad;
    }

    public void setAd(int ad) {
        if (this.ad != ad) {
            this.ad = ad;
            updatedFields.set(5);
        }
    }

    public int increaseAd() {
        var ad = this.ad += 1;
        updatedFields.set(5);
        return ad;
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
    public BsonDocument toBson() {
        var bson = new BsonDocument();
        bson.append("ct", new BsonInt64(coinTotal));
        bson.append("cu", new BsonInt64(coinUsed));
        bson.append("d", new BsonInt64(diamond));
        bson.append("ad", new BsonInt32(ad));
        return bson;
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        doc.append("ct", coinTotal);
        doc.append("cu", coinUsed);
        doc.append("d", diamond);
        doc.append("ad", ad);
        return doc;
    }

    @Override
    public Map<String, ?> toData() {
        var data = new LinkedHashMap<String, Object>();
        data.put("ct", coinTotal);
        data.put("cu", coinUsed);
        data.put("d", diamond);
        data.put("ad", ad);
        return data;
    }

    @Override
    public void load(Document src) {
        coinTotal = BsonUtil.longValue(src, "ct").getAsLong();
        coinUsed = BsonUtil.longValue(src, "cu").getAsLong();
        diamond = BsonUtil.longValue(src, "d").getAsLong();
        ad = BsonUtil.intValue(src, "ad").getAsInt();
    }

    @Override
    public void load(BsonDocument src) {
        coinTotal = BsonUtil.longValue(src, "ct").getAsLong();
        coinUsed = BsonUtil.longValue(src, "cu").getAsLong();
        diamond = BsonUtil.longValue(src, "d").getAsLong();
        ad = BsonUtil.intValue(src, "ad").getAsInt();
    }

    @Override
    public void load(Any src) {
        if (src.valueType() != ValueType.OBJECT) {
            reset();
            return;
        }
        coinTotal = BsonUtil.longValue(src, "ct").getAsLong();
        coinUsed = BsonUtil.longValue(src, "cu").getAsLong();
        diamond = BsonUtil.longValue(src, "d").getAsLong();
        ad = BsonUtil.intValue(src, "ad").getAsInt();
    }

    @Override
    public void load(JsonNode src) {
        if (!src.isObject()) {
            reset();
            return;
        }
        coinTotal = BsonUtil.longValue(src, "ct").getAsLong();
        coinUsed = BsonUtil.longValue(src, "cu").getAsLong();
        diamond = BsonUtil.longValue(src, "d").getAsLong();
        ad = BsonUtil.intValue(src, "ad").getAsInt();
    }

    public boolean coinTotalUpdated() {
        return updatedFields.get(1);
    }

    public boolean coinUsedUpdated() {
        return updatedFields.get(2);
    }

    public boolean coinUpdated() {
        return updatedFields.get(3);
    }

    public boolean diamondUpdated() {
        return updatedFields.get(4);
    }

    public boolean adUpdated() {
        return updatedFields.get(5);
    }

    @Override
    protected void appendFieldUpdates(List<Bson> updates) {
        var updatedFields = this.updatedFields;
        if (updatedFields.get(1)) {
            updates.add(Updates.set(xpath().resolve("ct").value(), coinTotal));
        }
        if (updatedFields.get(2)) {
            updates.add(Updates.set(xpath().resolve("cu").value(), coinUsed));
        }
        if (updatedFields.get(4)) {
            updates.add(Updates.set(xpath().resolve("d").value(), diamond));
        }
        if (updatedFields.get(5)) {
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
            update.put("coinTotal", coinTotal);
        }
        if (updatedFields.get(3)) {
            update.put("coin", getCoin());
        }
        if (updatedFields.get(4)) {
            update.put("diamond", diamond);
        }
        if (updatedFields.get(5)) {
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

    public long coin() {
        return coinTotal - coinUsed;
    }

    public LocalDate ago(int days) {
        return LocalDate.now().minusDays(days);
    }

    @JsonIgnore
    public ZonedDateTime testMethodCode(LocalDateTime time, ZoneId zone) {
        var zoned = time.atZone(zone);
        return zoned;
    }

    @Override
    public String toString() {
        return "Wallet(" + "coinTotal=" + coinTotal + ", " + "coinUsed=" + coinUsed + ", " + "diamond=" + diamond + ", " + "ad=" + ad + ")";
    }

}
