package com.cryptite.lite.bungee;

import java.util.ArrayList;
import java.util.List;

class Achievement {
    public String name;
    public String title;
    public String type;
    private final List<String> unlockMessage = new ArrayList<>();

    public String getAchievementText() {
        StringBuilder achievementLore = new StringBuilder();

        for (String msg : unlockMessage) {
            //TODO: Can re-enable multiline when FancyMessage fixes.
//            achievementLore.append(msg + "\n");
            achievementLore.append(msg).append(" ");
        }

        String achievementText = achievementLore.toString();
        return achievementText.substring(0, achievementText.length() - 1);
    }
}
