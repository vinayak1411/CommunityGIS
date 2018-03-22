package com.vinayakgaonkar.communitygis;

/**
 * Created by vinayak on 16-03-2018.
 */

public class Feedback {
    private int id;
    private String address,amenity_type,amenity_category,comment,image_url;
    private String rating;

    public Feedback(int id, String address, String amenity_type,String amenity_category, String comment, String image_url, String rating) {
        this.id = id;
        this.address = address;
        this.amenity_type = amenity_type;
        this.amenity_category = amenity_category;
        this.comment = comment;
        this.image_url = image_url;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getAmenity_type() {
        return amenity_type;
    }
    public String getAmenity_category() {
        return amenity_category;
    }
    public String getComment() {
        return comment;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getRating() {
        return rating;
    }
}