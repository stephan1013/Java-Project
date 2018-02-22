package client;

import org.junit.jupiter.api.Test;

public class clientTest {
    @Test
    public void cliTest() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                String[] s = new String[1];
                s[0] = "127.0.0.1";
                s[1] = "8080";
                client cli = new client();
                cli.main(s);
            } catch (Exception e) {

            }

        });

        thread.start();
        thread.sleep(5000);
    }

}

