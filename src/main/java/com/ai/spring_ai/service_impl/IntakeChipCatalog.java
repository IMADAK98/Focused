package com.ai.spring_ai.service_impl;

import com.ai.spring_ai.dto.design.IntakeChipCatalogItem;

import java.util.Comparator;
import java.util.List;

final class IntakeChipCatalog {

    private static final List<IntakeChipCatalogItem> MORNING_ENERGY_WHATS_NOT_WORKING = List.of(
            item("waking_up_groggy", "Waking up groggy", "energy", 1),
            item("phone_first_thing", "Phone first thing", "device", 2),
            item("scattered_mornings", "Scattered mornings", "attention", 3),
            item("no_breakfast_routine", "No breakfast routine", "food", 4),
            item("late_to_meetings", "Late to meetings", "time", 5),
            item("inconsistent_wake_time", "Inconsistent wake time", "energy", 6),
            item("hits_snooze_repeatedly", "Hits snooze repeatedly", "energy", 7),
            item("stays_in_bed_scrolling", "Stays in bed scrolling", "device", 8),
            item("caffeine_timing_off", "Caffeine timing feels off", "food", 9),
            item("skips_morning_movement", "Skips morning movement", "energy", 10),
            item("too_many_morning_decisions", "Too many morning decisions", "attention", 11),
            item("dark_or_cluttered_space", "Dark or cluttered start space", "environment", 12),
            item("family_or_kids_chaos", "Family / kids chaos", "environment", 13),
            item("rushed_commute_start", "Rushed commute start", "time", 14),
            item("low_morning_light", "Low morning light", "environment", 15));

    private IntakeChipCatalog() {}

    static List<IntakeChipCatalogItem> find(String focusAreaCatalogId, String kind) {
        if (!"morning-energy".equals(focusAreaCatalogId) || !"whats_not_working".equals(kind)) {
            return List.of();
        }
        return MORNING_ENERGY_WHATS_NOT_WORKING.stream()
                .sorted(Comparator.comparingInt(IntakeChipCatalogItem::sortOrder))
                .toList();
    }

    static boolean contains(String focusAreaCatalogId, String kind, String chipId) {
        return find(focusAreaCatalogId, kind).stream().anyMatch(item -> item.id().equals(chipId));
    }

    private static IntakeChipCatalogItem item(String id, String label, String group, int sortOrder) {
        return new IntakeChipCatalogItem(id, label, group, sortOrder, "morning-energy", "whats_not_working");
    }
}
