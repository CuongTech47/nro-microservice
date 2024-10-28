package com.ngocrong.user;

import com.ngocrong.network.Session;
import lombok.Data;
import org.apache.log4j.Logger;

import java.sql.Timestamp;

@Data
public class User {
    private static Logger logger = Logger.getLogger(User.class);
    private static final int[][] HAIR_ID = { { 64, 30, 31 }, { 9, 29, 32 }, { 6, 27, 28 } };
    private static final int[][] LOCATION = { { 39, 100, 384 }, { 40, 100, 384 }, { 41, 100, 384 } };

    private int id;
    private String username;
    private String password;
    private int status;
    private int role;
    private Session session;
    private Timestamp lockTime;
    public static boolean isAdmin;
}
