package server;

import org.junit.jupiter.api.Test;

public class serverTest {

    @Test
    public void servTest() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                String[] s = new String[1];
                s[0] = "8080";
                server serv = new server(8080);
                serv.run();
            } catch (Exception e) {

            }

        });

        thread.start();
        thread.sleep(5000);
    }

}
