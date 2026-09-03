package com.ai.spring_ai.repository.design;

import com.ai.spring_ai.design.FocusArea;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryFocusAreaStore {

    private final ConcurrentHashMap<String, FocusArea> byId = new ConcurrentHashMap<>();

    public InMemoryFocusAreaStore() {
        // ponytail: one seeded aggregate so deep-link GETs work without a prior POST
        save(MorningEnergySeed.create(LocalDate.now()));
    }

    public FocusArea save(FocusArea focusArea) {
        byId.put(focusArea.id(), focusArea);
        return focusArea;
    }

    public Optional<FocusArea> find(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public FocusArea require(String id) {
        return find(id).orElseThrow(() -> new java.util.NoSuchElementException("FocusArea not found: " + id));
    }

    public List<FocusArea> findByUser(String userId) {
        return byId.values().stream().filter(focusArea -> focusArea.userId().equals(userId)).toList();
    }
}