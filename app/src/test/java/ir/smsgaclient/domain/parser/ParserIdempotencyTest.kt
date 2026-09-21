// app/src/test/java/ir/smsgaclient/domain/parser/ParserIdempotencyTest.kt
package ir.smsgaclient.domain.parser

import ir.smsgaclient.domain.model.PatternSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParserIdempotencyTest {

    @Test
    fun `produces identical message id for identical raw sms and timestamp`() {
        val raw = "+۱۰۰٬۰۰۰ ریال — بلو"
        val timestamp = 1790000000000L

        val id1 = SmsParser.generateMessageId(raw, timestamp)
        val id2 = SmsParser.generateMessageId(raw, timestamp)

        assertEquals(id1, id2)
        assert(id1.startsWith("sha256:"))
    }
}
