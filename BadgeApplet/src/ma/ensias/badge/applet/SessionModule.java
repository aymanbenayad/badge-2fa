package ma.ensias.badge.applet;

public class SessionModule {
    
    private boolean isAuthenticated;
    private byte transactionLimit;

    public SessionModule() {
        isAuthenticated = false;
        transactionLimit = 0;
    }

    public void startSession() {
        isAuthenticated = true;
        transactionLimit = 5;
    }

    public void closeSession() {
        isAuthenticated = false;
        transactionLimit = 0;
    }

    public boolean isSessionActive() {
        if (!isAuthenticated) return false;
        if (transactionLimit > 0) {
            transactionLimit--;
            return true;
        } else {
            closeSession();
            return false;
        }
    }
}