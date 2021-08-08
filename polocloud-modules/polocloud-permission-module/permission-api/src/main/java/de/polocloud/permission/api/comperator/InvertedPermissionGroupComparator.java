package de.polocloud.permission.api.comperator;

import de.polocloud.permission.api.group.IPermissionGroup;

import java.util.Comparator;

public class InvertedPermissionGroupComparator implements Comparator<IPermissionGroup> {

    @Override
    public int compare(IPermissionGroup group1, IPermissionGroup group2) {
        return group1.getPriority() - group2.getPriority();
    }


}
