package practicaltest02var04.eim.systems.cs.pub.ro.practivaltest02var04.network;

import android.util.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import practicaltest02var04.eim.systems.cs.pub.ro.practivaltest02var04.general.Constants;
import practicaltest02var04.eim.systems.cs.pub.ro.practivaltest02var04.general.Utilities;


public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");
            String http_address = bufferedReader.readLine();
            if (http_address == null || http_address.isEmpty() ) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }
            HashMap<String, String> data = serverThread.getData();
            String result = null;
            if (data.containsKey(http_address)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                result = data.get(http_address);
                printWriter.println(result);
                printWriter.flush();
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpPost = new HttpGet(http_address);

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Log.d(Constants.TAG, http_address);
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }
                printWriter.println(pageSourceCode.toString());
                Log.d(Constants.TAG, pageSourceCode.toString());
                serverThread.setData(http_address, pageSourceCode.toString());
                printWriter.flush();

                }



        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (Exception
                jsonException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
