package org.hhautoresponder.config;

import io.github.cdimascio.dotenv.Dotenv;

public class HhApiConfig {

    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./hh-auto-responder")
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    public static final String CLIENT_ID = dotenv.get("CLIENT_ID");
    public static final String CLIENT_SECRET = dotenv.get("CLIENT_SECRET");
    public static final String REDIRECT_URI = dotenv.get("REDIRECT_URI");

}
