package com.dodream.common.enumtype;

public enum Category {
    CATEGORY_CS("CATEGORY_CS"),
    CATEGORY_CERT("CATEGORY_CERT"),
    CATEGORY_ETC("CATEGORY_ETC");

    String category;

    Category(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}