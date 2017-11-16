package com.cokiMing.handler;

import com.cokiMing.util.Configuration;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wuyiming on 2017/11/7.
 */
class BlackListManager {

    private byte[] blackList = new byte[0x20000000];

    private static BlackListManager manager;

    private Lock writeLock = new ReentrantReadWriteLock().readLock();

    private Lock readLock = new ReentrantReadWriteLock().readLock();

    public static synchronized BlackListManager getInstant() {
        if (manager == null) {
            manager = new BlackListManager();
        }

        return manager;
    }

    /**
     * 检查IP是否在黑名单内
     * @param IP
     */
    public boolean checkIPCertification(String... IP) {
        if(!checkFormat(IP)) {
            return false;
        }

        readLock.lock();
        int blackListIndex = getIPIndex(IP[0]);
        byte boxInfo = blackList[blackListIndex];
        String binaryString = shortBinaryString(Integer.toBinaryString(boxInfo));
        int certIndex = Integer.parseInt(IP[0].split("\\.")[3]) % 8;
        readLock.unlock();
        return binaryString.toCharArray()[certIndex] == '1';
    }

    private void defriendIP(String IP) {
        setIpStatus(IP,'1');
    }

    boolean defriendIPs(String... IPs) {
        if(!checkFormat(IPs)) {
            return false;
        }

        writeLock.lock();
        for (String ip : IPs) {
            defriendIP(ip);
        }
        writeLock.unlock();
        return true;
    }

    private void resumeIP(String IP) {
        setIpStatus(IP,'0');
    }

    boolean resumeIPs(String... IPs) {
        if(!checkFormat(IPs)) {
            return false;
        }

        writeLock.lock();
        for (String ip : IPs) {
            resumeIP(ip);
        }
        writeLock.unlock();
        return true;
    }

    private boolean checkFormat(String... IPs) {
        for (String ip : IPs) {
            String[] split = ip.split("\\.");
            for (String s : split) {
               if (Integer.parseInt(s) > 255) {
                   return false;
               }
            }
        }

        return true;
    }

    /**
     * 初始化黑名单信息
     */
    private void init() throws RuntimeException {
        try{
            String blacklist = Configuration.get("cokiMing.megaBlackList.blacklist");
            String[] ipList = blacklist.split(",");
            this.defriendIPs(ipList);
        } catch (Exception e) {
            System.out.println("加载配置文件错误");
            throw new RuntimeException(e);
        }
    }

    private void setIpStatus(String IP, char status) {
        int blackListIndex = getIPIndex(IP);
        byte boxInfo = blackList[blackListIndex];
        String binaryString = shortBinaryString(Integer.toBinaryString(boxInfo));

        int certIndex = Integer.parseInt(IP.split("\\.")[3]) % 8;
        char[] certArray = binaryString.toCharArray();
        certArray[certIndex] = status;
        blackList[blackListIndex] = Byte.parseByte(new String(certArray),2);
    }

    private int getIPIndex(String IP) {
        String[] subIPs = IP.split("\\.");
        int index1 = Integer.parseInt(subIPs[0]) << 21;
        int index2 = Integer.parseInt(subIPs[1]) << 13;
        int index3 = Integer.parseInt(subIPs[2]) << 5;
        int index4 = Integer.parseInt(subIPs[3]) >> 3;

        return index1 + index2 + index3 + index4;
    }

    private String shortBinaryString(String binaryString) {
        if (binaryString.length() < 8) {
            StringBuilder zeroBuilder = new StringBuilder();
            int num = 8 - binaryString.length();
            for (int i = 0; i < num; i ++) {
                zeroBuilder.append('0');
            }

            binaryString = zeroBuilder.append(binaryString).toString();
        } else if (binaryString.length() >= 8) {
            binaryString = binaryString.substring(24);
        }

        return binaryString;
    }

    private BlackListManager() {
        init();
    }
}
