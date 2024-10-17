package com.dodream.common.enumtype;

import lombok.Getter;

@Getter
public enum Category {
    CATEGORY_CS("CATEGORY_CS"),
    CATEGORY_CERT("CATEGORY_CERT"),
    CATEGORY_ETC("CATEGORY_ETC");

    final String category;

    Category(String category) {
        this.category = category;
    }

}