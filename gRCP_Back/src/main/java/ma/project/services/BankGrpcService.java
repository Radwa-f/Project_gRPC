package ma.project.services;

import io.grpc.stub.StreamObserver;
import ma.project.stubs.Bank;
import ma.project.stubs.BankServiceGrpc;


public class BankGrpcService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void convert(Bank.ConvertCurrencyRequest request, StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {

        System.out.println("Command started on port 5555...");

        String currencyFrom = request.getCurrencyFrom();
        String currencyTo = request.getCurrencyTo();
        double amount = request.getAmount();

        double conversionRate = 11.4;
        double result = amount * conversionRate;

        System.out.println("Received request: " + request);

        Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                .setCurrencyFrom(currencyFrom)
                .setCurrencyTo(currencyTo)
                .setAmount(amount)
                .setResult(result)
                .build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }
}
