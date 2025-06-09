package com.stefan_zemljic.reorder;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.platform.backend.navigation.NavigationRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collection;
import java.util.Map;

public abstract class TreeNodeDelegate<T> extends AbstractTreeNode<T> {
    private final AbstractTreeNode<T> d;

    public TreeNodeDelegate(@NotNull AbstractTreeNode<T> d) {
        super(d.getProject(), d.getValue());
        this.d = d;
    }

    @Override
    public @NotNull Collection<? extends AbstractTreeNode<?>> getChildren() {
        return d.getChildren();
    }

    @Override
    public boolean isAlwaysShowPlus() {
        return d.isAlwaysShowPlus();
    }

    @Override
    public boolean isAlwaysLeaf() {
        return d.isAlwaysLeaf();
    }

    @Override
    public boolean isAlwaysExpand() {
        return d.isAlwaysExpand();
    }

    @Override
    public boolean isIncludedInExpandAll() {
        return d.isIncludedInExpandAll();
    }

    @Override
    public boolean isAutoExpandAllowed() {
        return d.isAutoExpandAllowed();
    }

    @Override
    public void apply(@NotNull Map<String, String> info) {
        d.apply(info);
    }

    @Override
    public @Nullable Color getFileStatusColor(final FileStatus status) {
        return d.getFileStatusColor(status);
    }

    @Override
    public FileStatus getFileStatus() {
        return d.getFileStatus();
    }

    protected abstract void update(@NotNull PresentationData presentation);

    @Override
    public String getName() {
        return d.getName();
    }

    @Override
    public boolean canRepresent(final Object element) {
        return d.canRepresent(element);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable NavigationRequest navigationRequest() {
        return d.navigationRequest();
    }

    @Override
    public void navigate(boolean requestFocus) {
        d.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return d.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return d.canNavigateToSource();
    }
}
