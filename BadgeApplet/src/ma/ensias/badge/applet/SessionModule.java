package ma.ensias.badge.applet;

public class SessionModule {
    
    private boolean isAuthenticated;
    private short loginTime;

    public SessionModule() {
        isAuthenticated = false;
        loginTime = 0;
    }

    public void startSession(short time) {
        isAuthenticated = true;
        loginTime = time;
    }

    public void closeSession() {
        isAuthenticated = false;
        loginTime = 0;
    }

    public boolean isSessionActive() {
        return isAuthenticated;
    }
}