package com.stefan_zemljic.reorder;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.stefan_zemljic.reorder.ReflectiveUtils.callUpdate;

public class ReorderNode extends TreeNodeDelegate {
    private final @NotNull AbstractTreeNode<?> abstractTreeNode;
    private final VirtualFile virtualFile;
    private final int weight;

    protected ReorderNode(
            @NotNull AbstractTreeNode<?> abstractTreeNode,
            VirtualFile virtualFile,
            int weight
    ) {
        super(abstractTreeNode);
        this.abstractTreeNode = abstractTreeNode;
        this.virtualFile = virtualFile;
        this.weight = weight;
    }

    @Override
    protected @Nullable VirtualFile getVirtualFile() {
        return virtualFile;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        callUpdate(abstractTreeNode, presentation);
    }
}
