import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ConversorDeMoedas {
    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/08753033ff1a1f5d6d08290d";

    public ConversorDeMoedas() {
    }

    public static void main(String[] args) {

        Scanner entrada = new Scanner(System.in);
        while (true) {
            exibeMenu();

            var opcao = entrada.nextInt();
            entrada.nextLine();

            switch (opcao) {
                case 1:
                    converteMoeda(entrada);
                    break;
                case 2:
                    listaMoeda();
                    break;
                case 3:
                    System.out.println("FINALIZANDO...");
                    entrada.close();
                    System.exit(0);
                default:
                    System.out.println("Digite uma opção válida");
            }
        }
    }

    private static void exibeMenu() {
        System.out.println("*****************************************");
        System.out.println("**        Conversor de moedas:         **");
        System.out.println("**   1º Converter as moedas            **");
        System.out.println("**   2º Exibir a lista de moedas       **");
        System.out.println("**   3º Encerrar:                      **");
        System.out.println("**        Escolha uma das opções:      **");
        System.out.println("*****************************************");
    }
    private static void converteMoeda(Scanner entrada) {
        System.out.println("Digite a moeda para conversão (ex: BRL, EUR, USD): ");
        var moedaInicial = entrada.nextLine().toUpperCase();

        System.out.println("Agora digite a moeda convertida (ex: JPY, AUD, CAD): ");
        var moedaConvertida = entrada.nextLine().toUpperCase();

        System.out.println("Coloque o valor a ser convertido: ");
        var valor = entrada.nextDouble();

        try {
            var apiURL = API_BASE_URL + "latest/" + moedaInicial;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiURL))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonElement jsonElement = new Gson().fromJson(response.body(), JsonElement.class);
                var taxaDeCambio =jsonElement.getAsJsonObject().getAsJsonObject("conversion_rates").get(moedaConvertida).getAsDouble();
                var valorConvertido = valor * taxaDeCambio;

                System.out.printf("%.2f %s = %.2f %s \n", valor , moedaInicial, valorConvertido, moedaConvertida);
            }else {
                System.out.println("Erro na solicitação: " + response.statusCode());
            }
        }catch (Exception e){
            System.out.println("Houve um erro ao processar a solicitação, tente novamente, por gentileza.");

        }

    }
    private static void listaMoeda(){
        try {
            var apiURL = API_BASE_URL + "latest/USD";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiURL))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200){
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonObject conversionRates = jsonResponse.getAsJsonObject("conversion_rates");

                System.out.println("Moedas disponíveis para conversão:");
                for (String code : conversionRates.keySet()) {
                    System.out.println(code);
                }
            }else {
                System.out.println("Erro na solicitação: " + response.statusCode());
            }

        } catch (Exception e){
            System.out.println("Houve um erro ao processar a solicitação, tente novamente, por gentileza.");
        }
    }



}