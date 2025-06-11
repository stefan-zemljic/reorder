package com.stefan_zemljic.reorder;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class ReorderTreeStructureProvider implements TreeStructureProvider {
    private final static ThreadLocal<Map<String, Integer>> maps = ThreadLocal.withInitial(HashMap::new);

    @Override
    public @NotNull Collection<AbstractTreeNode<?>> modify(
            @NotNull AbstractTreeNode<?> parent,
            @NotNull Collection<AbstractTreeNode<?>> children,
            ViewSettings settings
    ) {
        switch (parent) {
            case PsiDirectoryNode n -> {
                return modifyDirectoryChildren(n, children, settings);
            }
            case ReorderDirectoryNode n -> {
                return modifyDirectoryChildren(n.getPsiDirectoryNode(), children, settings);
            }
            default -> {
                return children;
            }
        }
    }

    private @NotNull Collection<AbstractTreeNode<?>> modifyDirectoryChildren(
            PsiDirectoryNode parent,
            @NotNull Collection<AbstractTreeNode<?>> children,
            ViewSettings ignoredSettings
    ) {
        var file = parent.getVirtualFile();
        if (file == null) return children;
        var orderFile = file.findChild(".order");
        if (orderFile == null) return children;
        byte[] orderFileContent;
        try {
            orderFileContent = orderFile.contentsToByteArray();
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return children;
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
            if (map.containsKey("/other")) {
                map.put("/folders", map.get("/other"));
            } else {
                map.put("/folders", map.size());
            }
        }
        if (!map.containsKey("/files")) {
            if (map.containsKey("/other")) {
                map.put("/files", map.get("/other"));
            } else {
                map.put("/files", map.size());
            }
        }
        var orderedChildren = new ArrayList<AbstractTreeNode<?>>();
        for (var child : children) {
            var weight = Objects.requireNonNullElseGet(map.get(getTitle(child)), () -> {
                if (child instanceof PsiDirectoryNode || child instanceof ReorderDirectoryNode) {
                    return map.get("/folders");
                }
                return map.get("/files");
            });
            orderedChildren.add(map(child, weight));
        }
        map.clear();
        children.clear();
        return orderedChildren;
    }

    private String getTitle(AbstractTreeNode<?> node) {
        if (node instanceof PsiFileNode fileNode) {
            return fileNode.getValue().getName();
        } else if (node instanceof PsiDirectoryNode dirNode) {
            return dirNode.getValue().getName();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> AbstractTreeNode<T> map(AbstractTreeNode<T> node, int weight) {
        return switch (node) {
            case PsiFileNode n -> (AbstractTreeNode<T>) new ReorderFileNode(n, weight);
            case PsiDirectoryNode n -> (AbstractTreeNode<T>) new ReorderDirectoryNode(n, weight);
            default -> node;
        };
    }
}

