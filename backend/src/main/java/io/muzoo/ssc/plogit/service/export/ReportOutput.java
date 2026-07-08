package io.muzoo.ssc.plogit.service.export;

public record ReportOutput(
    byte[] content,
    String contentType,
    String filename
) {
}
