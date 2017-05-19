package practicaltest02var04.eim.systems.cs.pub.ro.practivaltest02var04.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import practicaltest02var04.eim.systems.cs.pub.ro.practivaltest02var04.general.Constants;
import practicaltest02var04.eim.systems.cs.pub.ro.practivaltest02var04.general.Utilities;


public class ClientThread extends Thread {

    private String address;
    private int port;
    private String httpAddress;
    private String informationType;
    private TextView resultTextView;

    private Socket socket;

    public ClientThread(String address, int port, String http_address, TextView resultTextView) {
        this.address = address;
        this.port = port;
        this.httpAddress = http_address;
        this.resultTextView = resultTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(this.httpAddress);
            printWriter.flush();

            String weatherInformation;
            while ((weatherInformation = bufferedReader.readLine()) != null) {
                final String finalizedWeateherInformation = weatherInformation;
                resultTextView.post(new Runnable() {
                   @Override
                    public void run() {
                       resultTextView.setText(finalizedWeateherInformation);
                   }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
