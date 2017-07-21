package com.github.mlamina.kubernetes;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandTokenizer {

    private final String command;
    private final Map<String,String> varMap = Maps.newHashMap();

    public CommandTokenizer(String command) {
        this.command = command;
    }

    // Tokenizes string while finding variables wrapped in "s
    public String[] tokenize() {
        Pattern regex = Pattern.compile("\"([^\"]+)\"");
        Matcher m = regex.matcher(command);
        String replaced = command;
        while (m.find()) {
            String key = UUID.randomUUID().toString();
            String var = m.group();
            varMap.put(key, var);
            replaced = replaced.replaceFirst(var, key);
        }
        List<String> result = Arrays.stream(replaced.split(" "))
                .map(token -> varMap.getOrDefault(token, token))
                .collect(Collectors.toList());
        // Find incomplete variable and collapse all tokens afterwards
        int unfinishedVarIndex = -1;
        StringBuilder unfinishedVarValue = new StringBuilder();
        for (int i=0; i<result.size(); i++) {
            if (result.get(i).startsWith("\"") && !result.get(i).endsWith("\""))
                unfinishedVarIndex = i;
            if (unfinishedVarIndex >= 0) {
                unfinishedVarValue.append(result.get(i));
                if (i < result.size() - (command.endsWith(" ") ? 0:1))
                    unfinishedVarValue.append(" ");
            }

        }
        if (unfinishedVarIndex >= 0) {
            while (unfinishedVarIndex < result.size())
                result.remove(result.size() - 1);
            result.add(unfinishedVarValue.toString());
        }


        return result.toArray(new String[result.size()]);
    }
}
