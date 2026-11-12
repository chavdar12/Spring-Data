package com.example.exerciseSpringDataAutoMappingObjects.models.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GameAddDto {

    @Pattern(regexp = "[A-Z].{2,99}", message = "Title must start with an uppercase letter and be 3-100 symbols")
    private String title;

    @NotNull
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull
    @Positive(message = "Size must be positive")
    @Digits(integer = 10, fraction = 1, message = "Size must have up to 1 digit after the decimal point")
    private BigDecimal size;

    @Size(min = 11, max = 11, message = "Trailer must be a YouTube video id of 11 symbols")
    private String trailer;

    @Pattern(regexp = "https?://.+", message = "Thumbnail URL must start with http:// or https://")
    private String imageThumbnail;

    @NotNull
    @Size(min = 20, message = "Description must be at least 20 symbols")
    private String description;

    @NotNull
    private LocalDate releaseDate;

    public GameAddDto() {
    }

    public GameAddDto(String title, BigDecimal price, BigDecimal size, String trailer, String imageThumbnail,
                      String description, LocalDate releaseDate) {
        this.title = title;
        this.price = price;
        this.size = size;
        // the task gives full YouTube links but stores only the video id
        this.trailer = trailer == null ? null : trailer.replaceFirst("^https?://(www\\.)?youtube\\.com/watch\\?v=", "");
        this.imageThumbnail = imageThumbnail;
        this.description = description;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSize() {
        return size;
    }

    public void setSize(BigDecimal size) {
        this.size = size;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}
