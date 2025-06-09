package com.stefan_zemljic.reorder;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

public class ReorderDirectoryNode extends TreeNodeDelegate<PsiDirectory> {
    private final PsiDirectoryNode n;
    private final int weight;

    protected ReorderDirectoryNode(PsiDirectoryNode n, int weight) {
        super(n);
        this.n = n;
        this.weight = weight;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    protected void update(@NotNull PresentationData presentation) {
        n.update(presentation);
    }
}
