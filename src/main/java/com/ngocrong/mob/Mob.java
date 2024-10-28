package com.ngocrong.mob;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Mob {
    private static Logger logger = Logger.getLogger(Mob.class);
    public enum ItemType {
        NONE, GOLD, ITEM, EQUIP, GEM, CARD, EVENT, NRO, UPGRADESTONE
    }
    public static final int[] LEVEL = { -1, -1, 1, 2, 3, 4, 5, 6, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 11, 12, 12, 12,
            12, -1, -1, -1 };
    public static final int[][][] OPTIONS = { { { 127, 139 }, { 128, 140 }, { 129, 141 } },
            { { 130, 142 }, { 131, 143 }, { 132, 144 } }, { { 133, 136 }, { 134, 137 }, { 135, 138 } } };

    public static ArrayList<MobTemplate> vMobTemplate = new ArrayList<>();
    public static byte[] data;
    public static int baseId = 0;
    public static int count = 0;
    public static void addMobTemplate(MobTemplate mob) {
        vMobTemplate.add(mob);
    }
    public static MobTemplate getMobTemplate(int id) {
        return vMobTemplate.get(id);
    }
    public static void createData() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(vMobTemplate.size());
            for (MobTemplate mob : vMobTemplate) {
                dos.writeByte(mob.mobTemplateId);
                dos.writeByte(mob.type);
                dos.writeUTF(mob.name);
                dos.writeLong(mob.hp);
                dos.writeByte(mob.rangeMove);
                dos.writeByte(mob.speed);
                dos.writeByte(mob.dartType);
            }
            data = bos.toByteArray();
            dos.close();
            bos.close();
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }
}
