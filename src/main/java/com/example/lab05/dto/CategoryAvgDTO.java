package com.example.lab05.dto;

public class CategoryAvgDTO {

    private String category;
    private Double avgPrice;

    public CategoryAvgDTO() {}

    public CategoryAvgDTO(String category, Double avgPrice) {
        this.category = category;
        this.avgPrice = avgPrice;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getAvgPrice() { return avgPrice; }
    public void setAvgPrice(Double avgPrice) { this.avgPrice = avgPrice; }
}
