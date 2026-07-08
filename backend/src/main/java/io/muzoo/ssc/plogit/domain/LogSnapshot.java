package io.muzoo.ssc.plogit.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record LogSnapshot(
    String activityType,
    String title,
    String description,
    String result,
    String target,
    String toolUsed,
    String outcome,
    List<String> tags,
    String codeBlock,
    String codeLanguage
) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("activityType", activityType);
        map.put("title", title);
        map.put("description", description);
        map.put("result", result);
        map.put("target", target);
        map.put("toolUsed", toolUsed);
        map.put("outcome", outcome);
        map.put("tags", tags);
        map.put("codeBlock", codeBlock);
        map.put("codeLanguage", codeLanguage);
        return map;
    }

    public static LogSnapshot from(LogEntry log) {
        return new LogSnapshot(
            log.getActivityType().name(),
            log.getTitle(),
            log.getDescription(),
            log.getResult(),
            log.getTarget(),
            log.getToolUsed(),
            log.getOutcome().name(),
            log.getTags(),
            log.getCodeBlock(),
            log.getCodeLanguage()
        );
    }
}
