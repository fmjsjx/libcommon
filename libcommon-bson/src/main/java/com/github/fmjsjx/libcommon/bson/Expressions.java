package com.github.fmjsjx.libcommon.bson;

import static com.github.fmjsjx.libcommon.bson.BsonValueUtil.encodeValue;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;


/**
 * A factory for MongoDB BSON expressions.
 *
 * @author MJ Fang
 * @since 3.3
 */
public final class Expressions {

    /**
     * Creates a new branch(for $switch operator) expression.
     *
     * @param caseExpression the case expression
     * @param thenExpression the then expression
     * @return the branch expression
     */
    public static final Bson branch(Object caseExpression, Object thenExpression) {
        return new BranchExpression(caseExpression, thenExpression);
    }

    private record BranchExpression(Object caseExpression, Object thenExpression) implements Bson {
        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            var writer = new BsonDocumentWriter(new BsonDocument(2));
            writer.writeStartDocument();
            writer.writeName("case");
            encodeValue(writer, caseExpression, codecRegistry);
            writer.writeName("then");
            encodeValue(writer, thenExpression, codecRegistry);
            writer.writeEndDocument();
            return writer.getDocument();
        }
    }

    private Expressions() {
    }

}
