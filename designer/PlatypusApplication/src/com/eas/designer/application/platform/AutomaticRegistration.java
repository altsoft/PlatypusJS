/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.platform;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Registers a Platypus.js runtime by creating instance file in cluster config
 * directory. Designed to be called from installer.
 *
 * @author vv
 * @see #main(args)
 */
public class AutomaticRegistration {

    private static final Logger LOGGER = Logger.getLogger(AutomaticRegistration.class.getName());

    /**
     * Performs registration/uregistration of server instance. May also list
     * existing tomcat instances.
     *
     * Exit codes:
     * <p>
     * <ul>
     * <li> 0: success
     * <li> 1: could not handle cluster folder
     * <li> 2: could not find platypus_runtime_home
     * <li> 3: could not save in the file
     * </ul>
     *
     * @param args command line arguments
     * <ul>
     * <li>--add cluster_path platypus_runtime_home
     * </ul>
     */
    public static void main(String[] args) {
        if (args.length <= 0) {
            printHelpAndExit();
        }

        if ("--add".equals(args[0])) {
            if (args.length < 3) {
                printHelpAndExit();
            }
            int status = registerPlatypusRuntime(args[1], args[2]);
            System.exit(status);
        } else {
            printHelpAndExit();
        }
    }

    private static void printHelpAndExit() {
        System.out.println("Available actions:");
        System.out.println("\t--add <clusterDir> <platypusRuntimeHome>");
        System.exit(-1);
    }

    private static int registerPlatypusRuntime(String clusterDirValue, String platypusRuntimePath) {
        // tell the infrastructure that the userdir is cluster dir
        System.setProperty("netbeans.user", clusterDirValue); // NOI18N
        File platypusRuntimeHome = new File(platypusRuntimePath);
        if (!platypusRuntimeHome.exists()) {
            LOGGER.log(Level.INFO, "Cannot register the Platypus.js Runtime. " // NOI18N
                    + "The Platypus.js Runtime home directory {0} does not exist.", platypusRuntimePath); // NOI18N
            return 2;
        }
        // make sure the platform's runtime is not registered yet
        String p = PlatypusPlatform.getPlatformHomePath();
        if (p != null && !p.isEmpty()) {
            // the platform's runtime is already registered, do nothing
            return 0;
        } else if (PlatypusPlatform.setPlatformHomePath(platypusRuntimePath)) {
            return 0;
        } else {
            return 3;
        }
    }

}
