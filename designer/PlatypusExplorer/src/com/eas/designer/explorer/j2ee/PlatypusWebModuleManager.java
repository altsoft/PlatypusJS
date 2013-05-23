/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee;

import com.eas.client.ClientConstants;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.explorer.j2ee.dd.AppListener;
import com.eas.designer.explorer.j2ee.dd.AuthConstraint;
import com.eas.designer.explorer.j2ee.dd.ContextParam;
import com.eas.designer.explorer.j2ee.dd.FormLoginConfig;
import com.eas.designer.explorer.j2ee.dd.LoginConfig;
import com.eas.designer.explorer.j2ee.dd.MultipartConfig;
import com.eas.designer.explorer.j2ee.dd.ResourceRef;
import com.eas.designer.explorer.j2ee.dd.SecurityConstraint;
import com.eas.designer.explorer.j2ee.dd.SecurityRole;
import com.eas.designer.explorer.j2ee.dd.Servlet;
import com.eas.designer.explorer.j2ee.dd.ServletMapping;
import com.eas.designer.explorer.j2ee.dd.WebApplication;
import com.eas.designer.explorer.j2ee.dd.WebResourceCollection;
import com.eas.designer.explorer.platform.EmptyPlatformHomePathException;
import com.eas.designer.explorer.platform.PlatypusPlatform;
import com.eas.designer.explorer.project.PlatypusProject;
import com.eas.util.FileUtils;
import com.eas.xml.dom.XmlDom2String;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * A tool to prepare and deploy the Platypus web module.
 *
 * @author vv
 */
public class PlatypusWebModuleManager {

    public static final String WAR_FILE_NAME = "PlatypusServlet.war"; //NOI18N
    public static final String PLATYPUS_SERVLET_URL_PATTERN = "/application/*"; //NOI18N
    public static final String WEB_DESCRIPTOR_FILE_NAME = "web.xml"; //NOI18N
    public static final String PLATYPUS_WEB_CLIENT_DIR_NAME = "pwc"; //NOI18N
    public static final String J2EE_RESOURCES_PACKAGE = "/com/eas/designer/explorer/j2ee/resources/"; //NOI18N
    public static final String START_PAGE_FILE_NAME = "application-start.html"; //NOI18N
    public static final String LOGIN_PAGE_FILE_NAME = "login.html"; //NOI18N
    public static final String LOGIN_FAIL_PAGE_FILE_NAME = "login-failed.html"; //NOI18N
    public static final String START_JS_FILE_NAME = "start.js"; //NOI18N
    public static final String WEB_XML_FILE_NAME = "web.xml"; //NOI18N
    public static final String SERVLET_BEAN_NAME = "Servlet"; //NOI18N
    public static final String MULTIPART_CONFIG_BEAN_NAME = "MultipartConfig"; //NOI18N
    public static final String SERVLET_MAPPING_BEAN_NAME = "ServletMapping"; //NOI18N
    public static final String PLATYPUS_SERVLET_NAME = "PlatypusServlet"; //NOI18N
    public static final String PLATYPUS_SERVLET_CLASS = "com.eas.server.httpservlet.PlatypusHttpServlet";//NOI18N
    public static final String WEB_APP_LISTENER_CLASS = "com.eas.server.httpservlet.PlatypusSessionsSynchonizer";//NOI18N
    public static final String DATASOURCE_CLASS = "javax.sql.DataSource"; //NOI18N
    public static final String CONTAIER_RESOURCE_SECURITY_TYPE = "Container"; //NOI18N
    public static final String PLATYPUS_WEB_RESOURCE_NAME = "platypus"; //NOI18N
    public static final String ANY_SIGNED_USER_ROLE = "*"; //NOI18N
    public static final String FORM_AUTH_METHOD = "FORM"; //NOI18N
    public static final long MULTIPART_MAX_FILE_SIZE = 2097152;
    public static final long MULTIPART_MAX_REQUEST_SIZE = 2165824;
    public static final long MULTIPART_MAX_FILE_THRESHOLD = 1048576;
    protected final String START_JS_FILE_TEMPLATE = "" //NOI18N
            + "/**\n"//NOI18N
            + " * Do not edit this file manualy, it will be overwritten by Platypus Application Designer.\n"//NOI18N
            + " */\n"//NOI18N
            + "if (!platypus) {\n"//NOI18N
            + "\tvar platypus = {}\n"//NOI18N
            + "}\n"//NOI18N
            + "platypus.ready = function() {\n"//NOI18N
            + "\trequire(['%s'], function(){\n"//NOI18N
            + "\t\tvar f = new %s()\n"//NOI18N
            + "\t\tf.show()\n"//NOI18N
            + "\t})\n"//NOI18N
            + "}\n";//NOI18N
    protected final PlatypusProject project;
    protected final FileObject projectDir;
    protected FileObject webAppDir;
    protected FileObject webInfDir;
    protected FileObject metaInfDir;
    protected FileObject publicDir;

