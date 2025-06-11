package com.stefan_zemljic.reorder;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

public class ReorderDirectoryNode extends TreeNodeDelegate<PsiDirectory> {
    private final PsiDirectoryNode psiDirectoryNode;
    private final int weight;

    protected ReorderDirectoryNode(PsiDirectoryNode psiDirectoryNode, int weight) {
        super(psiDirectoryNode);
        this.psiDirectoryNode = psiDirectoryNode;
        this.weight = weight;
    }

    public PsiDirectoryNode getPsiDirectoryNode() {
        return psiDirectoryNode;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        psiDirectoryNode.update(presentation);
    }
}
