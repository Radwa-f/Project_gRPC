package ma.ensaj.protogrpcApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText amountInput, sourceCurrencyInput, targetCurrencyInput;
    private TextView convertedAmountResult;
    private Button conversionButton;
    private GrpcClient grpcClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountInput = findViewById(R.id.amount_input);
        sourceCurrencyInput = findViewById(R.id.source_currency_input);
        targetCurrencyInput = findViewById(R.id.target_currency_input);
        convertedAmountResult = findViewById(R.id.converted_amount_result);
        conversionButton = findViewById(R.id.convert_button);

        testServerConnection();

        // Initialize the gRPC client
        grpcClient = new GrpcClient("192.168.1.2", 5555);

        conversionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertCurrency();
            }
        });
    }

    private void testServerConnection() {
        new Thread(() -> {
            try (Socket socket = new Socket("192.168.1.2", 5555)) {
                Log.d("Network Test", "Connexion réussie au serveur !");
            } catch (Exception e) {
                Log.e("Network Test", "Échec de la connexion au serveur : " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(this, "Impossible d'atteindre le serveur", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void convertCurrency() {
        String amounts = amountInput.getText().toString();
        String currencyFrom = sourceCurrencyInput.getText().toString();
        String currencyTo = targetCurrencyInput.getText().toString();

        if (amounts.isEmpty() || currencyFrom.isEmpty() || currencyTo.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amounts);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Montant invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                double result = grpcClient.convertCurrency(amount, currencyFrom, currencyTo);

                runOnUiThread(() -> convertedAmountResult.setText(" " + result));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Une erreur s'est produite lors de la conversion", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (grpcClient != null) {
            grpcClient.shutdown();
        }
    }
}
