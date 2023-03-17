package com.github.fmjsjx.libcommon.bson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.mongodb.MongoClientSettings;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.junit.jupiter.api.Test;


public class BsonValueUtilTests {

    @Test
    public void testEncodeValue() {
        var codecRegistry = MongoClientSettings.getDefaultCodecRegistry();
        try (var writer = new BsonDocumentWriter(new BsonDocument())) {
            writer.writeStartDocument();
            writer.writeName("null");
            BsonValueUtil.encodeValue(writer, null, codecRegistry);
            writer.writeName("bson");
            BsonValueUtil.encodeValue(writer, new BsonString("value"), codecRegistry);
            writer.writeName("string");
            BsonValueUtil.encodeValue(writer, "This is a string!", codecRegistry);
            writer.writeEndDocument();
            var document = writer.getDocument();
            assertEquals(BsonNull.VALUE, document.get("null"));
            assertEquals(new BsonString("value"), document.get("bson"));
            assertEquals(new BsonString("This is a string!"), document.get("string"));
        }

    }

}
