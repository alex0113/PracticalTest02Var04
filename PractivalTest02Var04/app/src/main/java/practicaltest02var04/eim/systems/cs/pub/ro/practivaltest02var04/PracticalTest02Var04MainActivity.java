package practicaltest02var04.eim.systems.cs.pub.ro.practivaltest02var04;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import practicaltest02var04.eim.systems.cs.pub.ro.practivaltest02var04.general.Constants;
import practicaltest02var04.eim.systems.cs.pub.ro.practivaltest02var04.network.ClientThread;
import practicaltest02var04.eim.systems.cs.pub.ro.practivaltest02var04.network.ServerThread;

public class PracticalTest02Var04MainActivity extends AppCompatActivity {
    private Button startButton, getButton;
    private EditText editPort, editAddress;
    private TextView resultText;
    private ServerThread serverThread = null;
    private ClientThread clientThread = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_var04_main);
        getButton = (Button)this.findViewById(R.id.get_button);
        startButton = (Button)this.findViewById(R.id.start_button);

        editAddress = (EditText)this.findViewById(R.id.edit_address);
        editPort =(EditText)this.findViewById(R.id.edit_port);

        resultText = (TextView)this.findViewById(R.id.text_result);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverPort = editPort.getText().toString();
                if (serverPort == null || serverPort.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                serverThread = new ServerThread(Integer.parseInt(serverPort));
                if (serverThread.getServerSocket() == null) {
                    Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                    return;
                }
                serverThread.start();
            }
        });
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientAddress = "127.0.0.1";
                String clientPort = editPort.getText().toString();
                if (clientAddress == null || clientAddress.isEmpty()
                        || clientPort == null || clientPort.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (serverThread == null || !serverThread.isAlive()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String address = editAddress.getText().toString();
                if (address == null || address.isEmpty()){
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                    return;
                }

                resultText.setText("waiting");

                clientThread = new ClientThread(
                        clientAddress, Integer.parseInt(clientPort), address, resultText);
                clientThread.start();
            }
        });

    }
}
