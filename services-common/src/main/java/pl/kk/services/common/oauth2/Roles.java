package pl.kk.services.common.oauth2;

public class Roles {
    public static final String USER = "#oauth2.client || hasAuthority('USER')";
    public static final String ADMIN = "#oauth2.client || hasAuthority('ADMIN')";
}
