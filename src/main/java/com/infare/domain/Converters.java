package com.infare.domain;

import org.springframework.batch.item.file.transform.FieldSet;

public final class Converters {

    public static final HiveModel to(FieldSet fs, HiveModel  model) {
        model.setObserved_date_min_as_infaredate(fs.readInt(0));
        model.setObserved_date_max_as_infaredate(fs.readInt(1));
        model.setFull_weeks_before_departure(fs.readInt(2));
        model.setCarrier_id(fs.readInt(3));
        model.setSearched_cabin_class(fs.readString(4));
        model.setBooking_site_id(fs.readInt(5));
        model.setBooking_site_type_id(fs.readInt(6));
        model.setIs_trip_one_way(fs.readInt(7));
        model.setTrip_origin_airport_id(fs.readInt(8));
        model.setTrip_destination_airport_id(fs.readInt(9));
        model.setTrip_min_stay(fs.readString(10).trim().isEmpty() ? 0 : fs.readInt(10));
        model.setTrip_price_min(fs.readDouble(11));
        model.setTrip_price_max(fs.readDouble(12));
        model.setTrip_price_avg(fs.readDouble(13));
        model.setAggregation_count(fs.readInt(14));
        return model;
    }

}
