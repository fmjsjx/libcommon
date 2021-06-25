package com.github.fmjsjx.libcommon.bson;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import org.junit.jupiter.api.Test;

public class BsonUtilTest {

    @Test
    public void testEmbedded() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":1,\"d2\":\"2\",\"d3\":[1,2,3]},\"c2\":[{\"i\":0},{\"i\":1},{\"i\":2}]}}}";
        var document = Document.parse(json);
        try {
            assertTrue(BsonUtil.embedded(document, "a").isPresent());
            assertTrue(BsonUtil.embedded(document, "a", "b").isPresent());
            assertTrue(BsonUtil.embedded(document, "a", "b", "c1").isPresent());
            assertTrue(BsonUtil.embedded(document, "a", "b", "c2").isPresent());
            Optional<Integer> d1 = BsonUtil.embedded(document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(1, d1.get().intValue());
            Optional<String> d2 = BsonUtil.embedded(document, "a", "b", "c1", "d2");
            assertTrue(d2.isPresent());
            assertEquals("2", d2.get());
            Optional<List<Integer>> d3 = BsonUtil.embedded(document, "a", "b", "c1", "d3");
            assertTrue(d2.isPresent());
            assertArrayEquals(new int[] { 1, 2, 3 }, d3.get().stream().mapToInt(Integer::intValue).toArray());
            Optional<Integer> c20 = BsonUtil.embedded(document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(0, c20.get().intValue());
            Optional<Integer> c21 = BsonUtil.embedded(document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(1, c21.get().intValue());
            Optional<Integer> c22 = BsonUtil.embedded(document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(2, c22.get().intValue());
            assertFalse(BsonUtil.embedded(document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embedded(document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testEmbeddedInt() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":1,\"d2\":\"2\",\"d3\":[1,2,3]},\"c2\":[{\"i\":0},{\"i\":1},{\"i\":2}]}}}";
        var document = Document.parse(json);
        try {
            var d1 = BsonUtil.embeddedInt(document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(1, d1.getAsInt());
            var d30 = BsonUtil.embeddedInt(document, "a", "b", "c1", "d3", 0);
            assertTrue(d30.isPresent());
            assertEquals(1, d30.getAsInt());
            var d31 = BsonUtil.embeddedInt(document, "a", "b", "c1", "d3", 1);
            assertTrue(d31.isPresent());
            assertEquals(2, d31.getAsInt());
            var d32 = BsonUtil.embeddedInt(document, "a", "b", "c1", "d3", 2);
            assertTrue(d32.isPresent());
            assertEquals(3, d32.getAsInt());
            var c20 = BsonUtil.embeddedInt(document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(0, c20.getAsInt());
            var c21 = BsonUtil.embeddedInt(document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(1, c21.getAsInt());
            var c22 = BsonUtil.embeddedInt(document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(2, c22.getAsInt());
            assertFalse(BsonUtil.embeddedInt(document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embeddedInt(document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testEmbeddedLong() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":1,\"d2\":\"2\",\"d3\":[1,2,3]},\"c2\":[{\"i\":0},{\"i\":1},{\"i\":2}]}}}";
        var document = Document.parse(json);
        try {
            var d1 = BsonUtil.embeddedLong(document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(1, d1.getAsLong());
            var d30 = BsonUtil.embeddedLong(document, "a", "b", "c1", "d3", 0);
            assertTrue(d30.isPresent());
            assertEquals(1, d30.getAsLong());
            var d31 = BsonUtil.embeddedLong(document, "a", "b", "c1", "d3", 1);
            assertTrue(d31.isPresent());
            assertEquals(2, d31.getAsLong());
            var d32 = BsonUtil.embeddedLong(document, "a", "b", "c1", "d3", 2);
            assertTrue(d32.isPresent());
            assertEquals(3, d32.getAsLong());
            var c20 = BsonUtil.embeddedLong(document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(0, c20.getAsLong());
            var c21 = BsonUtil.embeddedLong(document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(1, c21.getAsLong());
            var c22 = BsonUtil.embeddedLong(document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(2, c22.getAsLong());
            assertFalse(BsonUtil.embeddedLong(document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embeddedLong(document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testEmbeddedDouble() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":1,\"d2\":\"2\",\"d3\":[1,2,3]},\"c2\":[{\"i\":0},{\"i\":1},{\"i\":2}]}}}";
        var document = Document.parse(json);
        try {
            var d1 = BsonUtil.embeddedDouble(document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(1, d1.getAsDouble());
            var d30 = BsonUtil.embeddedDouble(document, "a", "b", "c1", "d3", 0);
            assertTrue(d30.isPresent());
            assertEquals(1, d30.getAsDouble());
            var d31 = BsonUtil.embeddedDouble(document, "a", "b", "c1", "d3", 1);
            assertTrue(d31.isPresent());
            assertEquals(2, d31.getAsDouble());
            var d32 = BsonUtil.embeddedDouble(document, "a", "b", "c1", "d3", 2);
            assertTrue(d32.isPresent());
            assertEquals(3, d32.getAsDouble());
            var c20 = BsonUtil.embeddedDouble(document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(0, c20.getAsDouble());
            var c21 = BsonUtil.embeddedDouble(document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(1, c21.getAsDouble());
            var c22 = BsonUtil.embeddedDouble(document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(2, c22.getAsDouble());
            assertFalse(BsonUtil.embeddedDouble(document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embeddedDouble(document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testEmbeddedDateTime() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":ISODate(\"2021-04-19T10:37:10.128Z\"),\"d2\":\"2\",\"d3\":[ISODate(\"2021-04-19T10:37:10.128Z\"),2,3]},\"c2\":[{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")},{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")},{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")}]}}}";
        var document = Document.parse(json);
        try {
            var zone = ZoneId.systemDefault();
            var time = ZonedDateTime.parse("2021-04-19T10:37:10.128Z").withZoneSameInstant(zone).toLocalDateTime();
            var d1 = BsonUtil.embeddedDateTime(document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(time, d1.get());
            var d30 = BsonUtil.embeddedDateTime(document, "a", "b", "c1", "d3", 0);
            assertTrue(d30.isPresent());
            var c20 = BsonUtil.embeddedDateTime(document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(time, c20.get());
            var c21 = BsonUtil.embeddedDateTime(document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(time, c21.get());
            var c22 = BsonUtil.embeddedDateTime(document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(time, c22.get());
            assertFalse(BsonUtil.embeddedDateTime(document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embeddedDateTime(document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testEmbeddedZonedDateTime() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":ISODate(\"2021-04-19T10:37:10.128Z\"),\"d2\":\"2\",\"d3\":[ISODate(\"2021-04-19T10:37:10.128Z\"),2,3]},\"c2\":[{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")},{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")},{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")}]}}}";
        var document = Document.parse(json);
        try {
            var zone = ZoneId.of("Z");
            var time = ZonedDateTime.parse("2021-04-19T10:37:10.128Z");
            var d1 = BsonUtil.embeddedDateTime(zone, document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(time, d1.get());
            var d30 = BsonUtil.embeddedDateTime(zone, document, "a", "b", "c1", "d3", 0);
            assertTrue(d30.isPresent());
            var c20 = BsonUtil.embeddedDateTime(zone, document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(time, c20.get());
            var c21 = BsonUtil.embeddedDateTime(zone, document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(time, c21.get());
            var c22 = BsonUtil.embeddedDateTime(zone, document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(time, c22.get());
            assertFalse(BsonUtil.embeddedDateTime(zone, document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embeddedDateTime(zone, document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testDocumentValue() {
        // {"a":{},"b":{"n":"b"}}
        var json = "{\"a\":{},\"b\":{\"n\":\"b\"}}";
        var document = Document.parse(json);
        try {
            var value = BsonUtil.documentValue(document, "a");
            assertTrue(value.isPresent());
            assertEquals(0, value.get().size());
            value = BsonUtil.documentValue(document, "b");
            assertTrue(value.isPresent());
            assertEquals(1, value.get().size());
            assertEquals("b", value.get().getString("n"));
            value = BsonUtil.documentValue(document, "c");
            assertTrue(value.isEmpty());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testEmbeddedBsonDocument() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":1,\"d2\":\"2\",\"d3\":[1,2,3]},\"c2\":[{\"i\":0},{\"i\":1},{\"i\":2}]}}}";
        var document = BsonDocument.parse(json);
        try {
            assertTrue(BsonUtil.embedded(document, "a").isPresent());
            assertTrue(BsonUtil.embedded(document, "a", "b").isPresent());
            assertTrue(BsonUtil.embedded(document, "a", "b", "c1").isPresent());
            assertTrue(BsonUtil.embedded(document, "a", "b", "c2").isPresent());
            Optional<BsonInt32> d1 = BsonUtil.embedded(document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(1, d1.get().intValue());
            Optional<BsonString> d2 = BsonUtil.embedded(document, "a", "b", "c1", "d2");
            assertTrue(d2.isPresent());
            assertEquals("2", d2.get().getValue());
            Optional<BsonArray> d3 = BsonUtil.embedded(document, "a", "b", "c1", "d3");
            assertTrue(d2.isPresent());
            assertArrayEquals(new int[] { 1, 2, 3 }, d3.get().stream().mapToInt(v -> v.asInt32().getValue()).toArray());
            Optional<BsonInt32> c20 = BsonUtil.embedded(document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(0, c20.get().intValue());
            Optional<BsonInt32> c21 = BsonUtil.embedded(document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(1, c21.get().intValue());
            Optional<BsonInt32> c22 = BsonUtil.embedded(document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(2, c22.get().intValue());
            assertFalse(BsonUtil.embedded(document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embedded(document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testEmbeddedIntBsonDocument() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":1,\"d2\":\"2\",\"d3\":[1,2,3]},\"c2\":[{\"i\":0},{\"i\":1},{\"i\":2}]}}}";
        var document = BsonDocument.parse(json);
        try {
            var d1 = BsonUtil.embeddedInt(document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(1, d1.getAsInt());
            var d30 = BsonUtil.embeddedInt(document, "a", "b", "c1", "d3", 0);
            assertTrue(d30.isPresent());
            assertEquals(1, d30.getAsInt());
            var d31 = BsonUtil.embeddedInt(document, "a", "b", "c1", "d3", 1);
            assertTrue(d31.isPresent());
            assertEquals(2, d31.getAsInt());
            var d32 = BsonUtil.embeddedInt(document, "a", "b", "c1", "d3", 2);
            assertTrue(d32.isPresent());
            assertEquals(3, d32.getAsInt());
            var c20 = BsonUtil.embeddedInt(document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(0, c20.getAsInt());
            var c21 = BsonUtil.embeddedInt(document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(1, c21.getAsInt());
            var c22 = BsonUtil.embeddedInt(document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(2, c22.getAsInt());
            assertFalse(BsonUtil.embeddedInt(document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embeddedInt(document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testEmbeddedLongBsonDocument() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":1,\"d2\":\"2\",\"d3\":[1,2,3]},\"c2\":[{\"i\":0},{\"i\":1},{\"i\":2}]}}}";
        var document = BsonDocument.parse(json);
        try {
            var d1 = BsonUtil.embeddedLong(document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(1, d1.getAsLong());
            var d30 = BsonUtil.embeddedLong(document, "a", "b", "c1", "d3", 0);
            assertTrue(d30.isPresent());
            assertEquals(1, d30.getAsLong());
            var d31 = BsonUtil.embeddedLong(document, "a", "b", "c1", "d3", 1);
            assertTrue(d31.isPresent());
            assertEquals(2, d31.getAsLong());
            var d32 = BsonUtil.embeddedLong(document, "a", "b", "c1", "d3", 2);
            assertTrue(d32.isPresent());
            assertEquals(3, d32.getAsLong());
            var c20 = BsonUtil.embeddedLong(document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(0, c20.getAsLong());
            var c21 = BsonUtil.embeddedLong(document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(1, c21.getAsLong());
            var c22 = BsonUtil.embeddedLong(document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(2, c22.getAsLong());
            assertFalse(BsonUtil.embeddedLong(document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embeddedLong(document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testEmbeddedDoubleBsonDocument() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":1,\"d2\":\"2\",\"d3\":[1,2,3]},\"c2\":[{\"i\":0},{\"i\":1},{\"i\":2}]}}}";
        var document = BsonDocument.parse(json);
        try {
            var d1 = BsonUtil.embeddedDouble(document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(1, d1.getAsDouble());
            var d30 = BsonUtil.embeddedDouble(document, "a", "b", "c1", "d3", 0);
            assertTrue(d30.isPresent());
            assertEquals(1, d30.getAsDouble());
            var d31 = BsonUtil.embeddedDouble(document, "a", "b", "c1", "d3", 1);
            assertTrue(d31.isPresent());
            assertEquals(2, d31.getAsDouble());
            var d32 = BsonUtil.embeddedDouble(document, "a", "b", "c1", "d3", 2);
            assertTrue(d32.isPresent());
            assertEquals(3, d32.getAsDouble());
            var c20 = BsonUtil.embeddedDouble(document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(0, c20.getAsDouble());
            var c21 = BsonUtil.embeddedDouble(document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(1, c21.getAsDouble());
            var c22 = BsonUtil.embeddedDouble(document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(2, c22.getAsDouble());
            assertFalse(BsonUtil.embeddedDouble(document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embeddedDouble(document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testEmbeddedDateTimeBsonDocument() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":ISODate(\"2021-04-19T10:37:10.128Z\"),\"d2\":\"2\",\"d3\":[ISODate(\"2021-04-19T10:37:10.128Z\"),2,3]},\"c2\":[{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")},{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")},{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")}]}}}";
        var document = BsonDocument.parse(json);
        try {
            var zone = ZoneId.systemDefault();
            var time = ZonedDateTime.parse("2021-04-19T10:37:10.128Z").withZoneSameInstant(zone).toLocalDateTime();
            var d1 = BsonUtil.embeddedDateTime(document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(time, d1.get());
            var d30 = BsonUtil.embeddedDateTime(document, "a", "b", "c1", "d3", 0);
            assertTrue(d30.isPresent());
            var c20 = BsonUtil.embeddedDateTime(document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(time, c20.get());
            var c21 = BsonUtil.embeddedDateTime(document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(time, c21.get());
            var c22 = BsonUtil.embeddedDateTime(document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(time, c22.get());
            assertFalse(BsonUtil.embeddedDateTime(document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embeddedDateTime(document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testEmbeddedZonedDateTimeBsonDocument() {
        var json = "{\"a\":{\"b\":{\"c1\":{\"d1\":ISODate(\"2021-04-19T10:37:10.128Z\"),\"d2\":\"2\",\"d3\":[ISODate(\"2021-04-19T10:37:10.128Z\"),2,3]},\"c2\":[{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")},{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")},{\"i\":ISODate(\"2021-04-19T10:37:10.128Z\")}]}}}";
        var document = BsonDocument.parse(json);
        try {
            var zone = ZoneId.of("Z");
            var time = ZonedDateTime.parse("2021-04-19T10:37:10.128Z");
            var d1 = BsonUtil.embeddedDateTime(zone, document, "a", "b", "c1", "d1");
            assertTrue(d1.isPresent());
            assertEquals(time, d1.get());
            var d30 = BsonUtil.embeddedDateTime(zone, document, "a", "b", "c1", "d3", 0);
            assertTrue(d30.isPresent());
            var c20 = BsonUtil.embeddedDateTime(zone, document, "a", "b", "c2", 0, "i");
            assertTrue(c20.isPresent());
            assertEquals(time, c20.get());
            var c21 = BsonUtil.embeddedDateTime(zone, document, "a", "b", "c2", 1, "i");
            assertTrue(c21.isPresent());
            assertEquals(time, c21.get());
            var c22 = BsonUtil.embeddedDateTime(zone, document, "a", "b", "c2", 2, "i");
            assertTrue(c22.isPresent());
            assertEquals(time, c22.get());
            assertFalse(BsonUtil.embeddedDateTime(zone, document, "a", "b", "c").isPresent());
            assertFalse(BsonUtil.embeddedDateTime(zone, document, "a2").isPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

}
