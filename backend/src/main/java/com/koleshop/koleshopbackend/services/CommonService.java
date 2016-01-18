package com.koleshop.koleshopbackend.services;

import com.google.gson.JsonObject;
import com.koleshop.koleshopbackend.common.Constants;
import com.koleshop.koleshopbackend.db.connection.DatabaseConnection;
import com.koleshop.koleshopbackend.db.models.Address;
import com.koleshop.koleshopbackend.db.models.KoleResponse;
import com.koleshop.koleshopbackend.db.models.SellerSettings;
import com.koleshop.koleshopbackend.gcm.GcmHelper;
import com.koleshop.koleshopbackend.utils.DatabaseConnectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Gundeep on 10/01/16.
 */
public class CommonService {

    private static final Logger logger = Logger.getLogger(CommonService.class.getName());

    public KoleResponse saveOrUpdateAddress(Address addressObject) {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        KoleResponse response = new KoleResponse();

        Long addressId = addressObject.getId();
        Long userId = addressObject.getUserId();
        String address = addressObject.getAddress();
        int addressType = addressObject.getAddressType();
        String name = addressObject.getName();
        String nickname = addressObject.getNickname();
        Long phone = addressObject.getPhoneNumber();
        int countryCode = addressObject.getCountryCode();
        Double gpsLong = addressObject.getGpsLong();
        Double gpsLat = addressObject.getGpsLat();

        String query;

        if (addressId != null && addressId > 0) {

            //update address and phone
            query = "update Address set name=?, address=?, address_type=?, phone_number=?, country_code=?, nickname=?, gps_long=?, gps_lat=?" +
                    " where user_id=? and id=?";

            logger.log(Level.INFO, "query=" + query);

            try {
                dbConnection = DatabaseConnection.getConnection();
                preparedStatement = dbConnection.prepareStatement(query);

                preparedStatement.setString(1, name);
                preparedStatement.setString(2, address);
                preparedStatement.setInt(3, addressType);
                preparedStatement.setLong(4, phone);
                preparedStatement.setInt(5, countryCode);
                preparedStatement.setString(6, nickname);
                preparedStatement.setDouble(7, gpsLong);
                preparedStatement.setDouble(8, gpsLat);
                preparedStatement.setLong(9, userId);
                preparedStatement.setLong(10, addressId);

                int update = preparedStatement.executeUpdate();
                if (update > 0) {
                    //address updated
                    response.setSuccess(true);
                    response.setData(addressObject);
                } else {
                    //address update failed
                    response.setStatus("address_not_updated");
                    response.setData(null);
                }

                DatabaseConnectionUtils.closeStatementAndConnection(preparedStatement, dbConnection);
                return response;

            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
                response.setStatus(e.getLocalizedMessage());
                response.setData(null);
                return response;
            } finally {
                DatabaseConnectionUtils.finallyCloseStatementAndConnection(preparedStatement, dbConnection);
            }

        } else {
            //add new address
            query = "insert into Address(user_id,name,address,address_type,phone_number,country_code,nickname,gps_long,gps_lat)" +
                    " values ?,?,?,?,?,?,?,?,?";

            logger.log(Level.INFO, "query=" + query);

            try {
                dbConnection = DatabaseConnection.getConnection();
                preparedStatement = dbConnection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

                preparedStatement.setLong(1, userId);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, address);
                preparedStatement.setInt(4, addressType);
                preparedStatement.setLong(5, phone);
                preparedStatement.setInt(6, countryCode);
                preparedStatement.setString(7, nickname);
                preparedStatement.setDouble(8, gpsLong);
                preparedStatement.setDouble(9, gpsLat);

                int executed = preparedStatement.executeUpdate();
                ResultSet rsAddress = preparedStatement.getGeneratedKeys();
                if (executed > 0 && rsAddress != null && rsAddress.next()) {
                    //address added successfully
                    addressId = rsAddress.getLong(1);
                    addressObject = new Address(addressId, userId, name, address, addressType, phone, countryCode, nickname, gpsLong, gpsLat);
                    response.setSuccess(true);
                    response.setData(addressObject);
                } else {
                    //address adding failed
                    response.setStatus("address_not_updated");
                    response.setData(null);
                }

                DatabaseConnectionUtils.closeStatementAndConnection(preparedStatement, dbConnection);
                return response;

            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
                response.setStatus(e.getLocalizedMessage());
                response.setData(null);
                return response;
            } finally {
                DatabaseConnectionUtils.finallyCloseStatementAndConnection(preparedStatement, dbConnection);
            }

        }
    }

    public KoleResponse updateSellerSettings(Long userId, SellerSettings settings) {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        KoleResponse response = new KoleResponse();

        Address addressObject = settings.getAddress();

        Date openTime = settings.getShopOpenTime();
        Date closeTime = settings.getShopCloseTime();
        logger.log(Level.INFO, "updating shop settings...");
        logger.log(Level.INFO, "shop open time : " + openTime);
        logger.log(Level.INFO, "shop close time : " + closeTime);
        boolean pickupFromShop = settings.isPickupFromShop();
        boolean homeDelivery = settings.isHomeDelivery();
        Long maxDeliveryDistance = settings.getMaximumDeliveryDistance();
        Float minOrder = settings.getMinimumOrder();
        Float deliveryCharges = settings.getDeliveryCharges();
        Date deliveryStartTime = settings.getDeliveryStartTime();
        Date deliveryEndTime = settings.getDeliveryEndTime();
        logger.log(Level.INFO, "deliveryStartTime: " + deliveryStartTime);
        logger.log(Level.INFO, "deliveryEndTime: " + deliveryEndTime);
        String address = null;
        String shopName = null;
        Long phone = null;
        int countryCode = 91;
        Double gpsLong = null;
        Double gpsLat = null;

        if (addressObject != null) {
            shopName = addressObject.getName();
            phone = addressObject.getPhoneNumber();
            countryCode = addressObject.getCountryCode();
            gpsLong = addressObject.getGpsLong();
            gpsLat = addressObject.getGpsLat();
            address = addressObject.getAddress();
        }

        String query;

        //update address and settings
        query = "update Address a join SellerSettings ss on a.id = ss.address_id and a.user_id = ss.user_id and ss.user_id=?" +
                " set a.name=?, a.address=?, a.phone_number=?, a.country_code=?, a.nickname=?, a.gps_long=?, a.gps_lat=?," +
                " ss.open_time=?, ss.close_time=?, ss.pickup_from_shop=?, ss.home_delivery=?, ss.max_delivery_distance=?," +
                " ss.min_order=?, ss.delivery_charges=?, ss.delivery_start_time=?, ss.delivery_end_time=?" +
                " where a.user_id=? and a.address_type='" + Constants.ADDRESS_TYPE_SELLER + "'";

        logger.log(Level.INFO, "query=" + query);

        try {
            dbConnection = DatabaseConnection.getConnection();
            preparedStatement = dbConnection.prepareStatement(query);

            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, shopName);
            preparedStatement.setString(3, address);
            preparedStatement.setLong(4, phone);
            preparedStatement.setInt(5, countryCode);
            preparedStatement.setString(6, Constants.SELLER_ADDRESS_NICKNAME);
            preparedStatement.setDouble(7, gpsLong);
            preparedStatement.setDouble(8, gpsLat);
            if(openTime!=null) {
                preparedStatement.setTimestamp(9, new java.sql.Timestamp(openTime.getTime()));
            } else {
                preparedStatement.setDate(9, null);
            }
            if(closeTime!=null) {
                preparedStatement.setTimestamp(10, new java.sql.Timestamp(closeTime.getTime()));
            } else {
                preparedStatement.setDate(10, null);
            }
            preparedStatement.setBoolean(11, pickupFromShop);
            preparedStatement.setBoolean(12, homeDelivery);
            preparedStatement.setLong(13, maxDeliveryDistance);
            preparedStatement.setFloat(14, minOrder);
            preparedStatement.setFloat(15, deliveryCharges);
            if(deliveryStartTime!=null){
                preparedStatement.setTimestamp(16, new java.sql.Timestamp(deliveryStartTime.getTime()));
            } else {
                preparedStatement.setDate(16, null);
            }
            if(deliveryEndTime!=null) {
                preparedStatement.setTimestamp(17, new java.sql.Timestamp(deliveryEndTime.getTime()));
            } else {
                preparedStatement.setDate(17, null);
            }
            preparedStatement.setLong(18, userId);

            logger.log(Level.INFO, "prepared statment = " + preparedStatement.toString());

            int update = preparedStatement.executeUpdate();
            if (update > 0) {
                //settings updated
                response.setSuccess(true);
                response.setData("settings_updated");

                //delete any old caches stored in phones
                JsonObject notificationJsonObject = new JsonObject();
                notificationJsonObject.addProperty("type", Constants.GCM_NOTI_DELETE_OLD_SETTINGS_CACHE);
                notificationJsonObject.addProperty("millis", new Date().getTime());
                GcmHelper.notifyUser(userId, notificationJsonObject, Constants.GCM_NOTI_COLLAPSE_KEY_DELETE_OLD_SETTINGS_CACHE);
            } else {
                //settings update
                response.setStatus("settings_not_updated");
                response.setData(null);
            }

            DatabaseConnectionUtils.closeStatementAndConnection(preparedStatement, dbConnection);
            return response;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException = " + e.getLocalizedMessage(), e);
            response.setStatus(e.getLocalizedMessage());
            response.setData(null);
            return response;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            response.setStatus(e.getLocalizedMessage());
            response.setData(null);
            return response;
        } finally {
            DatabaseConnectionUtils.finallyCloseStatementAndConnection(preparedStatement, dbConnection);
        }

    }

    public KoleResponse getSellerSettings(Long userId) {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        KoleResponse response = new KoleResponse();

        String query;

        //get address and settings
        query = "select a.id as address_id, ss.id as seller_settings_id, a.name, a.address, a.phone_number, a.country_code, a.nickname, a.gps_long, a.gps_lat," +
                " ss.open_time, ss.close_time, ss.home_delivery, ss.max_delivery_distance," +
                " ss.min_order, ss.delivery_charges, ss.delivery_start_time, ss.delivery_end_time" +
                " from Address a join SellerSettings ss on a.id = ss.address_id and a.user_id = ss.user_id and ss.user_id=?" +
                " where a.address_type='" + Constants.ADDRESS_TYPE_SELLER + "'";

        try {
            dbConnection = DatabaseConnection.getConnection();
            preparedStatement = dbConnection.prepareStatement(query);

            preparedStatement.setLong(1, userId);

            logger.log(Level.INFO, "prepared statement=" + preparedStatement);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                Long addressId = rs.getLong("address_id");
                Long sellerSettingsId = rs.getLong("seller_settings_id");
                String addressName = rs.getString("name");
                String addressString = rs.getString("address");
                Long phoneNumber = rs.getLong("phone_number");
                int countryCode = rs.getInt("country_code");
                String nickname = rs.getString("nickname");
                Double gpsLongitude = rs.getDouble("gps_long");
                Double gpsLatitude = rs.getDouble("gps_lat");
                java.sql.Time sqlOpenTime = rs.getTime("open_time");
                Date openTime = null;
                if (sqlOpenTime != null) {
                    openTime = new Date(sqlOpenTime.getTime());
                }
                java.sql.Time sqlCloseTime = rs.getTime("close_time");
                Date closeTime = null;
                if (sqlCloseTime != null) {
                    closeTime = new Date(sqlCloseTime.getTime());
                }
                boolean homeDelivery = rs.getBoolean("home_delivery");
                Long maxDeliveryDistance = rs.getLong("max_delivery_distance");
                Float minOrder = rs.getFloat("min_order");
                Float deliveryCharges = rs.getFloat("delivery_charges");
                Date deliveryStartTime = null;
                java.sql.Time sqlDeliveryStartTime = rs.getTime("delivery_start_time");
                if (sqlDeliveryStartTime != null) {
                    deliveryStartTime = new Date(sqlDeliveryStartTime.getTime());
                }
                Date deliveryEndTime = null;
                java.sql.Time sqlDeliveryEndTime = rs.getTime("delivery_end_time");
                if (sqlDeliveryEndTime != null) {
                    deliveryEndTime = new Date(sqlDeliveryEndTime.getTime());
                }

                Address address = new Address(addressId, userId, addressName, addressString, 1, phoneNumber, countryCode, nickname, gpsLongitude, gpsLatitude);
                SellerSettings sellerSettings = new SellerSettings(sellerSettingsId, userId, address, openTime, closeTime, true, homeDelivery, maxDeliveryDistance,
                        minOrder, deliveryCharges, deliveryStartTime, deliveryEndTime);
                response.setSuccess(true);
                response.setData(sellerSettings);
            }

            DatabaseConnectionUtils.closeStatementAndConnection(preparedStatement, dbConnection);
            return response;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            response.setStatus(e.getLocalizedMessage());
            response.setData(null);
            return response;
        } finally {
            DatabaseConnectionUtils.finallyCloseStatementAndConnection(preparedStatement, dbConnection);
        }
    }
}
