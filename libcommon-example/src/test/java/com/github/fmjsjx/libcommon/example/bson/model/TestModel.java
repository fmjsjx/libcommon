package com.github.fmjsjx.libcommon.example.bson.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.bson.BsonDateTime;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

import com.github.fmjsjx.libcommon.json.Jackson2Library;
import com.github.fmjsjx.libcommon.json.JsoniterLibrary;
import com.mongodb.client.model.Updates;

public class TestModel {

    @Test
    public void testToBson() {
        try {
            var player = new Player();
            player.setUid(123);
            player.getWallet().setCoin(5000);
            player.getWallet().setDiamond(10);
            player.getItems().put(2001, 5);
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setCreateTime(now);
            player.setUpdateTime(now);
            var bson = player.toBson();
            assertNotNull(bson);
            assertEquals(123, bson.getInt32("_id").intValue());
            assertEquals(5000, bson.getDocument("wt").getInt32("c").intValue());
            assertEquals(10, bson.getDocument("wt").getInt32("d").intValue());
            assertEquals(0, bson.getDocument("wt").getInt32("ad").intValue());
            assertEquals(5, bson.getDocument("itm").getInt32("2001").intValue());
            assertEquals(0, bson.getInt32("_uv").intValue());
            var zone = ZoneId.systemDefault();
            assertEquals(now, LocalDateTime.ofInstant(Instant.ofEpochMilli(bson.getDateTime("_ct").getValue()), zone));
            assertEquals(now, LocalDateTime.ofInstant(Instant.ofEpochMilli(bson.getDateTime("_ut").getValue()), zone));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testToJson() {
        try {
            var player = new Player();
            player.setUid(123);
            player.getWallet().setCoin(5000);
            player.getWallet().setDiamond(10);
            player.getItems().put(2001, 5);
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setCreateTime(now);
            player.setUpdateTime(now);
            var json = Jackson2Library.getInstance().dumpsToString(player);
            var any = JsoniterLibrary.getInstance().loads(json);
            assertEquals(123, any.toInt("uid"));
            assertEquals(5000, any.toInt("wallet", "coin"));
            assertEquals(10, any.toInt("wallet", "diamond"));
            assertEquals(0, any.toInt("wallet", "ad"));
            assertEquals(5, any.toInt("items", "2001"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testAppendUpdates() {
        try {
            var player = new Player();
            player.setUid(123);
            player.getWallet().setCoin(5000);
            player.getWallet().setDiamond(10);
            player.getItems().put(2001, 5);
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setCreateTime(now);
            player.setUpdateTime(now);

            player.reset();

            player.getWallet().setCoin(5200);
            player.getWallet().increaseAd();
            player.getItems().put(2002, 1);
            player.getItems().remove(2001);
            now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setUpdateTime(now);
            player.increaseUpdateVersion();

            var updates = new ArrayList<Bson>();
            var n = player.appendUpdates(updates);
            assertTrue(n > 0);
            assertEquals(n, updates.size());
            assertEquals(Updates.set("wt.c", 5200), updates.get(0));
            assertEquals(Updates.set("wt.ad", 1), updates.get(1));
            assertEquals(Updates.set("itm.2002", 1), updates.get(2));
            assertEquals(Updates.unset("itm.2001"), updates.get(3));
            assertEquals(Updates.set("_uv", 1), updates.get(4));
            var zone = ZoneId.systemDefault();
            var _ut = new BsonDateTime(now.atZone(zone).toInstant().toEpochMilli());
            assertEquals(Updates.set("_ut", _ut), updates.get(5));

            player.reset();
            var updates2 = new ArrayList<Bson>();
            var n2 = player.appendUpdates(updates2);
            assertEquals(0, n2);
            assertTrue(updates2.isEmpty());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testToUpdate() {
        try {
            var player = new Player();
            player.setUid(123);
            player.getWallet().setCoin(5000);
            player.getWallet().setDiamond(10);
            player.getItems().put(2001, 5);
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setCreateTime(now);
            player.setUpdateTime(now);

            player.reset();

            player.getWallet().setCoin(5200);
            player.getWallet().increaseAd();
            player.getItems().put(2002, 1);
            player.getItems().remove(2001);
            now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setUpdateTime(now);
            player.increaseUpdateVersion();

            var update = player.toUpdate();
            assertNotNull(update);
            var json = Jackson2Library.getInstance().dumpsToString(update);
            var any = JsoniterLibrary.getInstance().loads(json);
            assertEquals(2, any.asMap().size());
            assertEquals(2, any.get("wallet").asMap().size());
            assertEquals(1, any.get("items").asMap().size());
            assertEquals(5200, any.toInt("wallet", "coin"));
            assertEquals(1, any.toInt("wallet", "ad"));
            assertEquals(1, any.toInt("items", "2002"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testToDelete() {
        try {
            var player = new Player();
            player.setUid(123);
            player.getWallet().setCoin(5000);
            player.getWallet().setDiamond(10);
            player.getItems().put(2001, 5);
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setCreateTime(now);
            player.setUpdateTime(now);

            player.reset();

            player.getWallet().setCoin(5200);
            player.getWallet().increaseAd();
            player.getItems().put(2002, 1);
            player.getItems().remove(2001);
            now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setUpdateTime(now);
            player.increaseUpdateVersion();

            var delete = player.toDelete();
            assertNotNull(delete);
            var json = Jackson2Library.getInstance().dumpsToString(delete);
            var any = JsoniterLibrary.getInstance().loads(json);
            assertEquals(1, any.asMap().size());
            assertEquals(1, any.get("items").asMap().size());
            assertEquals(1, any.toInt("items", "2001"));
        } catch (Exception e) {
            fail(e);
        }
    }

}
