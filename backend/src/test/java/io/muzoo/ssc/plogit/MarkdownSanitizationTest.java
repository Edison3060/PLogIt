package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.service.MarkdownService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkdownSanitizationTest {

    private final MarkdownService md = new MarkdownService();

    @Test
    void rendersBasicMarkdown() {
        String result = md.render("**bold** and _italic_");
        assertTrue(result.contains("<strong>bold</strong>") || result.contains("<b>bold</b>"));
        assertTrue(result.contains("<em>italic</em>") || result.contains("<i>italic</i>"));
    }

    @Test
    void rendersCodeBlock() {
        String result = md.render("```\nnmap -sS\n```");
        assertTrue(result.contains("<code"));
        assertTrue(result.contains("nmap -sS"));
    }

    @Test
    void stripsScriptTag() {
        String result = md.render("<script>alert('xss')</script>");
        assertFalse(result.contains("<script"));
        assertFalse(result.contains("alert"));
    }

    @Test
    void stripsJavascriptProtocol() {
        String result = md.render("[click](javascript:alert(1))");
        assertFalse(result.contains("javascript:"));
    }

    @Test
    void stripsEventHandlers() {
        String result = md.render("<img src=x onerror=alert(1)>");
        assertFalse(result.contains("onerror"));
    }

    @Test
    void allowsSafeLinks() {
        String result = md.render("[plogit](https://example.com)");
        assertTrue(result.contains("href"));
        assertTrue(result.contains("example.com"));
    }

    @Test
    void handlesNull() {
        assertTrue(md.render(null).isEmpty());
    }

    @Test
    void handlesBlank() {
        assertTrue(md.render("").isEmpty());
    }
}
