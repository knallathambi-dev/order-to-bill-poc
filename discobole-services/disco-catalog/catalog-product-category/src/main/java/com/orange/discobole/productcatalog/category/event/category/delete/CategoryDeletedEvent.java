package com.orange.discobole.productcatalog.category.event.category.delete;

import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.event.category.CategoryEvent;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

public class CategoryDeletedEvent implements CategoryEvent {

    @TargetAggregateIdentifier
    private String categoryId;

    private List<Category> categoryList;

    public List<Category> getCategoryList() {
        return categoryList;
    }

    @Override
    public String toString() {
        return "CategoryDeletedEvent{" +
                "categoryId='" + categoryId + '\'' +
                ", categoryList=" + categoryList +
                '}';
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public CategoryDeletedEvent(String categoryId, List<Category> categoryList) {
        this.categoryId = categoryId;
        this.categoryList = categoryList;
    }



    public CategoryDeletedEvent() {
        super();
        this.categoryId = null;
        this.categoryList = null;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

}
