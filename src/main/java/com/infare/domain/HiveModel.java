package com.infare.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "hivemodel")
public class HiveModel {

    @Id String  id;
    Integer observed_date_min_as_infaredate;
    Integer observed_date_max_as_infaredate;
    Integer full_weeks_before_departure;
    Integer carrier_id;
    String searched_cabin_class;
    Integer booking_site_id;
    Integer booking_site_type_id;
    Integer is_trip_one_way;
    Integer trip_origin_airport_id;
    Integer trip_destination_airport_id;
    Integer trip_min_stay;
    Double trip_price_min;
    Double trip_price_max;
    Double trip_price_avg;
    Double trip_price_avg_2;
    Integer aggregation_count;
    Integer out_flight_departure_date_as_infaredate;
    Integer out_flight_departure_time_as_infaretime;
    Integer out_flight_time_in_minutes;
    Integer out_sector_count;
    Integer out_flight_sector_1_flight_code_id;
    Integer out_flight_sector_2_flight_code_id;
    Integer out_flight_sector_3_flight_code_id;
    Integer home_flight_departure_date_as_infaredate;
    Integer home_flight_departure_time_as_infaretime;
    Integer home_flight_time_in_minutes;
    Integer home_sector_count;
    Integer home_flight_sector_1_flight_code_id;
    Integer home_flight_sector_2_flight_code_id;
    Integer home_flight_sector_3_flight_code_id;
}
