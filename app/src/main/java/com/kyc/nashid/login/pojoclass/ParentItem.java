package com.kyc.nashid.login.pojoclass;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class ParentItem {
    private String title;
    private List<ChildItem> children;
    private boolean isExpanded;

    public ParentItem(String title, List<ChildItem> children) {
        this.title = title;
        this.children = children;
        this.isExpanded = false;
    }

    public String getTitle() {
        return title;
    }

    public List<ChildItem> getChildren() {
        return children;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
// Getters and setters
}


