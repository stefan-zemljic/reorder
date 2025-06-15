package com.stefan_zemljic.reorder;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFileSystemItem;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static com.stefan_zemljic.reorder.ReflectiveUtils.getVirtualFileOrNull;


public class ReorderTreeStructureProvider implements TreeStructureProvider {
    private final static ThreadLocal<Map<String, Integer>> maps = ThreadLocal.withInitial(HashMap::new);

    @Override
    public @NotNull Collection<AbstractTreeNode<?>> modify(
            @NotNull AbstractTreeNode<?> parent,
            @NotNull Collection<AbstractTreeNode<?>> children,
            ViewSettings settings
    ) {
        var parentValue = parent.getValue();
        if (parentValue instanceof PsiFileSystemItem fsItem && fsItem.isDirectory()) {
            return modifyDirectoryChildren(fsItem.getVirtualFile(), children, settings);
        }
        return children;
    }

    private @NotNull Collection<AbstractTreeNode<?>> modifyDirectoryChildren(
            VirtualFile directory,
            @NotNull Collection<AbstractTreeNode<?>> children,
            ViewSettings ignoredSettings
    ) {
        var orderFile = directory.findChild(".order");
        if (orderFile == null) return children;
        byte[] orderFileContent;
        try {
            orderFileContent = orderFile.contentsToByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read .order file in " + directory.getPath(), e);
        }
        var map = maps.get();
        map.clear();
        new String(orderFileContent).lines().forEach(line ->
                map.put(line.trim(), map.size())
        );
        if (!map.containsKey(".order")) {
            map.put(".order", map.size());
        }
        if (!map.containsKey("/folders")) {
            map.put("/folders", map.size());
        }
        if (!map.containsKey("/files")) {
            map.put("/files", map.size());
        }
        if (!map.containsKey("/other")) {
            map.put("/other", map.size());
        }
        var orderedChildren = new ArrayList<AbstractTreeNode<?>>();
        for (var child : children) {
            var childFile = getVirtualFileOrNull(child);
            int weight = childFile == null ? map.get("/other") : Objects.requireNonNullElseGet(map.get(childFile.getName()), () -> {
                if (childFile.isDirectory()) {
                    return map.get("/folders");
                }
                return map.get("/files");
            });
            orderedChildren.add(new ReorderNode(child, childFile, weight));
        }
        map.clear();
        children.clear();
        return orderedChildren;
    }
}