    public PlatypusWebModuleManager(PlatypusProject aProject) {
        project = aProject;
        projectDir = aProject.getProjectDirectory();
    }

    /**
     * Runs the web application.
     *
     * @param isDebug true if debug mode to be activated
     * @return URL to open in browser
     *
     */
    public String run(String appElementId, boolean isDebug) {
        PlatypusWebModule webModule = project.getLookup().lookup(PlatypusWebModule.class);
        String webAppRunUrl = null;
        if (webModule != null) {
            if (webModule.getServerID() == null || webModule.getServerID().isEmpty()) {
                project.getOutputWindowIO().getOut().println("Application server is not set. Check J2EE Server settings at Project's properties.");
                return null;
            }
            if (webModule.getUrl() == null || webModule.getUrl().isEmpty()) {
                project.getOutputWindowIO().getOut().println("J2EE Server context is not configured for the project.");
                return null;
            }
            try {
                prepareWebApplication();
                setupWebApplication(webModule);
                setStartApplicationElement(appElementId);
                webAppRunUrl = Deployment.getDefault().deploy(webModule, Deployment.Mode.RUN, null, START_PAGE_FILE_NAME, false);
                String deployResultMessage = "Web application deployed.";
                Logger.getLogger(PlatypusWebModuleManager.class.getName()).log(Level.INFO, deployResultMessage);
                project.getOutputWindowIO().getOut().println(deployResultMessage);

            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        } else {
            throw new IllegalStateException("J2eeModuleProvider instance should be in the project's lookup.");
        }
        return webAppRunUrl;
    }

    /**
     * Creates an web application skeleton if not created yet.
     */
    protected void prepareWebApplication() throws Exception {
        webAppDir = createFolderIfNotExists(projectDir, PlatypusWebModule.WEB_DIRECTORY);
        webInfDir = createFolderIfNotExists(webAppDir, PlatypusWebModule.WEB_INF_DIRECTORY);
        metaInfDir = createFolderIfNotExists(webAppDir, PlatypusWebModule.META_INF_DIRECTORY);
        publicDir = createFolderIfNotExists(webAppDir, PlatypusWebModule.PUBLIC_DIRECTORY);
        prepareJars();
        preparePlatypusWebClient();
        prepareResources();
    }

    private void prepareJars() throws Exception {
        FileObject libsDir = webInfDir.getFileObject(PlatypusWebModule.LIB_DIRECTORY_NAME);
        if (libsDir == null) {
            libsDir = webInfDir.createFolder(PlatypusWebModule.LIB_DIRECTORY_NAME);
        }
        if (libsDir.getChildren().length == 0) {
            copyBinJars(libsDir);
            copyLibJars(libsDir);
        }
    }

    private void preparePlatypusWebClient() throws IOException, EmptyPlatformHomePathException {
        FileObject pwcDir = webAppDir.getFileObject(PLATYPUS_WEB_CLIENT_DIR_NAME);
        if (pwcDir == null) {
            pwcDir = webAppDir.createFolder(PLATYPUS_WEB_CLIENT_DIR_NAME);
        }
        FileObject pwcSourceDir = FileUtil.toFileObject(PlatypusPlatform.getPlatformBinDirectory()).getFileObject(PLATYPUS_WEB_CLIENT_DIR_NAME);
        if (pwcSourceDir == null) {
            throw new IllegalStateException(String.format("Platypus web client is not found at %s.", PlatypusPlatform.getPlatformBinDirectory().getAbsolutePath()));
        }
        if (!pwcSourceDir.isFolder()) {
            throw new IllegalStateException("Platypus web client must be a directory.");
        }
        if (pwcDir.getChildren().length == 0) {
            copyContent(pwcSourceDir, pwcDir);
        }
    }

    private void prepareResources() throws IOException {
        copyResourceIfNotExists(START_PAGE_FILE_NAME);
        copyResourceIfNotExists(LOGIN_PAGE_FILE_NAME);
        copyResourceIfNotExists(LOGIN_FAIL_PAGE_FILE_NAME);
    }

    private void copyResourceIfNotExists(String filePath) throws IOException {
        FileObject fo = webAppDir.getFileObject(filePath);
        if (fo == null) {
            fo = webAppDir.createData(filePath);
            try (InputStream is = PlatypusWebModuleManager.class.getResourceAsStream(J2EE_RESOURCES_PACKAGE + filePath);
                    OutputStream os = fo.getOutputStream()) {
                FileUtil.copy(is, os);
            }
        }
    }

    /**
     * Recursively copies directory's content.
     *
     * @param source directory
     * @param destination directory
     * @throws IOException if some I/O problem occurred.
     */
    protected void copyContent(FileObject sourceDir, FileObject targetDir) throws IOException {
        assert sourceDir.isFolder() && targetDir.isFolder();
        for (FileObject childFile : sourceDir.getChildren()) {
            if (childFile.isFolder()) {
                FileObject targetFile = targetDir.createFolder(childFile.getNameExt());
                copyContent(childFile, targetFile);
            } else {
                childFile.copy(targetDir, childFile.getName(), childFile.getExt());
            }
        }
    }

    private FileObject createFolderIfNotExists(FileObject dir, String name) throws IOException {
        FileObject fo = dir.getFileObject(name);
        if (fo == null) {
            fo = dir.createFolder(name);
        }
        return fo;
    }

    /**
     * Sets up an web application.
     *
     * @param aJmp Web Module
     */
    protected void setupWebApplication(J2eeModuleProvider aJmp) throws Exception {
        WebAppManager webAppConfigurator = WebAppManagerFactory.getInstance().createWebAppManager(project, aJmp);
        if (webAppConfigurator != null) {
            webAppConfigurator.deployJdbcDrivers();
            webAppConfigurator.configure();
        } else {
            String errorMessage = String.format("Web application configuration is not supported for application server: %s", aJmp.getServerID());
            Logger.getLogger(PlatypusWebModuleManager.class.getName()).log(Level.WARNING, errorMessage);
            project.getOutputWindowIO().getErr().println(errorMessage);
        }
        configureDeploymentDescriptor();
    }

    private void configureDeploymentDescriptor() throws Exception {
        WebApplication wa = new WebApplication();
        configureParams(wa);
        wa.addAppListener(new AppListener(WEB_APP_LISTENER_CLASS));
        configureServlet(wa);
        configureDatasource(wa);
        if (project.getSettings().isWebSecurityEnabled()) {
            configureSecurity(wa);
        }
        FileObject webXml = webInfDir.getFileObject(WEB_XML_FILE_NAME);
        if (webXml == null) {
            webXml = webInfDir.createData(WEB_XML_FILE_NAME);
        }
        FileUtils.writeString(FileUtil.toFile(webXml), XmlDom2String.transform(wa.toDocument()), PlatypusUtils.COMMON_ENCODING_NAME);
    }

    protected void setStartApplicationElement(String appElementId) throws IOException {
        if (appElementId != null && !appElementId.isEmpty()) {
            FileObject startJs = webAppDir.getFileObject(START_JS_FILE_NAME);
            if (startJs == null) {
                startJs = webAppDir.createData(START_JS_FILE_NAME);
            }
            String starupScript = String.format(START_JS_FILE_TEMPLATE, appElementId, appElementId);
            FileUtils.writeString(FileUtil.toFile(startJs), starupScript, PlatypusUtils.COMMON_ENCODING_NAME);

        } else {
            throw new IllegalStateException("appElementId is null or empty.");
        }
    }

    private void configureParams(WebApplication wa) throws Exception {
        if (!project.getSettings().isDbAppSources()) {
            wa.addInitParam(new ContextParam(ClientConstants.APP_PATH_CMD_PROP_NAME1, project.getProjectDirectory().getPath()));
        }
        wa.addInitParam(new ContextParam(ClientConstants.DB_CONNECTION_URL_PROP_NAME, PlatypusWebModule.MAIN_DATASOURCE_NAME));
        DbConnectionSettings dbSettings = project.getSettings().getAppSettings().getDbSettings();
        String dbConnectionSchema = dbSettings.getInfo().getProperty(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME);
        if (dbConnectionSchema != null && !dbConnectionSchema.isEmpty()) {
            wa.addInitParam(new ContextParam(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME, dbConnectionSchema));
        }
        String dialect = GeneralResourceProvider.constructPropertiesByDbConnectionSettings(dbSettings).getProperty(ClientConstants.DB_CONNECTION_DIALECT_PROP_NAME);
        wa.addInitParam(new ContextParam(ClientConstants.DB_CONNECTION_DIALECT_PROP_NAME, dialect));
    }

    private void configureServlet(WebApplication wa) {
        Servlet platypusServlet = new Servlet(PLATYPUS_SERVLET_NAME, PLATYPUS_SERVLET_CLASS);
        MultipartConfig multiPartConfig = new MultipartConfig();
        multiPartConfig.setLocation(publicDir.getPath());
        multiPartConfig.setMaxFileSize(Long.toString(MULTIPART_MAX_FILE_SIZE));
        multiPartConfig.setMaxRequestSize(Long.toString(MULTIPART_MAX_REQUEST_SIZE));
        multiPartConfig.setFileSizeThreshold(Long.toString(MULTIPART_MAX_FILE_THRESHOLD));
        platypusServlet.setMultipartConfig(multiPartConfig);
        wa.addServlet(platypusServlet);
        wa.addServletMapping(new ServletMapping(PLATYPUS_SERVLET_NAME, PLATYPUS_SERVLET_URL_PATTERN));
    }

    private void configureDatasource(WebApplication wa) {
        ResourceRef resourceRef = new ResourceRef(PlatypusWebModule.MAIN_DATASOURCE_NAME, DATASOURCE_CLASS, CONTAIER_RESOURCE_SECURITY_TYPE);
        resourceRef.setDescription("Main database connection"); //NOI18N
        wa.addResourceRef(resourceRef);
    }

    private void configureSecurity(WebApplication wa) {
        SecurityConstraint sc = new SecurityConstraint();
        WebResourceCollection wrc = new WebResourceCollection(PLATYPUS_WEB_RESOURCE_NAME);
        wrc.setUrlPattern("/" + START_PAGE_FILE_NAME);//NOI18N
        sc.addWebResourceCollection(wrc);
        AuthConstraint ac = new AuthConstraint(ANY_SIGNED_USER_ROLE);
        LoginConfig lc = new LoginConfig();
        sc.setAuthConstraint(ac);
        wa.setSecurityConstraint(sc);
        lc.setAuthMethod(FORM_AUTH_METHOD);
        lc.setFormLoginConfig(new FormLoginConfig("/" + LOGIN_PAGE_FILE_NAME, "/" + LOGIN_FAIL_PAGE_FILE_NAME));//NOI18N
        wa.addSecurityRole(new SecurityRole(ANY_SIGNED_USER_ROLE));
        wa.setLoginConfig(lc);

    }

    private void copyBinJars(FileObject libsDir) throws EmptyPlatformHomePathException, IOException {
        FileObject platformBinDir = FileUtil.toFileObject(PlatypusPlatform.getPlatformBinDirectory());
        for (FileObject fo : platformBinDir.getChildren()) {
            if (fo.isData() && PlatypusPlatform.JAR_FILE_EXTENSION.equalsIgnoreCase(fo.getExt())) {
                FileUtil.copyFile(fo, libsDir, fo.getName());
            }
        }
    }

    private void copyLibJars(FileObject libsDir) throws Exception, EmptyPlatformHomePathException, IOException {
        Set<File> jdbcDrivers = new HashSet<>();
        for (String clazz : DbConnectionSettings.readDrivers().values()) {
            File jdbcDriver = PlatypusPlatform.findThirdpartyJar(clazz);
            if (jdbcDriver != null) {
                jdbcDrivers.add(jdbcDriver);
            }
        }
        FileObject platformLibDir = FileUtil.toFileObject(PlatypusPlatform.getPlatformLibDirectory());
        Enumeration<? extends FileObject> e = platformLibDir.getChildren(true);
        while (e.hasMoreElements()) {
            FileObject fo = e.nextElement();
            if (PlatypusPlatform.JAR_FILE_EXTENSION.equalsIgnoreCase(fo.getExt())
                    && !jdbcDrivers.contains(FileUtil.toFile(fo))) {
                FileUtil.copyFile(fo, libsDir, fo.getName());
            }
        }
    }
}
