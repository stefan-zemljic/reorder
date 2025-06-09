package com.stefan_zemljic.reorder;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class ReorderFileNode extends TreeNodeDelegate<PsiFile> {
    private final PsiFileNode n;
    private final int weight;

    protected ReorderFileNode(PsiFileNode n, int weight) {
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
