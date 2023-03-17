package com.github.fmjsjx.libcommon.bson;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionsTests {

    @Test
    public void testBranch() {
        assertEquals(
                new Document("case", "$case").append("then", "$then").toBsonDocument(),
                Expressions.branch("$case", "$then").toBsonDocument()
        );
    }

}
