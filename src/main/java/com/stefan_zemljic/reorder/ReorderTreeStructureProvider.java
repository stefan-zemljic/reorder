package com.stefan_zemljic.reorder;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReorderTreeStructureProvider implements TreeStructureProvider {
    private final static ThreadLocal<Map<String, Integer>> maps = ThreadLocal.withInitial(HashMap::new);

    @Override
    public @NotNull Collection<AbstractTreeNode<?>> modify(
            @NotNull AbstractTreeNode<?> parent,
            @NotNull Collection<AbstractTreeNode<?>> children,
            ViewSettings settings
    ) {
        if (parent instanceof PsiDirectoryNode n) {
            return modify(n, children, settings);
        }
        return children;
    }

    private @NotNull Collection<AbstractTreeNode<?>> modify(
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
        map.put(".order", 1);
        new String(orderFileContent).lines().forEach(line ->
                map.put(line.trim(), map.size() + 2)
        );
        System.out.println(map);
        var orderedChildren = new ArrayList<AbstractTreeNode<?>>();
        for (var child : children) {
            var weight = map.get(getTitle(child));
            if (weight == null) {
                orderedChildren.add(child);
                continue;
            }
            System.out.println("Mapping " + child.getName() + " to weight " + weight);
            orderedChildren.add(map(child, weight));
        }
        System.out.println(children.stream().map(Object::getClass).toList());
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

