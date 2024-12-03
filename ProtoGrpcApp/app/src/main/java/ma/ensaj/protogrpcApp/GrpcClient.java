package ma.ensaj.protogrpcApp;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import ma.project.stubs.Bank;
import ma.project.stubs.BankServiceGrpc;

public class GrpcClient {

    private ManagedChannel channel;
    private BankServiceGrpc.BankServiceBlockingStub stub;

    public GrpcClient(String host, int port) {
        try {
            channel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    .keepAliveTimeout(30, TimeUnit.SECONDS)
                    .keepAliveTime(30, TimeUnit.SECONDS)
                    .build();

            stub = BankServiceGrpc.newBlockingStub(channel);
        } catch (Exception e) {
            Log.e("GrpcClient Error", "Erreur lors de la configuration du client gRPC : " + e.getMessage(), e);
            throw new RuntimeException("Échec de la configuration du client gRPC", e);
        }
    }

    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        Bank.ConvertCurrencyRequest request = Bank.ConvertCurrencyRequest.newBuilder()
                .setAmount(amount)
                .setCurrencyFrom(fromCurrency)
                .setCurrencyTo(toCurrency)
                .build();

        try {
            Bank.ConvertCurrencyResponse response = stub.convert(request);
            return response.getResult();
        } catch (StatusRuntimeException e) {
            Log.e("GrpcClient Error", "Erreur lors de l'appel gRPC : " + e.getMessage(), e);
            throw e;
        }
    }

    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}
