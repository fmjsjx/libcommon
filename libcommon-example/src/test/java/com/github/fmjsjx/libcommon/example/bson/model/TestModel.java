package com.github.fmjsjx.libcommon.example.bson.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

import com.github.fmjsjx.libcommon.bson.BsonUtil;
import com.github.fmjsjx.libcommon.json.Jackson2Library;
import com.github.fmjsjx.libcommon.json.JsoniterLibrary;
import com.mongodb.client.model.Updates;

public class TestModel {

    @Test
    public void testToBson() {
        try {
            var player = new Player();
            player.setUid(123);
            player.getWallet().setCoinTotal(5000);
            player.getWallet().setCoinUsed(200);
            player.getWallet().setDiamond(10);
            var equipment = new Equipment();
            equipment.setId("12345678-1234-5678-9abc-123456789abc");
            equipment.setRefId(1);
            equipment.setAtk(10);
            equipment.setDef(0);
            equipment.setHp(0);
            player.getEquipments().put("12345678-1234-5678-9abc-123456789abc", equipment);
            player.getItems().put(2001, 5);
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setCreateTime(now);
            player.setUpdateTime(now);
            var bson = player.toBson();
            assertNotNull(bson);
            assertEquals(123, bson.getInt32("_id").intValue());
            assertEquals(5000, bson.getDocument("wt").getInt64("ct").intValue());
            assertEquals(200, bson.getDocument("wt").getInt64("cu").intValue());
            assertEquals(10, bson.getDocument("wt").getInt64("d").intValue());
            assertEquals(0, bson.getDocument("wt").getInt32("ad").intValue());
            var eqb = bson.getDocument("eqm").getDocument("12345678-1234-5678-9abc-123456789abc");
            assertNotNull(eqb);
            assertEquals("12345678-1234-5678-9abc-123456789abc", eqb.getString("id").getValue());
            assertEquals(1, eqb.getInt32("rid").getValue());
            assertEquals(10, eqb.getInt32("atk").getValue());
            assertEquals(0, eqb.getInt32("def").getValue());
            assertEquals(0, eqb.getInt32("hp").getValue());
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
    public void testToDocument() {
        try {
            var player = new Player();
            player.setUid(123);
            player.getWallet().setCoinTotal(5000);
            player.getWallet().setCoinUsed(200);
            player.getWallet().setDiamond(10);
            var equipment = new Equipment();
            equipment.setId("12345678-1234-5678-9abc-123456789abc");
            equipment.setRefId(1);
            equipment.setAtk(10);
            equipment.setDef(0);
            equipment.setHp(0);
            player.getEquipments().put("12345678-1234-5678-9abc-123456789abc", equipment);
            player.getItems().put(2001, 5);
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setCreateTime(now);
            player.setUpdateTime(now);

            var doc = player.toDocument();

            assertNotNull(doc);
            assertEquals(123, BsonUtil.intValue(doc, "_id").getAsInt());
            assertEquals(5000, BsonUtil.embeddedInt(doc, "wt", "ct").getAsInt());
            assertEquals(200, BsonUtil.embeddedInt(doc, "wt", "cu").getAsInt());
            assertEquals(10, BsonUtil.embeddedInt(doc, "wt", "d").getAsInt());
            assertEquals(0, BsonUtil.embeddedInt(doc, "wt", "ad").getAsInt());
            var eqb = BsonUtil.embeddedDocument(doc, "eqm", "12345678-1234-5678-9abc-123456789abc").get();
            assertNotNull(eqb);
            assertEquals("12345678-1234-5678-9abc-123456789abc", eqb.getString("id"));
            assertEquals(1, BsonUtil.intValue(eqb, "rid").getAsInt());
            assertEquals(10, BsonUtil.intValue(eqb, "atk").getAsInt());
            assertEquals(0, BsonUtil.intValue(eqb, "def").getAsInt());
            assertEquals(0, BsonUtil.intValue(eqb, "hp").getAsInt());
            assertEquals(5, BsonUtil.embeddedInt(doc, "itm", "2001").getAsInt());
            assertEquals(0, BsonUtil.intValue(doc, "_uv").getAsInt());
            var zone = ZoneId.systemDefault();
            assertEquals(now, LocalDateTime.ofInstant(doc.getDate("_ct").toInstant(), zone));
            assertEquals(now, LocalDateTime.ofInstant(doc.getDate("_ut").toInstant(), zone));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testLoad() {
        try {
            var date = new Date();
            var doc = new Document().append("_id", 123) // uid
                    .append("wt", new Document("ct", 5000).append("cu", 200).append("d", 10).append("ad", 2)) // wallet
                    .append("eqm", // equipments
                            new Document("12345678-1234-5678-9abc-123456789abc",
                                    new Document("id", "12345678-1234-5678-9abc-123456789abc").append("rid", 1)
                                            .append("atk", 12).append("def", 2).append("hp", 100)))
                    .append("itm", new Document("2001", 10)) // items
                    .append("_uv", 1) // update version
                    .append("_ct", date) // create time
                    .append("_ut", date); // update time
            var player = new Player();
            player.load(doc);

            assertFalse(player.updated());
            assertEquals(123, player.getUid());
            assertEquals(player, player.getWallet().parent());
            assertEquals(5000, player.getWallet().getCoinTotal());
            assertEquals(200, player.getWallet().getCoinUsed());
            assertEquals(4800, player.getWallet().getCoin());
            assertEquals(10, player.getWallet().getDiamond());
            assertEquals(2, player.getWallet().getAd());
            assertEquals(1, player.getEquipments().size());
            assertEquals(player, player.getEquipments().parent());
            var eq = player.getEquipments().get("12345678-1234-5678-9abc-123456789abc");
            assertTrue(eq.isPresent());
            assertEquals(player.getEquipments(), eq.get().parent());
            assertEquals("12345678-1234-5678-9abc-123456789abc", eq.get().key());
            assertEquals("12345678-1234-5678-9abc-123456789abc", eq.get().getId());
            assertEquals(1, eq.get().getRefId());
            assertEquals(12, eq.get().getAtk());
            assertEquals(2, eq.get().getDef());
            assertEquals(100, eq.get().getHp());
            assertEquals(1, player.getItems().size());
            assertEquals(player, player.getItems().parent());
            assertEquals(10, player.getItems().get(2001).get());
            assertEquals(1, player.getUpdateVersion());

            var zone = ZoneId.systemDefault();
            assertEquals(date, Date.from(player.getCreateTime().atZone(zone).toInstant()));
            assertEquals(date, Date.from(player.getUpdateTime().atZone(zone).toInstant()));

            doc = new Document().append("_id", 125) // uid
                    .append("wt", new Document("ct", 5200).append("cu", 200).append("d", 10).append("ad", 2)) // wallet
                    .append("eqm", // equipments
                            new Document("87654321-1234-5678-9abc-123456789abc",
                                    new Document("id", "87654321-1234-5678-9abc-123456789abc").append("rid", 1)
                                            .append("atk", 16).append("def", 2).append("hp", 100)))
                    .append("itm", new Document("2001", 10)) // items
                    .append("_uv", 1) // update version
                    .append("_ct", date) // create time
                    .append("_ut", date); // update time

            player.load(doc);

            assertFalse(player.updated());
            assertEquals(125, player.getUid());
            assertEquals(player, player.getWallet().parent());
            assertEquals(5200, player.getWallet().getCoinTotal());
            assertEquals(200, player.getWallet().getCoinUsed());
            assertEquals(5000, player.getWallet().getCoin());
            assertEquals(10, player.getWallet().getDiamond());
            assertEquals(2, player.getWallet().getAd());
            assertEquals(1, player.getEquipments().size());
            assertEquals(player, player.getEquipments().parent());

            eq = player.getEquipments().get("87654321-1234-5678-9abc-123456789abc");
            assertTrue(eq.isPresent());
            assertEquals(player.getEquipments(), eq.get().parent());
            assertEquals("87654321-1234-5678-9abc-123456789abc", eq.get().key());
            assertEquals("87654321-1234-5678-9abc-123456789abc", eq.get().getId());
            assertEquals(1, eq.get().getRefId());
            assertEquals(16, eq.get().getAtk());
            assertEquals(2, eq.get().getDef());
            assertEquals(100, eq.get().getHp());
            assertEquals(1, player.getItems().size());
            assertEquals(player, player.getItems().parent());
            assertEquals(10, player.getItems().get(2001).get());
            assertEquals(1, player.getUpdateVersion());

            assertEquals(date, Date.from(player.getCreateTime().atZone(zone).toInstant()));
            assertEquals(date, Date.from(player.getUpdateTime().atZone(zone).toInstant()));

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testLoadBsonDocument() {
        try {
            var date = new Date();
            var doc = new BsonDocument().append("_id", new BsonInt32(123)) // uid
                    .append("wt",
                            new BsonDocument("ct", new BsonInt64(5000)).append("cu", new BsonInt64(200))
                                    .append("d", new BsonInt64(10)).append("ad", new BsonInt32(2))) // wallet
                    .append("eqm", // equipments
                            new BsonDocument("12345678-1234-5678-9abc-123456789abc",
                                    new BsonDocument("id", new BsonString("12345678-1234-5678-9abc-123456789abc"))
                                            .append("rid", new BsonInt32(1)).append("atk", new BsonInt32(12))
                                            .append("def", new BsonInt32(2)).append("hp", new BsonInt32(100))))
                    .append("itm", new BsonDocument("2001", new BsonInt32(10))) // items
                    .append("_uv", new BsonInt32(1)) // update version
                    .append("_ct", new BsonDateTime(date.getTime())) // create time
                    .append("_ut", new BsonDateTime(date.getTime())); // update time
            var player = new Player();
            player.load(doc);

            assertFalse(player.updated());
            assertEquals(123, player.getUid());
            assertEquals(player, player.getWallet().parent());
            assertEquals(5000, player.getWallet().getCoinTotal());
            assertEquals(200, player.getWallet().getCoinUsed());
            assertEquals(4800, player.getWallet().getCoin());
            assertEquals(10, player.getWallet().getDiamond());
            assertEquals(2, player.getWallet().getAd());
            assertEquals(1, player.getEquipments().size());
            assertEquals(player, player.getEquipments().parent());
            var eq = player.getEquipments().get("12345678-1234-5678-9abc-123456789abc");
            assertTrue(eq.isPresent());
            assertEquals(player.getEquipments(), eq.get().parent());
            assertEquals("12345678-1234-5678-9abc-123456789abc", eq.get().key());
            assertEquals("12345678-1234-5678-9abc-123456789abc", eq.get().getId());
            assertEquals(1, eq.get().getRefId());
            assertEquals(12, eq.get().getAtk());
            assertEquals(2, eq.get().getDef());
            assertEquals(100, eq.get().getHp());
            assertEquals(1, player.getItems().size());
            assertEquals(player, player.getItems().parent());
            assertEquals(10, player.getItems().get(2001).get());
            assertEquals(1, player.getUpdateVersion());

            var zone = ZoneId.systemDefault();
            assertEquals(date, Date.from(player.getCreateTime().atZone(zone).toInstant()));
            assertEquals(date, Date.from(player.getUpdateTime().atZone(zone).toInstant()));

            doc = new BsonDocument().append("_id", new BsonInt32(125)) // uid
                    .append("wt",
                            new BsonDocument("ct", new BsonInt64(5200)).append("cu", new BsonInt64(200))
                                    .append("d", new BsonInt64(10)).append("ad", new BsonInt32(2))) // wallet
                    .append("eqm", // equipments
                            new BsonDocument("87654321-1234-5678-9abc-123456789abc",
                                    new BsonDocument("id", new BsonString("87654321-1234-5678-9abc-123456789abc"))
                                            .append("rid", new BsonInt32(1)).append("atk", new BsonInt32(16))
                                            .append("def", new BsonInt32(2)).append("hp", new BsonInt32(100))))
                    .append("itm", new BsonDocument("2001", new BsonInt32(10))) // items
                    .append("_uv", new BsonInt32(1)) // update version
                    .append("_ct", new BsonDateTime(date.getTime())) // create time
                    .append("_ut", new BsonDateTime(date.getTime())); // update time

            player.load(doc);

            assertFalse(player.updated());
            assertEquals(125, player.getUid());
            assertEquals(player, player.getWallet().parent());
            assertEquals(5200, player.getWallet().getCoinTotal());
            assertEquals(200, player.getWallet().getCoinUsed());
            assertEquals(5000, player.getWallet().getCoin());
            assertEquals(10, player.getWallet().getDiamond());
            assertEquals(2, player.getWallet().getAd());
            assertEquals(1, player.getEquipments().size());
            assertEquals(player, player.getEquipments().parent());

            eq = player.getEquipments().get("87654321-1234-5678-9abc-123456789abc");
            assertTrue(eq.isPresent());
            assertEquals(player.getEquipments(), eq.get().parent());
            assertEquals("87654321-1234-5678-9abc-123456789abc", eq.get().key());
            assertEquals("87654321-1234-5678-9abc-123456789abc", eq.get().getId());
            assertEquals(1, eq.get().getRefId());
            assertEquals(16, eq.get().getAtk());
            assertEquals(2, eq.get().getDef());
            assertEquals(100, eq.get().getHp());
            assertEquals(1, player.getItems().size());
            assertEquals(player, player.getItems().parent());
            assertEquals(10, player.getItems().get(2001).get());
            assertEquals(1, player.getUpdateVersion());

            assertEquals(date, Date.from(player.getCreateTime().atZone(zone).toInstant()));
            assertEquals(date, Date.from(player.getUpdateTime().atZone(zone).toInstant()));

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testToJson() {
        try {
            var player = new Player();
            player.setUid(123);
            player.getWallet().setCoinTotal(5000);
            player.getWallet().setCoinUsed(200);
            player.getWallet().setDiamond(10);
            var equipment = new Equipment();
            equipment.setId("12345678-1234-5678-9abc-123456789abc");
            equipment.setRefId(1);
            equipment.setAtk(10);
            equipment.setDef(0);
            equipment.setHp(0);
            player.getEquipments().put("12345678-1234-5678-9abc-123456789abc", equipment);
            player.getItems().put(2001, 5);
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setCreateTime(now);
            player.setUpdateTime(now);
            var json = Jackson2Library.getInstance().dumpsToString(player);
            var any = JsoniterLibrary.getInstance().loads(json);
            assertEquals(123, any.toInt("uid"));
            assertEquals(5000, any.toInt("wallet", "coinTotal"));
            assertEquals(4800, any.toInt("wallet", "coin"));
            assertEquals(10, any.toInt("wallet", "diamond"));
            assertEquals(0, any.toInt("wallet", "ad"));
            assertEquals("12345678-1234-5678-9abc-123456789abc",
                    any.toString("equipments", "12345678-1234-5678-9abc-123456789abc", "id"));
            assertEquals(1, any.toInt("equipments", "12345678-1234-5678-9abc-123456789abc", "refId"));
            assertEquals(10, any.toInt("equipments", "12345678-1234-5678-9abc-123456789abc", "atk"));
            assertEquals(0, any.toInt("equipments", "12345678-1234-5678-9abc-123456789abc", "def"));
            assertEquals(0, any.toInt("equipments", "12345678-1234-5678-9abc-123456789abc", "hp"));
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
            player.getWallet().setCoinTotal(5000);
            player.getWallet().setCoinUsed(200);
            player.getWallet().setDiamond(10);
            var eq1 = new Equipment();
            eq1.setId("12345678-1234-5678-9abc-123456789abc");
            eq1.setRefId(1);
            eq1.setAtk(10);
            eq1.setDef(0);
            eq1.setHp(0);
            player.getEquipments().put("12345678-1234-5678-9abc-123456789abc", eq1);
            var eq2 = new Equipment();
            eq2.setId("11111111-2222-3333-4444-555555555555");
            eq2.setRefId(2);
            eq2.setDef(5);
            eq2.setHp(2);
            player.getEquipments().put("11111111-2222-3333-4444-555555555555", eq2);
            player.getItems().put(2001, 5);
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setCreateTime(now.minusDays(1));
            player.setUpdateTime(now.minusSeconds(10));

            player.reset();

            player.getWallet().setCoinTotal(5200);
            player.getWallet().setCoinUsed(300);
            player.getWallet().increaseAd();

            player.getEquipments().get("12345678-1234-5678-9abc-123456789abc").get().fullyUpdate(true).setAtk(12);
            assertTrue(player.getEquipments().remove("11111111-2222-3333-4444-555555555555").isPresent());
            var eq3 = new Equipment();
            eq3.setId("00000000-0000-0000-0000-000000000000");
            eq3.setRefId(3);
            eq3.setDef(5);
            eq3.setHp(2);
            player.getEquipments().put("00000000-0000-0000-0000-000000000000", eq3.fullyUpdate(true));

            player.getItems().put(2002, 1);
            player.getItems().remove(2001);
            now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setUpdateTime(now);
            player.increaseUpdateVersion();

            var updates = new ArrayList<Bson>();
            var n = player.appendUpdates(updates);
            assertTrue(n > 0);
            assertEquals(n, updates.size());
            assertEquals(Updates.set("wt.ct", 5200L), updates.get(0));
            assertEquals(Updates.set("wt.cu", 300L), updates.get(1));
            assertEquals(Updates.set("wt.ad", 1), updates.get(2));
            assertEquals(Updates.set("eqm.12345678-1234-5678-9abc-123456789abc", eq1.toBson()), updates.get(3));
            assertEquals(Updates.set("eqm.00000000-0000-0000-0000-000000000000", eq3.toBson()), updates.get(4));
            assertEquals(Updates.unset("eqm.11111111-2222-3333-4444-555555555555"), updates.get(5));
            assertEquals(Updates.set("itm.2002", 1), updates.get(6));
            assertEquals(Updates.unset("itm.2001"), updates.get(7));
            assertEquals(Updates.set("_uv", 1), updates.get(8));
            var zone = ZoneId.systemDefault();
            var _ut = new BsonDateTime(now.atZone(zone).toInstant().toEpochMilli());
            assertEquals(Updates.set("_ut", _ut), updates.get(9));

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
            player.getWallet().setCoinTotal(5000);
            player.getWallet().setCoinUsed(200);
            player.getWallet().setDiamond(10);
            var eq1 = new Equipment();
            eq1.setId("12345678-1234-5678-9abc-123456789abc");
            eq1.setRefId(1);
            eq1.setAtk(10);
            eq1.setDef(0);
            eq1.setHp(0);
            player.getEquipments().put("12345678-1234-5678-9abc-123456789abc", eq1);
            var eq2 = new Equipment();
            eq2.setId("11111111-2222-3333-4444-555555555555");
            eq2.setRefId(2);
            eq2.setDef(5);
            eq2.setHp(2);
            player.getEquipments().put("11111111-2222-3333-4444-555555555555", eq2);
            player.getItems().put(2001, 5);
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setCreateTime(now);
            player.setUpdateTime(now);

            player.reset();

            player.getWallet().setCoinTotal(5200);
            player.getWallet().setCoinUsed(300);
            player.getWallet().increaseAd();

            player.getEquipments().get("12345678-1234-5678-9abc-123456789abc").get().fullyUpdate(true).setAtk(12);
            assertTrue(player.getEquipments().remove("11111111-2222-3333-4444-555555555555").isPresent());
            var eq3 = new Equipment();
            eq3.setId("00000000-0000-0000-0000-000000000000");
            eq3.setRefId(3);
            eq3.setDef(5);
            eq3.setHp(2);
            player.getEquipments().put("00000000-0000-0000-0000-000000000000", eq3.fullyUpdate(true));

            player.getItems().put(2002, 1);
            player.getItems().remove(2001);
            now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setUpdateTime(now);
            player.increaseUpdateVersion();

            var update = player.toUpdate();
            assertNotNull(update);
            var json = Jackson2Library.getInstance().dumpsToString(update);
            var any = JsoniterLibrary.getInstance().loads(json);
            assertEquals(3, any.asMap().size());
            assertEquals(3, any.get("wallet").asMap().size());
            assertEquals(2, any.get("equipments").asMap().size());
            assertEquals(5, any.get("equipments", "12345678-1234-5678-9abc-123456789abc").asMap().size());
            assertEquals(5, any.get("equipments", "00000000-0000-0000-0000-000000000000").asMap().size());
            assertEquals(1, any.get("items").asMap().size());
            assertEquals(5200, any.toInt("wallet", "coinTotal"));
            assertEquals(4900, any.toInt("wallet", "coin"));
            assertEquals("12345678-1234-5678-9abc-123456789abc",
                    any.toString("equipments", "12345678-1234-5678-9abc-123456789abc", "id"));
            assertEquals(1, any.toInt("equipments", "12345678-1234-5678-9abc-123456789abc", "refId"));
            assertEquals(12, any.toInt("equipments", "12345678-1234-5678-9abc-123456789abc", "atk"));
            assertEquals(0, any.toInt("equipments", "12345678-1234-5678-9abc-123456789abc", "def"));
            assertEquals(0, any.toInt("equipments", "12345678-1234-5678-9abc-123456789abc", "hp"));
            assertEquals("00000000-0000-0000-0000-000000000000",
                    any.toString("equipments", "00000000-0000-0000-0000-000000000000", "id"));
            assertEquals(3, any.toInt("equipments", "00000000-0000-0000-0000-000000000000", "refId"));
            assertEquals(0, any.toInt("equipments", "00000000-0000-0000-0000-000000000000", "atk"));
            assertEquals(5, any.toInt("equipments", "00000000-0000-0000-0000-000000000000", "def"));
            assertEquals(2, any.toInt("equipments", "00000000-0000-0000-0000-000000000000", "hp"));
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
            player.getWallet().setCoinTotal(5000);
            player.getWallet().setCoinUsed(200);
            player.getWallet().setDiamond(10);
            var eq1 = new Equipment();
            eq1.setId("12345678-1234-5678-9abc-123456789abc");
            eq1.setRefId(1);
            eq1.setAtk(10);
            eq1.setDef(0);
            eq1.setHp(0);
            player.getEquipments().put("12345678-1234-5678-9abc-123456789abc", eq1);
            var eq2 = new Equipment();
            eq2.setId("11111111-2222-3333-4444-555555555555");
            eq2.setRefId(2);
            eq2.setDef(5);
            eq2.setHp(2);
            player.getEquipments().put("11111111-2222-3333-4444-555555555555", eq2);
            player.getItems().put(2001, 5);
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setCreateTime(now);
            player.setUpdateTime(now);

            player.reset();

            player.getWallet().setCoinTotal(5200);
            player.getWallet().increaseAd();

            player.getEquipments().get("12345678-1234-5678-9abc-123456789abc").get().fullyUpdate(true).setAtk(12);
            assertTrue(player.getEquipments().remove("11111111-2222-3333-4444-555555555555").isPresent());
            var eq3 = new Equipment();
            eq3.setId("00000000-0000-0000-0000-000000000000");
            eq3.setRefId(3);
            eq3.setDef(5);
            eq3.setHp(2);
            player.getEquipments().put("00000000-0000-0000-0000-000000000000", eq3.fullyUpdate(true));

            player.getItems().put(2002, 1);
            player.getItems().remove(2001);
            now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            player.setUpdateTime(now);
            player.increaseUpdateVersion();

            var delete = player.toDelete();
            assertNotNull(delete);
            var json = Jackson2Library.getInstance().dumpsToString(delete);
            var any = JsoniterLibrary.getInstance().loads(json);
            assertEquals(2, any.asMap().size());
            assertEquals(1, any.get("equipments").asMap().size());
            assertEquals(1, any.get("items").asMap().size());
            assertEquals(1, any.toInt("equipments", "11111111-2222-3333-4444-555555555555"));
            assertEquals(1, any.toInt("items", "2001"));
        } catch (Exception e) {
            fail(e);
        }
    }

}
