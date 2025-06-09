package com.stefan_zemljic.reorder;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GetWeight {
    public static Integer getWeight(@Nullable VirtualFile file) {
        if (file == null) return null;
        if (Objects.equals(file.getName(), ".order")) {
            return Integer.MIN_VALUE;
        }
        var orderFile = file.getParent().findChild(".order");
        if (orderFile == null) {
            return null;
        }
        byte[] data;
        try {
            data = orderFile.contentsToByteArray(true);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return null;
        }
        String content = new String(data);
        List<String> lines = content.lines().toList();
        for (var i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.equals(file.getName())) {
                return Integer.MIN_VALUE + 1 + i;
            }
        }
        return 0;
    }
}
