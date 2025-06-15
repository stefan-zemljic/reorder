package com.stefan_zemljic.reorder;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.PresentableNodeDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ReflectiveUtils {
    private static final Method getVirtualFileMethod;
    private static final Method updateMethod;

    static {
        try {
            getVirtualFileMethod = AbstractTreeNode.class.getDeclaredMethod("getVirtualFile");
            getVirtualFileMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            updateMethod = PresentableNodeDescriptor.class.getDeclaredMethod("update", PresentationData.class);
            updateMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static VirtualFile getVirtualFileOrNull(AbstractTreeNode<?> node) {
        try {
            Object res = getVirtualFileMethod.invoke(node);
            if (res instanceof VirtualFile) {
                return (VirtualFile) res;
            }
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException("Failed to call getVirtualFile method on " + node.getClass().getName(), exception);
        }
        return null;
    }

    static void callUpdate(PresentableNodeDescriptor<?> obj, @NotNull PresentationData presentation) {
        try {
            updateMethod.invoke(obj, presentation);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException("Failed to call update method on " + obj.getClass().getName(), exception);
        }
    }
}