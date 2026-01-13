package controller;

import Users.SOCUSER;

public class Session {
    private static SOCUSER currentUser;

    public static SOCUSER getCurrentUser() { return currentUser; }
    public static void setCurrentUser(SOCUSER u) { currentUser = u; }
}
