package ma.project.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ma.project.stubs.Bank;
import ma.project.stubs.BankServiceGrpc;

public class Client1 {
    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5555)
                .usePlaintext()
                .build();

        BankServiceGrpc.BankServiceBlockingStub blockingStub = BankServiceGrpc.newBlockingStub(channel);

        Bank.ConvertCurrencyRequest request = Bank.ConvertCurrencyRequest.newBuilder()
                .setCurrencyFrom("MAD")
                .setCurrencyTo("USD")
                .setAmount(1000)
                .build();

        Bank.ConvertCurrencyResponse response = blockingStub.convert(request);

        System.out.println("Response: " + response);

        channel.shutdown();
    }
}
